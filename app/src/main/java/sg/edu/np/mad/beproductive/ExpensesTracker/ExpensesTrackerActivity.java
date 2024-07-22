package sg.edu.np.mad.beproductive.ExpensesTracker;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.ToDoListPage.TodoList;
import sg.edu.np.mad.beproductive.User;


public class ExpensesTrackerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpensesAdapter adapter;
    private List<ExpensesModel> expensesList;

    private ViewPager2 viewPager;
    private ExpensesPagerAdapter expPagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots;

    private DatabaseReference userRef;

    private boolean isBalanceSetupComplete = false;
    private boolean isBudgetSetupComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_tracker);

        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);

        // Initialize the database reference
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("expenses");

        loadExpensesFromFirebase();
        // Check if the setup is complete
        checkBalanceSetupComplete();
        checkBudgetSetup();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        expensesList = new ArrayList<>();
        // expensesList.add(new ExpensesModel("Food", "11/06/2024 11:30", "$10.00", R.drawable.food_icon));

        adapter = new ExpensesAdapter(expensesList);
        recyclerView.setAdapter(adapter);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        // Initialize dots
        initializeDots(2); // Update this number according to the number of pages in ViewPager2

        // Add page change listener to update dots
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });

        checkBalanceSetupComplete();
        FloatingActionButton addTranscFab = findViewById(R.id.addTransactionFAB);
        addTranscFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });
        // Load the SetBudgetFragment initially
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.budgetFragmentContainer, new SetBudgetFragment())
                    .commit();
        }


        ImageView backBtn = findViewById(R.id.expensesBackbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recievingEnd = getIntent();
                int id = recievingEnd.getIntExtra("ID", 0);
                String username = recievingEnd.getStringExtra("Username");
                String password = recievingEnd.getStringExtra("Password");
                String email = recievingEnd.getStringExtra("Email");

                User user0 = new User(username, password, email);
                user0.setId(id);
                Bundle extras = new Bundle();
                extras.putInt("ID", user0.getId());
                extras.putString("Username", user0.getName());
                extras.putString("Password", user0.getPassword());
                extras.putString("Email", user0.getEmail());
                Intent intent = new Intent(ExpensesTrackerActivity.this, HomeMenu.class);
                intent.putExtras(extras);
                startActivity(intent);
                finish(); // Call this if you don't want to keep the current activity in the back stack
            }
        });


//        budgetTextView = findViewById(R.id.budgetTextView);
//        spentTextView = findViewById(R.id.spentTextView);
//        budgetProgressBar = findViewById(R.id.budgetProgressBar);

//        budgetTextView.setText("Budget: $" + budget);
//        budgetProgressBar.setMax((int) budget);
//        updateBudgetProgress();
    }
    private void checkBalanceSetupComplete() {
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isSetupComplete = snapshot.exists();
                setupViewPager(isSetupComplete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesTrackerActivity.this, "Error checking setup status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager(boolean isBalanceSetupComplete) {
        viewPager = findViewById(R.id.viewPager);

        // Initialize the pager adapter with the setup status and expenses list
        ExpensesPagerAdapter expensesPagerAdapter = new ExpensesPagerAdapter(this, isBalanceSetupComplete, expensesList);
        viewPager.setAdapter(expensesPagerAdapter);

        // Initialize dots for view pager if necessary
        initializeDots(isBalanceSetupComplete ? 2 : 1);

        // Add page change listener to update dots
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });
    }

    private void checkBudgetSetup() {
        userRef.child("budget").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isBudgetSetupComplete = snapshot.exists();
                updateBudgetFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesTrackerActivity.this, "Error checking budget status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateBudgetFragment() {
        Fragment fragment = isBudgetSetupComplete ? new BudgetProgressFragment() : new SetBudgetFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.budgetFragmentContainer, fragment)
                .commit();
    }



    private void initializeDots(int count) {
        dots = new ArrayList<>();
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.dot_inactive); // Replace with your dot drawable
            dotsLayout.addView(dot);
            dots.add(dot);
        }
        if (dots.size() > 0) {
            dots.get(0).setImageResource(R.drawable.dot_active); // Replace with your active dot drawable
        }
    }

    private void updateDots(int position) {
        for (int i = 0; i < dots.size(); i++) {
            if (i == position) {
                dots.get(i).setImageResource(R.drawable.dot_active); // Replace with your active dot drawable
            } else {
                dots.get(i).setImageResource(R.drawable.dot_inactive); // Replace with your dot drawable
            }
        }
    }

//    private void updateBudgetProgress() {
//        budgetProgressBar.setProgress((int) totalSpent);
//        spentTextView.setText("Spent: $" + totalSpent);
////        float progressPercentage = (totalSpent / budget) * 100;
//    }

    private Map<String, Float> calculateExpensesByCategory() {
        Map<String, Float> categoryTotals = new HashMap<>();

        for (ExpensesModel expense : expensesList) {
            String category = expense.getCategory();
            float amount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));

            if (categoryTotals.containsKey(category)) {
                categoryTotals.put(category, categoryTotals.get(category) + amount);
            } else {
                categoryTotals.put(category, amount);
            }
        }

        return categoryTotals;
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_transaction, null);
        builder.setView(dialogView);

        final Spinner spinnerCategory = dialogView.findViewById(R.id.transactionSpinnerCategory);
        final EditText etPrice = dialogView.findViewById(R.id.addPrice);
        Button btnAddExpense = dialogView.findViewById(R.id.btnAddExpense);

        final AlertDialog dialog = builder.create();
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = spinnerCategory.getSelectedItem().toString();
                String price = etPrice.getText().toString();
                String dateTime = getCurrentDateTime(); // Use current date-time in a real app
                int categoryIcon = getCategoryIcon(category);
                // Add the new expense to the list

//                float amount = Float.parseFloat(price);
                expensesList.add(new ExpensesModel(category, dateTime, "$" + price, categoryIcon)); // Update the icon as needed
//                updateBudgetProgress();
                saveExpenseToFirebase(new ExpensesModel(category, dateTime, "$" + price, categoryIcon));
                adapter.notifyDataSetChanged();
                updateGraphFragment();
                refreshBalance();
                saveTotalSpendings();
                dialog.dismiss();
            }

        });

        dialog.show();
    }
    private void saveExpenseToFirebase(ExpensesModel expense) {
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);

        // Calculate new balance
        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float currentBalance = snapshot.getValue(Float.class);
                    float expenseAmount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));
                    float newBalance = currentBalance - expenseAmount;

                    // Update balance in Firebase
                    userRef.child("balance").setValue(newBalance).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ExpensesTrackerActivity.this, "Expense saved and balance updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ExpensesTrackerActivity", "Failed to update balance", task.getException());
                            Toast.makeText(ExpensesTrackerActivity.this, "Failed to update balance!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d("ExpensesTrackerActivity", "No balance found for user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ExpensesTrackerActivity", "Failed to get balance: " + error.getMessage(), error.toException());
            }
        });

        // Save the expense
        DatabaseReference expensesRef = userRef.child("expenses");
        expensesRef.push().setValue(expense).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ExpensesTrackerActivity.this, "Expense saved!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ExpensesTrackerActivity", "Failed to save expense", task.getException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to save expense!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void refreshBalance() {
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float balance = snapshot.getValue(Float.class);
                    // Update UI with new balance
                    TextView balanceTextView = findViewById(R.id.accountBalanceTextView);
                    balanceTextView.setText("$" + balance);
                } else {
                    Log.d("ExpensesTrackerActivity", "No balance found for user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ExpensesTrackerActivity", "Failed to get balance: " + error.getMessage(), error.toException());
            }
        });
    }
    private void saveTotalSpendings() {
        float totalSpendings = calculateTotalSpendings();

        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);

        userRef.child("spendings").setValue(totalSpendings).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ExpensesTrackerActivity.this, "Total spendings updated!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ExpensesTrackerActivity", "Failed to update spendings", task.getException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to update spendings!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private float calculateTotalSpendings() {
        float total = 0;
        for (ExpensesModel expense : expensesList) {
            float amount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));
            total += amount;
        }
        return total;
    }


    private void loadExpensesFromFirebase() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expensesList.clear();  // Clear the existing list to avoid duplication

                if (snapshot.exists()) {
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        ExpensesModel expense = expenseSnapshot.getValue(ExpensesModel.class);
                        if (expense != null) {
                            // Optional: Validate or convert data as needed
                            expensesList.add(expense);
                        } else {
                            Log.d("ExpensesTrackerActivity", "Expense data is null for snapshot: " + expenseSnapshot.getKey());
                        }
                    }
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged();
                    // Update the graph or budget progress as needed
                    updateGraphFragment();
                } else {
                    // Handle the case where there are no expenses
                    Log.d("ExpensesTrackerActivity", "No expenses found.");
                    Toast.makeText(ExpensesTrackerActivity.this, "No expenses recorded.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("ExpensesTrackerActivity", "Failed to load expenses: " + error.getMessage(), error.toException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to load expenses. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateGraphFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        GraphFragment graphFragment = (GraphFragment) fragmentManager.findFragmentByTag("f1");

        if (graphFragment != null && graphFragment.isAdded()) {
            Map<String, Float> categoryTotals = calculateExpensesByCategory();
            graphFragment.updatePieChart(categoryTotals);
        } else {
            Log.d("ExpensesTrackerActivity", "GraphFragment is not yet added or is null");
        }
    }

    private int getCategoryIcon(String category) {
        switch (category) {
            case "Dining":
                return R.drawable.dining_icon;
            case "Transport":
                return R.drawable.transport_icon;
            case "Entertainment":
                return R.drawable.entertainment_icon;
            case "Allowance":
                return R.drawable.allowance_icon;
            case "Shopping":
                return R.drawable.shopping_icon;
            case "Stationery":
                return R.drawable.stationery_icon;
            case "Books":
                return R.drawable.books_icon;
            case "Beverages":
                return R.drawable.beverages_icon;
            case "Gifts":
                return R.drawable.gifts_icon;
        }
        return 0;
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(calendar.getTime());
    }
}
