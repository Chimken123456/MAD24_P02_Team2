//package sg.edu.np.mad.beproductive.Alarm;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//
//import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
//import sg.edu.np.mad.beproductive.Manifest;
//import sg.edu.np.mad.beproductive.Manifest;
//import sg.edu.np.mad.beproductive.R;
//import sg.edu.np.mad.beproductive.User;
//
//public class AlarmList extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_alarm_list);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
////        if(ContextCompat.checkSelfPermission(this, Mani) == PackageManager.PERMISSION_DENIED)
//
//        Boolean notificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
//
//        if(!notificationEnabled)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.P});
//        }
//
//
//        RecyclerView recyclerView = findViewById(R.id.alarm_recycler);
//
//        //Receive user information and create instance of User
//        Intent receive = getIntent();
//        int id = receive.getIntExtra("ID",0);
//        String username = receive.getStringExtra("Username");
//        String password = receive.getStringExtra("Password");
//        String email = receive.getStringExtra("Email");
//        User user0 = new User(username,password,email);
//        user0.setId(id);
////        Log.i("MAOMAOO",String.valueOf(user0.getId()));
//        Intent recievingEnd = getIntent();
//        String time = recievingEnd.getStringExtra("Alarm_time");
//        Boolean checked = recievingEnd.getBooleanExtra("Alarm_checked",false);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        DatabaseReference myRef = database.getReference("User");
//        DatabaseReference userRef = myRef.child("user"+String.valueOf(user0.getId() +1 ));
//        DatabaseReference alarmRef = userRef.child("alarm");
//
//
//
//        ArrayList<Alarm> alarmArrayList = new ArrayList<>();
//
//        alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful())
//                {
//                    DataSnapshot dataSnapshot = task.getResult();
//                    Integer count = 0;
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                    {
//                        Alarm alarm;
//                        String time = snapshot.child("alarm").getValue().toString();
//                        String checked = snapshot.child("checked").getValue().toString();
//                        if(checked.equals("true"))
//                        {
//                            alarm = new Alarm(time, true);
//                            alarmArrayList.add(alarm);
//                        }
//                        else
//                        {
//                            alarm = new Alarm(time, false);
//                            alarmArrayList.add(alarm);
//                        }
//                        alarm.setAlarm_id(count);
//                        count++;
//
//                    }
//                    ArrayList<Integer> alarmListNum = new ArrayList<>();
//                    for (Alarm a : alarmArrayList)
//                    {
//                        String[] time_formatted = a.getTime().split(":");
//                        alarmListNum.add(Integer.valueOf(time_formatted[0] + time_formatted[1]));
//                    }
//                    Collections.sort(alarmListNum);
//
//                    ArrayList<Alarm> alarmArrayListFinal = new ArrayList<>();
//                    for (Integer i : alarmListNum)
//                    {
//
//                        for (Alarm a : alarmArrayList)
//                        {
//                            String[] time_formatted = a.getTime().split(":");
//                            String time_formatted_final = time_formatted[0] + time_formatted[1];
//                            Integer time_num = Integer.valueOf(time_formatted_final);
//                            if(time_num.equals(i))
//                            {
//                                alarmArrayListFinal.add(a);
//                            }
//                        }
//                    }
//
//                    AlarmAdapter alarmAdapter = new AlarmAdapter(alarmArrayListFinal,AlarmList.this);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AlarmList.this);
//                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                    recyclerView.setAdapter(alarmAdapter);
//                }
//            }
//        });
////
////        if(time!=null)
////        {
////            Alarm alarm3 = new Alarm(time, checked);
////            alarmArrayList.add(alarm3);
////        }
////        Log.i("MAOMAOO",String.valueOf(alarmArrayList.size()));
//
//
//        FloatingActionButton addNewAlarmButton = findViewById(R.id.alarm_recycler_floatingbutton);
//        addNewAlarmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.i("MAOMAOO", "Button works");
//                Intent activity = new Intent(AlarmList.this,AlarmSetter.class);
//                Bundle extras = new Bundle();
//                extras.putInt("ID",user0.getId());
//                extras.putString("Username",user0.getName());
//                extras.putString("Password",user0.getPassword());
//                extras.putString("Email",user0.getEmail());
//                extras.putBoolean("SignUp",true);
//                activity.putExtras(extras);
//                startActivity(activity);
//
//            }
//        });
//
//
//        ImageView alarmBackBtn = findViewById(R.id.alarm_list_backbtn);
//        alarmBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent activity = new Intent(AlarmList.this, HomeMenu.class);
//                Bundle extras = new Bundle();
//                extras.putInt("ID",user0.getId());
//                extras.putString("Username",user0.getName());
//                extras.putString("Password",user0.getPassword());
//                extras.putString("Email",user0.getEmail());
//                extras.putBoolean("SignUp",true);
//                activity.putExtras(extras);
//                startActivity(activity);
//            }
//        });
//
////        Log.i("MAOMAOO", String.valueOf(R.layout.activity_alarm_list));
//
//    }
//
//}
package sg.edu.np.mad.beproductive.Alarm;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

public class AlarmList extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if notification permission is granted, if not ask them for permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        }

        RecyclerView recyclerView = findViewById(R.id.alarm_recycler);

        // Receive user information and create instance of User
        Intent receive = getIntent();
        int id = receive.getIntExtra("ID", 0);
        String username = receive.getStringExtra("Username");
        String password = receive.getStringExtra("Password");
        String email = receive.getStringExtra("Email");
        User user0 = new User(username, password, email);
        user0.setId(id);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user" + (user0.getId() + 1));
        DatabaseReference alarmRef = userRef.child("alarm");

        ArrayList<Alarm> alarmArrayList = new ArrayList<>();

        //Getting all alarms from the user and setting it into adapter
        alarmRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                Integer count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Alarm alarm;
                    String time = snapshot.child("alarm").getValue().toString();
                    String checked = snapshot.child("checked").getValue().toString();
                    if (checked.equals("true")) {
                        alarm = new Alarm(time, true);
                        alarmArrayList.add(alarm);
                    } else {
                        alarm = new Alarm(time, false);
                        alarmArrayList.add(alarm);
                    }
                    alarm.setAlarm_id(count);
                    count++;
                }
                //Making a new list to sort
                ArrayList<Integer> alarmListNum = new ArrayList<>();
                for (Alarm a : alarmArrayList) {
                    String[] time_formatted = a.getTime().split(":");
                    alarmListNum.add(Integer.valueOf(time_formatted[0] + time_formatted[1]));
                }
                Collections.sort(alarmListNum); //Sort the alarm

                ArrayList<Alarm> alarmArrayListFinal = new ArrayList<>();
                //Checking for duplicates
                for (Integer i : alarmListNum) {
                    for (Alarm a : alarmArrayList) {
                        String[] time_formatted = a.getTime().split(":");
                        String time_formatted_final = time_formatted[0] + time_formatted[1];
                        Integer time_num = Integer.valueOf(time_formatted_final);
                        if (time_num.equals(i)) {
                            alarmArrayListFinal.add(a);
                        }
                    }
                }

                //Adding the array into the recyclerView
                AlarmAdapter alarmAdapter = new AlarmAdapter(alarmArrayListFinal, AlarmList.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AlarmList.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(alarmAdapter);
            }
        });

        //Button to go to the activity to add new alarm
        FloatingActionButton addNewAlarmButton = findViewById(R.id.alarm_recycler_floatingbutton);
        addNewAlarmButton.setOnClickListener(v -> {
            Intent activity = new Intent(AlarmList.this, AlarmSetter.class);
            Bundle extras = new Bundle();
            extras.putInt("ID", user0.getId());
            extras.putString("Username", user0.getName());
            extras.putString("Password", user0.getPassword());
            extras.putString("Email", user0.getEmail());
            extras.putBoolean("SignUp", true);
            activity.putExtras(extras);
            startActivity(activity);
        });

        //Button to go back to home menu
        ImageView alarmBackBtn = findViewById(R.id.alarm_list_backbtn);
        alarmBackBtn.setOnClickListener(v -> {
            Intent activity = new Intent(AlarmList.this, HomeMenu.class);
            Bundle extras = new Bundle();
            extras.putInt("ID", user0.getId());
            extras.putString("Username", user0.getName());
            extras.putString("Password", user0.getPassword());
            extras.putString("Email", user0.getEmail());
            extras.putBoolean("SignUp", true);
            activity.putExtras(extras);
            startActivity(activity);
        });
    }

    //Actions to be taken when the notification propmt is granted or denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied. Notifications may not be shown.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

