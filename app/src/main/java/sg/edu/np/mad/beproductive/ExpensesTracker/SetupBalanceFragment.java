package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;


//fragment for when user has not set initial balance
public class SetupBalanceFragment extends Fragment {

    private DatabaseReference balanceRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_balance, container, false);

        Button setupButton = view.findViewById(R.id.setupButton);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetupDialog();
            }
        });

        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);

        // Initialize the database reference to the 'balance' node
        balanceRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("balance");

        return view;
    }

    private void showSetupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setup_balance, null);
        builder.setView(dialogView);

        final EditText etBalance = dialogView.findViewById(R.id.balanceEditText);
        Button btnSaveBalance = dialogView.findViewById(R.id.btnSaveBalance);

        final AlertDialog dialog = builder.create();
        btnSaveBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String balanceStr = etBalance.getText().toString();
                if (!balanceStr.isEmpty()) {
                    try {
                        float balance = Float.parseFloat(balanceStr);
                        saveBalanceToFirebase(balance);
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid balance amount.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Balance cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void saveBalanceToFirebase(float balance) {
        // Save the balance under the 'balance' node
        balanceRef.setValue(balance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Balance saved successfully
                    Toast.makeText(getContext(), "Balance saved successfully!", Toast.LENGTH_SHORT).show();
                    // Restart the activity to update the pager adapter
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                } else {
                    // Handle errors
                    Toast.makeText(getContext(), "Error saving balance: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
