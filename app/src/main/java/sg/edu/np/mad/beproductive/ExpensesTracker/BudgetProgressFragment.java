package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;

public class BudgetProgressFragment extends Fragment {

    private TextView budgetTextView;
    private ProgressBar budgetProgressBar;
    private TextView spentTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_progress, container, false);

        budgetTextView = view.findViewById(R.id.budgetTextView);
        budgetProgressBar = view.findViewById(R.id.budgetProgressBar);
        spentTextView = view.findViewById(R.id.spentTextView);

        loadBudgetData();

        return view;
    }

    public void updateSpentTextView(float totalSpent) {
        if (spentTextView != null) {
            spentTextView.setText("Spent: $" + totalSpent);
        }
    }

    public void updateProgressBarWithTotalSpent(float totalSpent) {
        if (budgetProgressBar != null && budgetTextView != null) {
            // Get the current budget
            String budgetText = budgetTextView.getText().toString();
            int budget = Integer.parseInt(budgetText.replace("Budget: $", "").trim());

            // Update spent text view
            spentTextView.setText("Spent: $" + totalSpent);

            // Update progress bar
            budgetProgressBar.setProgress((int) totalSpent);

            if (totalSpent > budget) {
                // Change to exceeded drawable
                budgetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_budget_bar_exceeded, null));
            } else {
                // Change to normal drawable
                budgetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_budget_bar, null));
            }
        }
    }


    private void loadBudgetData() {
        // Logic to load budget and spent data from the database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User/userX/budget");
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("budget");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer budget = snapshot.getValue(Integer.class);
                if (budget != null) {
                    budgetTextView.setText("Budget: $" + budget);
                    budgetProgressBar.setMax(budget);

                    // Assume spent amount is stored and retrieved as well
                    int spent = 0; // Replace with actual spent amount retrieval logic
                    spentTextView.setText("Spent: $" + spent);
                    budgetProgressBar.setProgress(spent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetProgressFragment", "Failed to load budget data.", error.toException());
            }
        });
    }
}
