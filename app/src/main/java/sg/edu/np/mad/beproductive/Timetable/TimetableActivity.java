package sg.edu.np.mad.beproductive.Timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

public class TimetableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.timetable_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Receive user information and create instance of User
        Intent receive = getIntent();
        int id = receive.getIntExtra("ID",0);
        String username = receive.getStringExtra("Username");
        String password = receive.getStringExtra("Password");
        String email = receive.getStringExtra("Email");
        User user = new User(username,password,email);
        user.setId(id);
        //Create instance of DatabaseHandler for the current activity
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        //Back button
        ImageView backButton = findViewById(R.id.timetable_back);
        //Start HomeMenu activity when clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TimetableActivity.this, HomeMenu.class));
            }
        });
        //Display the current date 
        ZoneId zone = ZoneId.of("Singapore");
        LocalDate today = LocalDate.now(zone);
        String currentDate = "Date: " + today.toString();
        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(currentDate);
        //Create instance of Schedule
        Schedule userSchedule = new Schedule();
        //Check if there is currently a timetable in the database and initialises one if there isnt
        if (dbHandler.checkTableNull()) {
            userSchedule.onCreate();
            ArrayList<Timeslot> slots = userSchedule.getTimeslots();
            for (int i = 0; i < slots.size(); i++) {
                dbHandler.insertActivity(slots.get(i));
            }

        }
        //Fetch saved activities from database if it exists
        else {
            userSchedule = dbHandler.getUserActivities();
        }
        //Store the saved timeslots in an ArrayList
        ArrayList<Timeslot> timeslotList = userSchedule.getTimeslots();
        //Inflate recyclerview
        RecyclerView recyclerView = findViewById(R.id.timetableRecyclerView);
        LinearLayoutManager linLayoutManager = new LinearLayoutManager(this);
        TimetableAdapter tAdapter = new TimetableAdapter(timeslotList, this);

        recyclerView.setLayoutManager(linLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tAdapter);

        //Alert for reset
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset all timetable activities?");
        builder.setCancelable(true);
        //Restarts the activity on click
        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dbHandler.resetAllActivities();
                        restartActivity();
                    }
                });
        //Closes the alert dialog on click
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog resetDialog = builder.create();
        
        TextView resetButton = findViewById(R.id.resetButton);
        resetButton.setText("Reset");
        //Show alert dialog on click
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetDialog.show();
            }
        });
    }
    //Method to restart the activity without transition
    private void restartActivity() {
        finish();
        overridePendingTransition(0,0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }
}
