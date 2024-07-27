package sg.edu.np.mad.beproductive.ExpensesTracker;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ExpensesPagerAdapter extends FragmentStateAdapter {

    private boolean isBalanceSetupComplete;
    private List<ExpensesModel> expensesList;

    public ExpensesPagerAdapter(@NonNull FragmentActivity fragmentActivity, boolean isSetupComplete, List<ExpensesModel> expensesList) {
        super(fragmentActivity);
        this.isBalanceSetupComplete = isSetupComplete;
        this.expensesList = expensesList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (!isBalanceSetupComplete) {
            // When setup is not complete, only show SetupBalanceFragment
            switch (position) {
                case 0:
                    return new SetupBalanceFragment();
                default:
                    return new SetupBalanceFragment(); // Default for safety
            }
        } else {
            // When setup is complete, show AccountDetailsFragment and GraphFragment
            switch (position) {
                case 0:
                    return new AccountDetailsFragment();
                case 1:
                    GraphFragment graphFragment = new GraphFragment();
                    graphFragment.setExpensesList(expensesList); // Pass expenses list to the fragment
                    return graphFragment;
                default:
                    throw new IllegalArgumentException("Unexpected position: " + position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return isBalanceSetupComplete ? 2 : 1;
    }

    public void updateBalanceSetupStatus(boolean isSetupComplete, List<ExpensesModel> expensesList) {
        this.isBalanceSetupComplete = isSetupComplete;
        this.expensesList = expensesList;
        notifyDataSetChanged(); // Refresh the adapter
    }
}
