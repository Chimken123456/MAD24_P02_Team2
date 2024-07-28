package sg.edu.np.mad.beproductive.Alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Calendar;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

public class AlarmNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView timeText = findViewById(R.id.alarm_notification_time);
        Button wakeUpBtn = findViewById(R.id.alarm_notification_wakeup_btn);
        Button snoozeBtn = findViewById(R.id.alarm_notification_snooze_btn);

        Intent recievingEnd = getIntent();
        Integer user_id  = recievingEnd.getIntExtra("User_Id" , -1);

        //Getting the current time, formatting it to 12:30 format, and setting the time to the textView
        Calendar calendar0 = Calendar.getInstance();
        String timeCurrent = String.valueOf(calendar0.getTime());
        String[] timeCurrentFormatted = timeCurrent.split(" ");
        String timeHourMinCurrent = timeCurrentFormatted[3];
        String[] time_time_array = timeHourMinCurrent.split(":");
        String time = time_time_array[0] + ":" +  time_time_array[1];
        timeText.setText(time);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user"+String.valueOf(user_id+1 ));
        DatabaseReference alarmRef = userRef.child("alarm");




        //When the user pressess done
        wakeUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            //Stops the music and finding the user to send information to HomeMenu
                            DataSnapshot dataSnapshot = task.getResult();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String email = dataSnapshot.child("email").getValue().toString();
                            String password = dataSnapshot.child("password").getValue().toString();

                            Intent activity = new Intent(AlarmNotification.this, HomeMenu.class);
                            activity.putExtra("ID" , user_id);
                            activity.putExtra("Username" , name);
                            activity.putExtra("Password" , password);
                            activity.putExtra("Email" , email);
                            CommonMethod.player.stop();
                            startActivity(activity);
                        }
                    }
                });


            }
        });

        //What happens if the user presses snooze
        snoozeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                //Getting the current time and setting the calendar to be current time with another 5minutes added into it
                String timeCurrent = String.valueOf(calendar.getTime());
                String[] timeCurrentFormatted = timeCurrent.split(" ");
                String timeHourMinCurrent = timeCurrentFormatted[3];
                String[] time_time_array = timeHourMinCurrent.split(":");
                Integer minute = Integer.valueOf(time_time_array[1]);
                calendar.set(Calendar.HOUR_OF_DAY , Integer.valueOf(time_time_array[0]));
                calendar.set(Calendar.MINUTE , minute + 5);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                //Setting of the alarm
                Intent intent = new Intent(v.getContext(),AlarmReceiver.class);
                AlarmManager alarmManager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(),0,intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

//                CommonMethod.player.stop();


                Toast.makeText(v.getContext(),"Snoozed", Toast.LENGTH_SHORT).show();
                userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            //Getting the user info and sending information to Home Menu
                            DataSnapshot dataSnapshot = task.getResult();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String email = dataSnapshot.child("email").getValue().toString();
                            String password = dataSnapshot.child("password").getValue().toString();

                            Intent activity = new Intent(v.getContext(), HomeMenu.class);
                            activity.putExtra("ID" , user_id);
                            activity.putExtra("Username" , name);
                            activity.putExtra("Password" , password);
                            activity.putExtra("Email" , email);
                            CommonMethod.player.stop();
                            startActivity(activity);
                        }
                    }
                });

            }
        });



    }
}