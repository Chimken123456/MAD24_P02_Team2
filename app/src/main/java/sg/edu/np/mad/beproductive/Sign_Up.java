package sg.edu.np.mad.beproductive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;

public class Sign_Up extends AppCompatActivity {

    private void showError(EditText et, String s)
    {
        et.setError(s);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Getting relevant xml
        EditText email_input = findViewById(R.id.sign_up_email);
        EditText name_input =findViewById(R.id.sign_up_name);
        EditText password_input = findViewById(R.id.sign_up_password);
        Button submit_button = findViewById(R.id.sign_up_submit_button);
        TextView log_in = findViewById(R.id.log_in);
//        DatabaseHandler dbHandler = new DatabaseHandler(this);
        //Setting user with dummy data
        User user0 = new User("test","test123","testingemail");


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting input from user
                String name = name_input.getText().toString();
                String password = password_input.getText().toString();
                String email = email_input.getText().toString();

                //Validate
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
                //Creating user using database
                DatabaseHandler dbHandler = new DatabaseHandler(v.getContext());
                ArrayList<User> user_array = new ArrayList<>();
                user_array = dbHandler.getAllUsers();
                int id = user_array.size() + 1;
                user0.setId(id);
                user0.setName(name);
                user0.setPassword(password);
                user0.setEmail(email);
                dbHandler.addUsers(user0);
                Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_SHORT).show();

                //Making intent and the relevant data to send over to home menu page
                Intent activity = new Intent(Sign_Up.this, HomeMenu.class);
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                activity.putExtras(extras);
                Global.setUser_Id(user0.getId()); //Setting the global variable user id such that all activities can access
                startActivity(activity);
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Sign_Up.this,Log_In.class);
                startActivity(activity);
            }
        });


    }
}