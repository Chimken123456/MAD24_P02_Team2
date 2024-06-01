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
import sg.edu.np.mad.beproductive.ToDoListPage.TodoList;
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

        Intent receive = getIntent();
        int id = receive.getIntExtra("ID",0);
        String username = receive.getStringExtra("Username");
        String password = receive.getStringExtra("Password");
        String email = receive.getStringExtra("Email");
        User user = new User(username,password,email);
        user.setId(id);

        DatabaseHandler dbHandler = new DatabaseHandler(this);
        //Back button
        ImageView backButton = findViewById(R.id.timetable_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",user.getId());
                extras.putString("Username",user.getName());
                extras.putString("Password",user.getPassword());
                extras.putString("Email",user.getEmail());
                Intent intent = new Intent(TimetableActivity.this, HomeMenu.class);
                intent.putExtras(extras);
                startActivity(intent);
//                startActivity(new Intent(TimetableActivity.this, HomeMenu.class));
            }
        });
        //Set current date to display
        ZoneId zone = ZoneId.of("Singapore");
        LocalDate today = LocalDate.now(zone);
        String currentDate = "Date: " + today.toString();
        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(currentDate);

        Schedule userSchedule = new Schedule();

        if (dbHandler.checkTableNull()) {
            userSchedule.onCreate();
            ArrayList<Timeslot> slots = userSchedule.getTimeslots();
            for (int i = 0; i < slots.size(); i++) {
                dbHandler.insertActivity(slots.get(i));
            }

        }
        else {
            //implement saving of description and resetting
            userSchedule = dbHandler.getUserActivities();
        }

        ArrayList<Timeslot> timeslotList = userSchedule.getTimeslots();

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

        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dbHandler.resetAllActivities();
                        restartActivity();
                    }
                });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog resetDialog = builder.create();

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setText("Reset");
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetDialog.show();
            }
        });
    }

    private void restartActivity() {
        finish();
        overridePendingTransition(0,0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }
}
