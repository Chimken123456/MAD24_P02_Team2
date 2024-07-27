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
import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private Calendar currentStartDate, currentEndDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private TextView dateRangeTextView;
    private ImageView prevWeekButton, nextWeekButton;

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

        // Initialize UI components
        checkBalanceSetupComplete();
        checkBudgetSetup();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dateRangeTextView = findViewById(R.id.dateRangeTextView);
        prevWeekButton = findViewById(R.id.prevWeekButton);
        nextWeekButton = findViewById(R.id.nextWeekButton);
        expensesList = new ArrayList<>();
        adapter = new ExpensesAdapter(expensesList);
        recyclerView.setAdapter(adapter);
        setWeekDates(Calendar.getInstance());
        updateDateRangeText();

        prevWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPreviousWeek();
            }
        });

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextWeek();
            }
        });

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        expPagerAdapter = new ExpensesPagerAdapter(this, isBalanceSetupComplete, expensesList);
        viewPager.setAdapter(expPagerAdapter);
        initializeDots(2); // Adjust the number of dots as needed

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });

        FloatingActionButton addTranscFab = findViewById(R.id.addTransactionFAB);
        addTranscFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

        // Fragment transactions
        if (savedInstanceState == null) {
            // Start a new transaction for adding fragments
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Transaction for adding GraphFragment and AccountDetailsFragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Add GraphFragment
            GraphFragment graphFragment = new GraphFragment();
            transaction.add(R.id.graphfragment_container, graphFragment, "GRAPH_FRAGMENT_TAG");

            // Add AccountDetailsFragment
            AccountDetailsFragment accountDetailsFragment = new AccountDetailsFragment();
            transaction.add(R.id.accDetailsFragmentContainer, accountDetailsFragment, "ACCOUNT_DETAILS_TAG");

            // Start another transaction for replacing with SetBudgetFragment
            FragmentTransaction replaceTransaction = fragmentManager.beginTransaction();
            SetBudgetFragment setBudgetFragment = new SetBudgetFragment();
            replaceTransaction.replace(R.id.budgetFragmentContainer, setBudgetFragment, "SET_BUDGET_TAG");
            replaceTransaction.commit();
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
    }
//    public void onBalanceSetup() {
//        // Update the state and reload the adapter
//        isBalanceSetupComplete = true;
//        expPagerAdapter = new ExpensesPagerAdapter(this, isBalanceSetupComplete, expensesList);
//        viewPager.setAdapter(expPagerAdapter);
//    }


    private void setWeekDates(Calendar calendar) {
        // Set the calendar to the start of the current week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        currentStartDate = (Calendar) calendar.clone();

        // Set the calendar to the end of the current week (Sunday)
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Move to the next week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        currentEndDate = (Calendar) calendar.clone();

        // Update the TextView with the date range
        updateDateRangeText();
    }

    private float calculateTotalSpentForWeek(Date startDate, Date endDate) {
        float totalSpent = 0;
        List<ExpensesModel> weeklyExpenses = filterExpensesByDateRange(startDate, endDate);

        for (ExpensesModel expense : weeklyExpenses) {
            if (!"Allowance".equals(expense.getCategory())) {
                try {
                    float amount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));
                    totalSpent += amount;
                } catch (NumberFormatException e) {
                    Log.e("ExpensesTrackerActivity", "Error parsing price: " + expense.getPrice(), e);
                }
            }
        }
        return totalSpent;
    }



    private void updateBudgetFragmentWithTotalSpent() {
        float totalSpent = calculateTotalSpentForWeek(currentStartDate.getTime(), currentEndDate.getTime());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.budgetFragmentContainer);

        if (fragment instanceof BudgetProgressFragment) {
            BudgetProgressFragment budgetProgressFragment = (BudgetProgressFragment) fragment;
            budgetProgressFragment.updateSpentTextView(totalSpent);
            budgetProgressFragment.updateProgressBarWithTotalSpent(totalSpent);
        } else {
            Log.e("ExpensesTrackerActivity", "Expected BudgetProgressFragment but found " + (fragment != null ? fragment.getClass().getSimpleName() : "null"));
        }
    }



    private void updateDateRangeText() {
        String startDate = dateFormat.format(currentStartDate.getTime());
        String endDate = dateFormat.format(currentEndDate.getTime());
        dateRangeTextView.setText(startDate + " - " + endDate);
        updateRecyclerViewWithFilteredData(currentStartDate.getTime(), currentEndDate.getTime());
        updateGraphFragmentWithDateRange(currentStartDate, currentEndDate);
        updateBudgetFragmentWithTotalSpent();
        // TODO: Load and display expenses based on the current week
    }
    private List<ExpensesModel> filterExpensesByDateRange(Date startDate, Date endDate) {
        List<ExpensesModel> filteredList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        if (expensesList == null) {
            Log.w("ExpensesTrackerActivity", "expensesList is null");
            return filteredList; // return empty list
        }

        Log.d("ExpensesTrackerActivity", "Filtering expenses between " + startDate + " and " + endDate);
        for (ExpensesModel expense : expensesList) {
            try {
                Date expenseDate = dateFormat.parse(expense.getDateTime());
                if (expenseDate != null && !expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                    filteredList.add(expense);
                }
            } catch (ParseException e) {
                Log.e("ExpensesTrackerActivity", "Error parsing date for expense: " + expense.getDateTime(), e);
            }
        }

        return filteredList; // Return the filtered list
    }
    private void updateGraphFragmentWithDateRange(Calendar startCalendar, Calendar endCalendar) {
        // Convert Calendar to Date
        Date startDate = startCalendar.getTime();
        Date endDate = endCalendar.getTime();

        // Filter expenses based on date range
        List<ExpensesModel> filteredExpenses = filterExpensesByDateRange(startDate, endDate);

        // Access the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        GraphFragment graphFragment = (GraphFragment) fragmentManager.findFragmentById(R.id.graphfragment_container);

        if (graphFragment != null) {
            graphFragment.updatePieChart(filteredExpenses);
        } else {
            Log.e("ActivityName", "GraphFragment not found.");
        }
    }



    private void updateRecyclerViewWithFilteredData(Date startDate, Date endDate) {
        List<ExpensesModel> filteredExpenses = filterExpensesByDateRange(startDate, endDate);
        adapter.updateData(filteredExpenses);
        adapter.notifyDataSetChanged();
    }



    private void moveToPreviousWeek() {
        currentStartDate.add(Calendar.WEEK_OF_YEAR, -1);
        currentEndDate.add(Calendar.WEEK_OF_YEAR, -1);
        updateDateRangeText();
    }

    private void moveToNextWeek() {
        currentStartDate.add(Calendar.WEEK_OF_YEAR, +1);
        currentEndDate.add(Calendar.WEEK_OF_YEAR, +1);
        updateDateRangeText();
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
                saveExpenseToFirebase(new ExpensesModel(category, dateTime, "$" + price, categoryIcon), category);
                adapter.notifyDataSetChanged();
                updateGraphFragment();
                refreshBalance();
                saveTotalSpendings();
                dialog.dismiss();
            }

        });

        dialog.show();
    }
    private void refreshViewPager() {
        expPagerAdapter = new ExpensesPagerAdapter(this, isBalanceSetupComplete, expensesList);
        viewPager.setAdapter(expPagerAdapter);
        initializeDots(isBalanceSetupComplete ? 2 : 1);
    }

    private void saveExpenseToFirebase(ExpensesModel expense, String category) {
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
                    float newBalance;

                    // Check category and update balance
                    if ("Allowance".equals(category)) {
                        newBalance = currentBalance + expenseAmount; // Add to balance
                    } else {
                        newBalance = currentBalance - expenseAmount; // Subtract from balance
                    }
                    // Update balance in Firebase
                    userRef.child("balance").setValue(newBalance).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ExpensesTrackerActivity.this, "Expense saved and balance updated!", Toast.LENGTH_SHORT).show();
                            isBalanceSetupComplete = true; // Ensure this flag is set to true
                            refreshViewPager();
                            refreshBalance();
                            updateGraphFragment();
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
                refreshRecyclerView();
            } else {
                Log.e("ExpensesTrackerActivity", "Failed to save expense", task.getException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to save expense!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshRecyclerView() {
        adapter.updateData(expensesList);
        adapter.notifyDataSetChanged();
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

                    // Update the fragment with the new balance
                    AccountDetailsFragment fragment = (AccountDetailsFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.accDetailsFragmentContainer); // Replace with your actual fragment container ID

                    if (fragment != null) {
                        fragment.updateBalance(balance);
                    } else {
                        Log.e("ExpensesTrackerActivity", "AccountDetailsFragment is not found");
                    }
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

//    private void saveTotalSpendings() {
//        float totalSpendings = calculateTotalSpendings();
//
//        int user_Id = Global.getUser_Id();
//        String userPath = "user" + (user_Id + 1);
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);
//
//        userRef.child("spendings").setValue(totalSpendings).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(ExpensesTrackerActivity.this, "Total spendings updated!", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.e("ExpensesTrackerActivity", "Failed to update spendings", task.getException());
//                Toast.makeText(ExpensesTrackerActivity.this, "Failed to update spendings!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void saveTotalSpendings() {
        // Calculate total spendings and total allowance
        float totalSpendings = calculateTotalSpendings();
        float totalAllowance = calculateTotalAllowance();

        // Retrieve user ID and reference path
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);

        // Save total spendings
        userRef.child("spendings").setValue(totalSpendings).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ExpensesTrackerActivity.this, "Total spendings updated!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ExpensesTrackerActivity", "Failed to update spendings", task.getException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to update spendings!", Toast.LENGTH_SHORT).show();
            }
        });

        // Save total allowance
        userRef.child("allowance").setValue(totalAllowance).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ExpensesTrackerActivity.this, "Total allowance updated!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ExpensesTrackerActivity", "Failed to update allowance", task.getException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to update allowance!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private float calculateTotalAllowance() {
        float total = 0;
        for (ExpensesModel expense : expensesList) {
            if ("Allowance".equals(expense.getCategory())) {
                float amount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));
                total += amount;
            }
        }
        return total;
    }

    private float calculateTotalSpendings() {
        float total = 0;
        for (ExpensesModel expense : expensesList) {
            if (!"Allowance".equals(expense.getCategory())) {
                float amount = Float.parseFloat(expense.getPrice().replace("$", "").replace(",", ""));
                total += amount;
            }
        }
        return total;
    }



    private void loadExpensesFromFirebase() {
        Log.d("ExpensesTrackerActivity", "Loading expenses from Firebase...");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ExpensesTrackerActivity", "Data snapshot received.");
                expensesList.clear();  // Clear the existing list to avoid duplication

                if (snapshot.exists()) {
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        ExpensesModel expense = expenseSnapshot.getValue(ExpensesModel.class);
                        if (expense != null) {
                            expensesList.add(expense);
                        } else {
                            Log.d("ExpensesTrackerActivity", "Expense data is null for snapshot: " + expenseSnapshot.getKey());
                        }
                    }
                    Log.d("ExpensesTrackerActivity", "Expenses loaded: " + expensesList.size());
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged();
                    refreshRecyclerView();
                    updateGraphFragment(); // Update graph with new data
                    updateDateRangeText();
                } else {
                    Log.d("ExpensesTrackerActivity", "No expenses found.");
                    Toast.makeText(ExpensesTrackerActivity.this, "No expenses recorded.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ExpensesTrackerActivity", "Failed to load expenses: " + error.getMessage(), error.toException());
                Toast.makeText(ExpensesTrackerActivity.this, "Failed to load expenses. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateGraphFragment() {
        Log.d("ExpensesTrackerActivity", "Updating GraphFragment...");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment graphFragment = fragmentManager.findFragmentById(R.id.graphfragment_container);

        if (graphFragment != null && graphFragment instanceof GraphFragment) {
            ((GraphFragment) graphFragment).setExpensesList(expensesList);
        } else {
            Log.d("ExpensesTrackerActivity", "GraphFragment is not found or is not of type GraphFragment");
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
