package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.beproductive.R;

public class GraphFragment extends Fragment {

    private PieChart pieChart;
    private List<ExpensesModel> expensesList = new ArrayList<>(); // Initialize with an empty list

    public GraphFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        setupPieChart();
        // Ensure the pie chart is updated if expensesList is not empty
        if (!expensesList.isEmpty()) {
            updatePieChart(expensesList);
        }
        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
    }

    // Setter for the expenses list
    public void setExpensesList(List<ExpensesModel> expensesList) {
        this.expensesList = expensesList;
        // Update the pie chart if the PieChart view is already initialized
        if (pieChart != null) {
            updatePieChart(expensesList);
        }
    }

    // Update pie chart with the expenses data
    public void updatePieChart(List<ExpensesModel> expensesList) {
        Map<String, Float> categoryTotals = calculateExpensesByCategory(expensesList);

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        int[] colors = {
                getResources().getColor(R.color.pastelGreen),
                getResources().getColor(R.color.pastelPurple),
                getResources().getColor(R.color.pastelPink),
                getResources().getColor(R.color.beige),
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.turqiose),
                getResources().getColor(R.color.lightgray),
                getResources().getColor(R.color.yellow)
        };
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }

    private Map<String, Float> calculateExpensesByCategory(List<ExpensesModel> expensesList) {
        Map<String, Float> categoryTotals = new HashMap<>();

        for (ExpensesModel expense : expensesList) {
            String category = expense.getCategory();
            String priceStr = expense.getPrice().replace("$", "").replace(",", "");

            // Validate the price string
            if (priceStr != null && !priceStr.isEmpty()) {
                try {
                    float amount = Float.parseFloat(priceStr);

                    // Update the category total
                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
                } catch (NumberFormatException e) {
                    // Handle the parsing error
                    Log.e("GraphFragment", "Invalid number format for price: " + priceStr, e);
                }
            } else {
                // Handle the case where price is null or empty
                Log.w("GraphFragment", "Empty or null price for category: " + category);
            }
        }

        return categoryTotals;
    }
}
