package sg.edu.np.mad.beproductive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;

public class Log_In extends AppCompatActivity {

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
        EditText name_input = findViewById(R.id.log_in_name);
        EditText password_input = findViewById(R.id.password);
        EditText email_input = findViewById(R.id.email_address);
        Button submit_button = findViewById(R.id.submit_button);
        TextView sign_up = findViewById(R.id.sign_up);
        Intent activity = new Intent(Log_In.this, HomeMenu.class);


        User user0 = new User("test","test123","testingemail");
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_input.getText().toString();
                String password = password_input.getText().toString();
                String email = email_input.getText().toString();
//                System.out.println(name);
//                System.out.println(password);
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


                ArrayList<User> user_array = new ArrayList<>();
                user_array = dbHandler.getAllUsers();
                Boolean correct= false;
                for (User u : user_array)
                {
                    if(user0.getName().equals(u.getName()))
                    {
                        if(user0.getEmail().equals(u.getEmail()))
                        {
                            if(user0.getPassword().equals(u.getPassword()))
                            {
                                user0.setId(u.getId());
                                extras.putInt("ID",user0.getId());
                                activity.putExtras(extras);
                                startActivity(activity);
                                break;
                            }
                        }
                    }

                }

                //After sign up
                Intent recievingEnd = getIntent();
                boolean signUped = false;
                signUped = recievingEnd.getBooleanExtra("SignUp",false);
                int id = recievingEnd.getIntExtra("ID",0);
                String username0 = recievingEnd.getStringExtra("Username");
                String password0 = recievingEnd.getStringExtra("Password");
                String email0 = recievingEnd.getStringExtra("Email");
//                Log.i("MAOMAOO","Sign up:"+String.valueOf(signUped));
                if(signUped)
                {

                    if(user0.getName().equals(username0))
                    {
                        if(user0.getEmail().equals(email0))
                        {
                            if(user0.getPassword().equals(password0))
                            {
                                user0.setId(id);
                                extras.putInt("ID",user0.getId());
                                activity.putExtras(extras);
                                startActivity(activity);
                            }
                        }
                    }

                }

            }



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