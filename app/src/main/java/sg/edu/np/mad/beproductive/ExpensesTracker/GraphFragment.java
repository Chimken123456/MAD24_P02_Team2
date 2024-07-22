package sg.edu.np.mad.beproductive.ExpensesTracker;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.beproductive.ExpensesTracker.ExpensesModel;
import sg.edu.np.mad.beproductive.R;

public class GraphFragment extends Fragment {

    private PieChart pieChart;
    private List<ExpensesModel> expensesList;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        setupPieChart();
        if (expensesList != null) {
            updatePieChart(calculateExpensesByCategory());
        }
        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(android.R.color.transparent);
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

    public void setExpensesList(List<ExpensesModel> expensesList) {
        this.expensesList = expensesList;
        if (pieChart != null) {
            updatePieChart(calculateExpensesByCategory());
        }
    }

    void updatePieChart(Map<String, Float> categoryTotals) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
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
        data.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }
}
