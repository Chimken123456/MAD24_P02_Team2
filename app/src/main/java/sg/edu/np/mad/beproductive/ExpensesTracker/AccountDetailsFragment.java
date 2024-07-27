package sg.edu.np.mad.beproductive.ExpensesTracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

// to display basic details that an expenses tracker would have
public class AccountDetailsFragment extends Fragment {

    private TextView balanceTextView;
    private TextView accountTextView;
    private TextView spendingTextView;
    private TextView incomeTextView;
    private DatabaseReference balanceRef;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);
        balanceTextView = view.findViewById(R.id.accountBalanceTextView);
        accountTextView = view.findViewById(R.id.accountNameTextView);
        spendingTextView = view.findViewById(R.id.totalSpendingTextView);
        incomeTextView = view.findViewById(R.id.totalIncomeTextView);

        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);

        // Initialize the database reference
        balanceRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("balance");
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath);
        // Retrieve and display the balance
        loadBalanceFromFirebase();
        loadAccNameFromFirebase();
        loadTotalSpendingsFromFirebase();
        loadTotalIncomeFromFirebase();

        return view;
    }

    public void updateBalance(float newBalance) {
        if (balanceTextView != null) {
            balanceTextView.setText("$" + newBalance);
        }
    }

    private void loadBalanceFromFirebase() {
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        Float balance = snapshot.getValue(Float.class);
                        if (balance != null) {
                            balanceTextView.setText("$" + balance);
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        balanceTextView.setText("Error loading total balance");
                    }
                } else {
                    balanceTextView.setText("Expenses:\n$0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void loadAccNameFromFirebase() {
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String name = snapshot.getValue(String.class);
                        if (name != null) {
                            accountTextView.setText(name + "'s savings!");
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        accountTextView.setText("Error loading account name");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void loadTotalSpendingsFromFirebase() {
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("spendings");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        Float spendings = snapshot.getValue(Float.class);
                        if (spendings != null) {
                            spendingTextView.setText("Expenses:\n$" + spendings);
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        spendingTextView.setText("Error loading total spendings");
                    }
                } else {
                    spendingTextView.setText("Expenses:\n$0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                spendingTextView.setText("Error loading total spendings");
            }
        });
    }

    private void loadTotalIncomeFromFirebase() {
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("allowance");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        Float income = snapshot.getValue(Float.class);
                        if (income != null) {
                            incomeTextView.setText("Income:\n$" + income);
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        incomeTextView.setText("Error loading total Income");
                    }
                } else {
                    incomeTextView.setText("Income:\n$0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                spendingTextView.setText("Error loading total income");
            }
        });
    }

}
