package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;

//fragment for when user has not set budget
public class SetBudgetFragment extends Fragment {

    private Button saveBudgetButton;
    private DatabaseReference budgetRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_budget, container, false);

        saveBudgetButton = view.findViewById(R.id.saveBudgetButton);

        saveBudgetButton.setOnClickListener(v -> showSetBudgetDialog());
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);

        // Initialize the database reference
        budgetRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("budget");
        return view;
    }

    private void showSetBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_budget, null);
        builder.setView(dialogView);

        EditText budgetEditText = dialogView.findViewById(R.id.budgetEditText);
        Button btnSave = dialogView.findViewById(R.id.saveBudgetButton);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String budgetStr = budgetEditText.getText().toString();
            if (!budgetStr.isEmpty()) {
                try {
                    float budget = Float.parseFloat(budgetStr);
                    saveBudgetToDatabase(budget);

                    // Replace the fragment with BudgetProgressFragment after saving
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.budgetFragmentContainer, new BudgetProgressFragment())
                            .commit();

                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Log.e("SetBudgetFragment", "Invalid budget amount", e);
                    // Optionally show an error message to the user
                }
            } else {
                Log.e("SetBudgetFragment", "Budget input is empty.");
            }
        });

        dialog.show();
    }

    private void saveBudgetToDatabase(float budget) {
        // Save the budget under 'budget' as a Float
        budgetRef.setValue(budget).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SetBudgetFragment", "Budget saved successfully.");
            } else {
                Log.e("SetBudgetFragment", "Error saving budget", task.getException());
            }
        }).addOnFailureListener(e -> Log.e("SetBudgetFragment", "Write failed", e));
    }
}
