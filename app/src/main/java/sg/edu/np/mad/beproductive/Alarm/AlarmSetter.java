package sg.edu.np.mad.beproductive.Alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
//import sg.edu.np.mad.beproductive.Manifest;
import sg.edu.np.mad.beproductive.Analysis.AnalysisActivity;
import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;
import sg.edu.np.mad.beproductive.databinding.ActivityAlarmSetterBinding;

public class AlarmSetter extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    private ActivityAlarmSetterBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_setter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can now send notifications
//            } else {
//                // Permission denied, handle accordingly
//            }
//        }

        Intent receive = getIntent();
        int id = receive.getIntExtra("ID",0);
        String username = receive.getStringExtra("Username");
        String password = receive.getStringExtra("Password");
        String email = receive.getStringExtra("Email");
        User user0 = new User(username,password,email);
        user0.setId(id);

        Bundle extras = new Bundle();
        extras.putInt("ID",user0.getId());
        extras.putString("Username",user0.getName());
        extras.putString("Password",user0.getPassword());
        extras.putString("Email",user0.getEmail());
        extras.putBoolean("SignUp",true);

        binding = ActivityAlarmSetterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        NumberPicker numberPickerHour0 = findViewById(R.id.alarm_numberpicker_hour0);
        numberPickerHour0.setMaxValue(23);
        numberPickerHour0.setMinValue(0);
        numberPickerHour0.setValue(0);

        NumberPicker numberPickerMinute0 = findViewById(R.id.alarm_numberpicker_minute0);
        numberPickerMinute0.setMaxValue(59);
        numberPickerMinute0.setMinValue(0);
        numberPickerMinute0.setValue(0);



        binding.alarmBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(AlarmSetter.this, AlarmList.class);
//                Bundle extras = new Bundle();
//                extras.putString("Alarm_time", alarm.getTime());
//                extras.putBoolean("Alarm_checked", alarm.getChecked());
                activity.putExtras(extras);
                startActivity(activity);
            }
        });

//        binding.selectTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timePicker = new MaterialTimePicker.Builder()
//                        .setTimeFormat(TimeFormat.CLOCK_12H)
//                        .setHour(12)
//                        .setMinute(0)
//                        .setTitleText("Select Alarm Time")
//                        .build();
//                timePicker.show(getSupportFragmentManager(),"alarm123");
//                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(timePicker.getHour() > 12)
//                        {
//                            binding.selectTime.setText(
//                                    String.format("%02d",(timePicker.getHour()-12)) +":"+ String.format("%02d", timePicker.getMinute())+"PM"
//                            );
//                        }
//                        else
//                        {
//                            binding.selectTime.setText(timePicker.getHour()+":" + timePicker.getMinute()+ "AM");
//                        }
//
//                        calendar = Calendar.getInstance();
//                        String timeCurrent = String.valueOf(calendar.getTime());
//                        String[] timeCurrentFormatted = timeCurrent.split(" ");
//                        String timeHourMinCurrent = timeCurrentFormatted[3];
//                        String[] time_time_array = timeHourMinCurrent.split(":");
//                        String timeCompare = time_time_array[0] + time_time_array[1];
//
////                        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
////                        calendar.set(Calendar.MINUTE,timePicker.getMinute());
//                        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(numberPickerHour0.getValue()));
//                        calendar.set(Calendar.MINUTE, Integer.valueOf(numberPickerMinute0.getValue()));
//                        calendar.set(Calendar.SECOND,0);
//                        calendar.set(Calendar.MILLISECOND,0);
//                        String time = String.valueOf(calendar.getTime());
//                        String[] test = time.split(" ");
//                        String[] time_time_array1 = test[3].split(":");
//                        String timeCompareTo = time_time_array1[0] + time_time_array1[1];
//                        if(Integer.valueOf(timeCompare) > Integer.valueOf(timeCompareTo))
//                        {
//                            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(timeCurrentFormatted[2])+ 1);
//                        }
//
//
////                        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(test[2])+1);
////                        Log.i("MAOMAOO" , String.valueOf(numberPickerHour0.getValue()));
////                        Log.i("MAOMAOO" , String.valueOf(numberPickerMinute0.getValue()));
//                        Log.i("MAOMAOO", calendar.getTime().toString());
//                    }
//                });
//            }
//        });
        binding.setAlarm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ScheduleExactAlarm")
            @Override
            public void onClick(View v) {
                //Setting of time
                calendar = Calendar.getInstance();
                String timeCurrent = String.valueOf(calendar.getTime());
                String[] timeCurrentFormatted = timeCurrent.split(" ");
                String timeHourMinCurrent = timeCurrentFormatted[3];
                String[] time_time_array = timeHourMinCurrent.split(":");
                String timeCompare = time_time_array[0] + time_time_array[1];

                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(numberPickerHour0.getValue()));
                calendar.set(Calendar.MINUTE, Integer.valueOf(numberPickerMinute0.getValue()));
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                String time = String.valueOf(calendar.getTime());
                String[] test = time.split(" ");
                String[] time_time_array1 = test[3].split(":");
                String timeCompareTo = time_time_array1[0] + time_time_array1[1];
                if(Integer.valueOf(timeCompare) > Integer.valueOf(timeCompareTo))
                {
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(timeCurrentFormatted[2])+ 1);
                }


                FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference myRef = database.getReference("User");
                DatabaseReference userRef = myRef.child("user"+String.valueOf(user0.getId() +1 ));
                DatabaseReference alarmRef = userRef.child("alarm");


                String time0 = String.valueOf(calendar.getTime());
                String[] test0 = time0.split(" ");
                String time_time = test0[3];
                String[] time_time_array0 = time_time.split(":");
                String time_time_formatted = time_time_array0[0]+":"+time_time_array0[1];

                alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            if(snapshot.child("alarm").getValue().toString().equals(time_time_formatted))
                            {

                                snapshot.getRef().child("checked").setValue(true);
                                return;
                            }
                        }

                        int count = Integer.valueOf(String.valueOf(task.getResult().getChildrenCount()));
                        String newAlarmId = alarmRef.push().getKey();

                        DatabaseReference timeRef = alarmRef.child(newAlarmId);
                        HashMap hashMap = new HashMap();
                        hashMap.put("alarm",String.valueOf(time_time_formatted));
                        hashMap.put("checked",true);
                        timeRef.setValue(hashMap);


                        Integer count1 = Integer.valueOf(String.valueOf(dataSnapshot.getChildrenCount()));
                        alarm = new Alarm(time_time_formatted,true);
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(AlarmSetter.this,AlarmReceiver.class);
                        intent.putExtra("Alarm_Setter_Time",time_time_formatted);
                        intent.putExtra("User_id", user0.getId());
                        intent.putExtra("Username",user0.getName());
                        intent.putExtra("Password",user0.getPassword());
                        intent.putExtra("Email",user0.getEmail());
                        intent.putExtra("SignUp",true);
                        pendingIntent = PendingIntent.getBroadcast(AlarmSetter.this,count1,intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

                        Toast.makeText(AlarmSetter.this,"Alarm Set", Toast.LENGTH_SHORT).show();

                    }
                });

//                alarm = new Alarm(time_time_formatted,true);
//                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                Intent intent = new Intent(AlarmSetter.this,AlarmReceiver.class);
//                intent.putExtra("Alarm_Setter_Time",time_time_formatted);
//                intent.putExtra("User_id", user0.getId());
//                intent.putExtra("Username",user0.getName());
//                intent.putExtra("Password",user0.getPassword());
//                intent.putExtra("Email",user0.getEmail());
//                intent.putExtra("SignUp",true);
//                pendingIntent = PendingIntent.getBroadcast(AlarmSetter.this,0,intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
//
//                Toast.makeText(AlarmSetter.this,"Alarm Set", Toast.LENGTH_SHORT).show();



            }

        });

    }
    private void createNotificationChannel()
    {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        CharSequence name = "alarmchannel";
        String desc = "Channel for Alarm Manager";
        int imp = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("alarm123",name,imp);
        channel.setDescription(desc);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


}