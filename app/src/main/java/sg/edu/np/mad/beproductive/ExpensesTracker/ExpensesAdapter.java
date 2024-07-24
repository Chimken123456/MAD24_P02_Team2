package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import sg.edu.np.mad.beproductive.R;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {
    private List<ExpensesModel> expensesList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryIcon;
        public TextView tvCategory;
        public TextView tvPrice;
        public TextView tvDateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }

    }

    public ExpensesAdapter(List<ExpensesModel> expensesList) {
        this.expensesList = expensesList;
    }

    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        return new ViewHolder(view);

    }
    public void updateData(List<ExpensesModel> newExpenses) {
        this.expensesList = newExpenses;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpensesModel currentItem = expensesList.get(position);

        // Check if the resource ID is valid
        int iconResId = currentItem.getCategoryIcon();
        try {
            holder.categoryIcon.setImageResource(iconResId);
            Log.d("ExpensesAdapter", "Setting icon with resource ID: " + iconResId);

        } catch (Exception e) {
            holder.categoryIcon.setImageResource(R.drawable.dining_icon);
            Log.e("ExpensesAdapter", "Error setting category icon: ", e);
        }

        holder.tvCategory.setText(currentItem.getCategory());
        holder.tvPrice.setText(currentItem.getPrice());
        holder.tvDateTime.setText(currentItem.getDateTime());
    }



    @Override
    public int getItemCount() {
        return expensesList.size();
    }
}
