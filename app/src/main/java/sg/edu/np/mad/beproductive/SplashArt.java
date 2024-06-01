package sg.edu.np.mad.beproductive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;

public class SplashArt extends AppCompatActivity {
    private Boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_art);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






        DatabaseHandler dbHandler = new DatabaseHandler(this);
        ArrayList<User> user_array = new ArrayList<>();
        user_array = dbHandler.getAllUsers();
        User user0 = new User("test","test123","testingemail");


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

                signedIn = true;
                break;
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(signedIn)
                {
                    Intent activity = new Intent(SplashArt.this, Log_In.class);
                    Bundle extras = new Bundle();
                    extras.putString("Username",user0.getName());
                    extras.putString("Password",user0.getPassword());
                    extras.putString("Email",user0.getEmail());
                    extras.putInt("ID",user0.getId());
                    extras.putBoolean("SignedIn",user0.getSignedIn());
                    extras.putBoolean("Pass",true);
                    activity.putExtras(extras);
                    startActivity(activity);

                }
                else
                {
                    Intent activity = new Intent(SplashArt.this,Log_In.class);
                    startActivity(activity);
                }

            }
        },550);


//        Intent activity = new Intent(SplashArt.this,Log_In.class);
//                    startActivity(activity);

    }
}