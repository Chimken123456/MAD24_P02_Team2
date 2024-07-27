//package sg.edu.np.mad.beproductive.HomePage;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import sg.edu.np.mad.beproductive.DatabaseHandler;
//import sg.edu.np.mad.beproductive.Global;
//import sg.edu.np.mad.beproductive.Log_In;
//import sg.edu.np.mad.beproductive.R;
//import sg.edu.np.mad.beproductive.Reminders.ReminderActivity;
//import sg.edu.np.mad.beproductive.Reminders.ReminderMain;
//import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;
//import sg.edu.np.mad.beproductive.ToDoListPage.TodoList;
//import sg.edu.np.mad.beproductive.User;
//
//public class HomeMenu extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_home_menu);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        DatabaseHandler dbHandler = new DatabaseHandler(this);
//        Intent recievingEnd = getIntent();
//        int id = recievingEnd.getIntExtra("ID",0);
//        String username = recievingEnd.getStringExtra("Username");
//        String password = recievingEnd.getStringExtra("Password");
//        String email = recievingEnd.getStringExtra("Email");
//        User user0 = new User(username,password,email);
//        user0.setId(id);
//
//        Global.setUser_Id(user0.getId());
//
//
//
//        Global.setUser_Id(user0.getId());
//
//        CardView toDoListButton = findViewById(R.id.todolist_navbutton);
//        CardView logOutButton = findViewById(R.id.logout_btn);
//
//        logOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(),"Signing Out",Toast.LENGTH_SHORT).show();
//                Intent activity = new Intent(HomeMenu.this, Log_In.class);
//                dbHandler.updateSignedIn_User(false,id);
//                startActivity(activity);
//            }
//        });
//        toDoListButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle extras = new Bundle();
//                extras.putInt("ID",user0.getId());
//                Global.setUser_Id(user0.getId());
//
//                extras.putString("Username",user0.getName());
//                extras.putString("Password",user0.getPassword());
//                extras.putString("Email",user0.getEmail());
//                extras.putBoolean("SignUp",true);
//
//                Intent intent = new Intent(HomeMenu.this, TodoList.class);
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//        });
//
//        CardView timetableButton = findViewById(R.id.timetable_navbutton);
//        timetableButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle extras = new Bundle();
//                extras.putInt("ID",user0.getId());
//                Global.setUser_Id(user0.getId());
//
//                extras.putString("Username",user0.getName());
//                extras.putString("Password",user0.getPassword());
//                extras.putString("Email",user0.getEmail());
//                extras.putBoolean("SignUp",true);
//                Intent intent = new Intent(HomeMenu.this, TimetableActivity.class);
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//        });
//
//       CardView remindersButton = findViewById(R.id.reminders_navbtn);
//        remindersButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create an intent to navigate to the ReminderMain activity
//                Intent intent = new Intent(HomeMenu.this, ReminderMain.class);
//                startActivity(intent);
//            }
//        });
//
//        CardView remindersButton = findViewById(R.id.reminders_navbtn);
//        remindersButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle extras = new Bundle();
//                extras.putInt("ID",user0.getId());
//                extras.putString("Username",user0.getName());
//                extras.putString("Password",user0.getPassword());
//                extras.putString("Email",user0.getEmail());
//                extras.putBoolean("SignUp",true);
//                Intent intent = new Intent(HomeMenu.this, ReminderMain.class);
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//        });
//    }
//}

package sg.edu.np.mad.beproductive.HomePage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sg.edu.np.mad.beproductive.Alarm.AlarmList;
import sg.edu.np.mad.beproductive.Analysis.AnalysisActivity;
import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.Log_In;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.Reminders.ReminderActivity;
import sg.edu.np.mad.beproductive.Reminders.ReminderMain;
import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;
import sg.edu.np.mad.beproductive.ToDoListPage.TodoList;
import sg.edu.np.mad.beproductive.User;

public class HomeMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        Intent recievingEnd = getIntent();
        int id = recievingEnd.getIntExtra("ID",0);
        String username = recievingEnd.getStringExtra("Username");
        String password = recievingEnd.getStringExtra("Password");
        String email = recievingEnd.getStringExtra("Email");
        User user0 = new User(username,password,email);
        user0.setId(id);

        Global.setUser_Id(user0.getId());

        CardView toDoListButton = findViewById(R.id.todolist_navbutton);
        CardView logOutButton = findViewById(R.id.logout_btn);
        CardView alarmButton = findViewById(R.id.alarm_btn);
        TextView loggedInUsername = findViewById(R.id.logged_in_username);
        loggedInUsername.setText(String.format("Logged in as %s", username));
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Signing Out",Toast.LENGTH_SHORT).show();
                Intent activity = new Intent(HomeMenu.this, Log_In.class);
                dbHandler.updateSignedIn_User(false,id);
                startActivity(activity);
            }
        });
        toDoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                Global.setUser_Id(user0.getId());

                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);

                Intent intent = new Intent(HomeMenu.this, TodoList.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        CardView timetableButton = findViewById(R.id.timetable_navbutton);
        timetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                Global.setUser_Id(user0.getId());
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                Intent intent = new Intent(HomeMenu.this, TimetableActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        CardView monthlyButton = findViewById(R.id.monthlyreport_navbtn);
        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                Intent intent = new Intent(HomeMenu.this, AnalysisActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });



        CardView remindersButton = findViewById(R.id.reminders_navbtn);
        remindersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                Intent intent = new Intent(HomeMenu.this, ReminderMain.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user0.getId());
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                Intent intent = new Intent(HomeMenu.this, AlarmList.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

    }
}
