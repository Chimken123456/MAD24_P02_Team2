package sg.edu.np.mad.beproductive;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;

public class Log_In extends AppCompatActivity {
    private Boolean Checked = false;
    private Boolean test = false;


    private void showError(EditText et, String s)
    {
        et.setError(s);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Getting all relevant xmls from log in xml
        EditText name_input = findViewById(R.id.log_in_name);
        EditText password_input = findViewById(R.id.password);
        EditText email_input = findViewById(R.id.email_address);
        Button submit_button = findViewById(R.id.submit_button);
        TextView sign_up = findViewById(R.id.sign_up);
        Intent activity = new Intent(Log_In.this, HomeMenu.class);
        RadioButton stay_signed_in = findViewById(R.id.stay_signed_in_radioButton);


        //Implement Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");


        //Testing
//        Getting data from real time database
//        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//
//                if(task.isSuccessful())
//                {
//                    DataSnapshot dataSnapshot = task.getResult();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                    {
//
////                        Log.i("MAOMAOO", String.valueOf(snapshot.child("id").getValue()) + "\t" + snapshot.child("name").getValue() + "\t" + snapshot.child("email").getValue());
//                    }
//
//
//
//                }
//            }
//        });

//        DataSnapshot dataSnapshot = task.getResult();
//        String name = String.valueOf(dataSnapshot.child("name").getValue());
//        String password = String.valueOf(dataSnapshot.child("password").getValue());
//        Log.i("MAOMAOO",name+ "\t" +password);
        //Writing data from real time database
//        userRef.setValue("cooking");

        //End of testing

        //Creating user with dummy data
        User user0 = new User("test","test123","testingemail");

        DatabaseHandler dbHandler = new DatabaseHandler(this);

        //Getting Pass info and checking if user has "pass" and if so , this means user has selected the keep signed in option
        Intent recievingEnd = getIntent();
        Boolean pass = recievingEnd.getBooleanExtra("Pass",false);
        if(pass)
        {
            int id = recievingEnd.getIntExtra("ID",0);
            String username = recievingEnd.getStringExtra("Username");
            String password = recievingEnd.getStringExtra("Password");
            String email = recievingEnd.getStringExtra("Email");
            user0.setName(username);
            user0.setPassword(password);
            user0.setEmail(email);
            user0.setId(id);
            Global.setUser_Id(user0.getId());//Setting the global variable user id such that all activities can access
            Bundle extras = new Bundle();

            extras.putString("Username",user0.getName());
            extras.putString("Password",user0.getPassword());
            extras.putString("Email",user0.getEmail());
            extras.putInt("ID",user0.getId());
            activity.putExtras(extras);

            startActivity(activity);
        }

        //When the user closes the app and have the signed in option selected already
        ArrayList<User> user_array = new ArrayList<>();
        user_array = dbHandler.getAllUsers();
        for(User u : user_array)
        {
//            Log.i("MAOMAOO",  String.valueOf(u.getId())+ "    "+ u.getName());

            if(u.getSignedIn())
            {
                user0.setId(u.getId());
                user0.setName(u.getName());
                user0.setPassword(u.getPassword());
                user0.setEmail(u.getEmail());
                user0.setSignedIn(u.getSignedIn());

                Global.setUser_Id(user0.getId()); //Setting the global variable user id such that all activities can access
                Bundle extras = new Bundle();

                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putInt("ID",user0.getId());
                activity.putExtras(extras);

                startActivity(activity);

                break;
            }
        }
        stay_signed_in.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Checked = true;
                }
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isCheckedFinal = Checked;
                final Boolean[] correct = {false};

                //Getting input from user
                String name = name_input.getText().toString();
                String password = password_input.getText().toString();
                String email = email_input.getText().toString();

                //Validating
                if(name.isEmpty())
                {
                    showError(name_input,"Invalid Name");
                    return;
                }

                if(password.isEmpty())
                {
                    showError(password_input,"Invalid Password");
                    return;
                }

                if (email.isEmpty())
                {
                    showError(email_input, "Invalid email input");
                    return;
                }
                else if (!(email.contains("@")))
                {
                    showError(email_input, "Missing @");
                    return;
                }

                user0.setName(name);
                user0.setPassword(password);
                user0.setEmail(email);

                Bundle extras = new Bundle();

                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());

                myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DataSnapshot dataSnapshot = task.getResult();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                String email = snapshot.child("email").getValue().toString();
                                String name = snapshot.child("name").getValue().toString();
                                String password = snapshot.child("password").getValue().toString();

                                if(user0.getName().equals(name))
                                {
                                    if(user0.getEmail().equals(email))
                                    {
                                        if(user0.getPassword().equals(password))
                                        {
                                            int id = Integer.valueOf(String.valueOf(snapshot.child("id").getValue()));
                                            user0.setId(id);
                                            Toast.makeText(v.getContext(),"Welcome " + user0.getName(),Toast.LENGTH_SHORT).show();
                                            Global.setUser_Id(user0.getId()); //Setting the global variable user id such that all activities can access
                                            extras.putInt("ID",user0.getId());
                                            extras.putString("userNum", snapshot.getKey()); // Pass userNum
                                            activity.putExtras(extras);


                                            if(isCheckedFinal)  // If user has opt for keep signed in
                                            {
                                                ArrayList<User> user_array = new ArrayList<>();
                                                user_array = dbHandler.getAllUsers();
                                                boolean has_acc = false;
                                                for(User u : user_array)
                                                {
                                                    if(u.getId() == user0.getId())
                                                    {
                                                        dbHandler.updateSignedIn_User(true,user0.getId());
                                                        has_acc = true;
                                                        test = true;
                                                    }
                                                }
                                                if(!has_acc) {
                                                    test = true;
                                                    dbHandler.addUsers(user0);
                                                    dbHandler.updateSignedIn_User(true, user0.getId());
                                                }
                                            }



                                            startActivity(activity);
                                            correct[0] = true;
                                            break;

                                        }
                                    }
                                }

                            }



                        }
                        if(!correct[0])
                        {
                            Toast.makeText(v.getContext(),"Wrong Details",Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            }


            //ArrayList<User> user_array = new ArrayList<>();
//                user_array = dbHandler.getAllUsers();
//                //Checking through database and see if user exists
//                for (User u : user_array)
//                {
//                    if(user0.getName().equals(u.getName()))
//                    {
//                        if(user0.getEmail().equals(u.getEmail()))
//                        {
//                            if(user0.getPassword().equals(u.getPassword()))
//                            {
//                                user0.setId(u.getId());
//                                if(isCheckedFinal)  // If user has opt for keep signed in
//                                {
//                                    dbHandler.updateSignedIn_User(true,user0.getId());
//                                }
//                                Toast.makeText(v.getContext(),"Welcome " + user0.getName(),Toast.LENGTH_SHORT).show();
//                                Global.setUser_Id(user0.getId()); //Setting the global variable user id such that all activities can access
//                                extras.putInt("ID",user0.getId());
//                                activity.putExtras(extras);
//                                startActivity(activity);
//                                correct = true;
//                                break;
//                            }
//                        }
//                    }
//                }

        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent
                Intent activity = new Intent(Log_In.this, Sign_Up.class);
                startActivity(activity);
            }
        });


    }
}