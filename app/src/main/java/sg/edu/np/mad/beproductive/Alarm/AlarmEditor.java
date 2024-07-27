package sg.edu.np.mad.beproductive.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

public class AlarmEditor extends AppCompatActivity {
    private User user0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        ImageView backbtn = findViewById(R.id.alarm_editor_backbtn);
        NumberPicker numberPickerHour = findViewById(R.id.alarm_numberpicker_edit_hour0);
        NumberPicker numberPickerMinute = findViewById(R.id.alarm_numberpicker_edit_minute0);
        Button editAlarm = findViewById(R.id.alarm_editor_edit_btn);
        Button deleteAlarm = findViewById(R.id.alarm_editor_delete_btn);

        //Setting the number pickers value and default value
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);

        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setMinValue(0);

        Intent recievingEnd = getIntent();
        Integer userid = recievingEnd.getIntExtra("User_Id" ,-1);
        String AlarmTime = recievingEnd.getStringExtra("Alarm_Time");
        Boolean Checked = recievingEnd.getBooleanExtra("Alarm_Checked" , false);

        //Setting the alarm time to the number picker as a default number
        String[] time_time_array = AlarmTime.split(":");

        numberPickerHour.setValue(Integer.valueOf(time_time_array[0]));
        numberPickerMinute.setValue(Integer.valueOf(time_time_array[1]));

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user"+String.valueOf(Global.getUser_Id() +1 ));
        DatabaseReference alarmRef = userRef.child("alarm");

        //Checking and setting the user
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        if(snapshot.child("id").getValue().toString().equals(String.valueOf(Global.getUser_Id())))
                        {
                            String email = snapshot.child("email").getValue().toString();
                            String password = snapshot.child("password").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            user0 = new User(name, password,email);
                            user0.setId(Global.getUser_Id());
//                            user0.setName(name);
//                            user0.setPassword(password);
//                            user0.setEmail(email);

                            break;
                        }
                    }
                }
            }
        });

        //When the user presses into the edit alarm button
        editAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            //Formatting the string if its a single digit
                            String hour = String.valueOf(numberPickerHour.getValue());
                            if(numberPickerHour.getValue() < 10)
                            {
                                hour = "0" + hour;
                            }

                            String minute = String.valueOf(numberPickerMinute.getValue());

                            //Formatting the string if its a single digit
                            if(numberPickerMinute.getValue() < 10)
                            {
                                minute = "0" + minute;
                            }
                            DataSnapshot dataSnapshot = task.getResult();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                if(snapshot.child("alarm").getValue().toString().equals(AlarmTime))
                                {
                                    //Saving the new alarm to overwrite the old alarm
                                    snapshot.getRef().child("alarm").setValue(hour+":"+minute);
                                    Toast.makeText(AlarmEditor.this, "Alarm Editted",Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                        }
                    }
                });
            }
        });

        //Deleting the alarm if the user presses into delete alarm
        deleteAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DataSnapshot dataSnapshot = task.getResult();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                if(snapshot.child("alarm").getValue().toString().equals(AlarmTime))
                                {
                                    //Removing the alarm from database
                                    snapshot.getRef().removeValue();
                                    Toast.makeText(AlarmEditor.this, "Alarm Deleted",Toast.LENGTH_SHORT).show();
                                    Intent activity = new Intent(AlarmEditor.this, AlarmList.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("ID",userid);
                                    extras.putString("Username",user0.getName());
                                    extras.putString("Password",user0.getPassword());
                                    extras.putString("Email",user0.getEmail());
                                    extras.putBoolean("SignUp",true);
                                    activity.putExtras(extras);

                                    startActivity(activity);
                                    break;
                                }
                            }

                        }
                    }
                });
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(AlarmEditor.this, AlarmList.class);
                Bundle extras = new Bundle();
                extras.putInt("ID",userid);
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                activity.putExtras(extras);
                startActivity(activity);
            }
        });

    }
}