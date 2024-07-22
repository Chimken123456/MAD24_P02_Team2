package sg.edu.np.mad.beproductive.ExpensesTracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.ToDoListPage.TodoList;
import sg.edu.np.mad.beproductive.User;


public class ExpensesTrackerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpensesAdapter adapter;
    private List<ExpensesModel> expensesList;

    private PieChart pieChart;

    private ProgressBar budgetProgressBar;
    private TextView budgetTextView, spentTextView;

    private float budget = 1000f; // Set your budget here
    private float totalSpent = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_tracker);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        expensesList = new ArrayList<>();
        // expensesList.add(new ExpensesModel("Food", "11/06/2024 11:30", "$10.00", R.drawable.food_icon));

        adapter = new ExpensesAdapter(expensesList);
        recyclerView.setAdapter(adapter);
        FloatingActionButton addTranscFab = findViewById(R.id.addTransactionFAB);
        addTranscFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

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

        pieChart = findViewById(R.id.pieChart);
        setupPieChart();
        updatePieChart();

        budgetTextView = findViewById(R.id.budgetTextView);
        spentTextView = findViewById(R.id.spentTextView);
        budgetProgressBar = findViewById(R.id.budgetProgressBar);

        budgetTextView.setText("Budget: $" + budget);
        budgetProgressBar.setMax((int) budget);
        updateBudgetProgress();
    }

    private void updateBudgetProgress() {
        budgetProgressBar.setProgress((int) totalSpent);
        spentTextView.setText("Spent: $" + totalSpent);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(61f);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
    }

    private void updatePieChart() {
        Map<String, Float> categoryTotals = calculateExpensesByCategory();

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(new int[]{R.color.pastelGreen, R.color.pastelPurple, R.color.pastelPink, R.color.beige, R.color.blue, R.color.turqiose}, this);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(data);
        pieChart.invalidate();
    }

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

                float amount = Float.parseFloat(price);
                expensesList.add(new ExpensesModel(category, dateTime, "$" + price, categoryIcon)); // Update the icon as needed
                totalSpent += amount;
                updateBudgetProgress();

                adapter.notifyDataSetChanged();
                updatePieChart();
                dialog.dismiss();
            }

        });

        dialog.show();
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
