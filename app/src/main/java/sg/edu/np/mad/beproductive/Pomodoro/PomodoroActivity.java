package sg.edu.np.mad.beproductive.Pomodoro;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import sg.edu.np.mad.beproductive.Alarm.AlarmEditor;
import sg.edu.np.mad.beproductive.Alarm.AlarmList;
import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class PomodoroActivity extends AppCompatActivity {
    private User user0;
    long timeLeft = 25 * 60 * 1000;
    final long mainTime = 25 * 60 * 1000;
    final long breakTime = 5 * 60 * 1000;
    boolean mainOrBreak = true;
    private CountDownTimer pomodoroTimer;
    private boolean timerRunning = false;
    private Button startButton;
    private TextView timer_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro_main);
        startButton = findViewById(R.id.start_button);
        timer_text_view = findViewById(R.id.timer_text_view);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    startTimer(timeLeft);
                    timerRunning = true;
                    startButton.setText("Pause");
                } else {
                    if (pomodoroTimer != null) {
                        pomodoroTimer.cancel();
                    }
                    timerRunning = false;
                    startButton.setText("Start");
                }
            }
        });
        // Storing total time ever used
        Intent recievingEnd = getIntent();
        Integer user_id  = recievingEnd.getIntExtra("User_Id" , -1);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user"+String.valueOf(user_id+1 ));
        DatabaseReference pomodoroRef = userRef.child("pomodoro");

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
        ImageView backbtn = findViewById(R.id.pomodoro_backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(PomodoroActivity.this, HomeMenu.class);
                Bundle extras = new Bundle();
                extras.putInt("ID",user_id);
                extras.putString("Username",user0.getName());
                extras.putString("Password",user0.getPassword());
                extras.putString("Email",user0.getEmail());
                extras.putBoolean("SignUp",true);
                activity.putExtras(extras);
                startActivity(activity);
            }
        });
    }


    private void startTimer(long timerLength) {
        pomodoroTimer = new CountDownTimer(timerLength, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = millisUntilFinished / 1000 % 60;
                timer_text_view.setText(String.format("%d:%02d", minutes, seconds));
                if (!mainOrBreak){
                    pomodoroTimer.cancel();
                    startBreakTimer(timerLength);
                }
            }


            @Override
            public void onFinish() {
                if (pomodoroTimer != null) {
                    pomodoroTimer.cancel();
                }
                timerRunning = false;
                // Display the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PomodoroActivity.this);
                builder.setMessage("Start a 5-minute break timer?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            timerRunning = true;
                            startBreakTimer(breakTime);
                            mainOrBreak = false;
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            startTimer(mainTime);
                            timerRunning = true;
                            startButton.setText("Pause");
                        })
                        .show();

                // Create the notification
                showNotification();
            }

            private void showNotification() {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return; // Exit if the permission is not granted
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(PomodoroActivity.this, "pomodoro_channel")
                        .setContentTitle("Pomodoro Timer")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentText("Your Pomodoro session is finished!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

                // Display the notification

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PomodoroActivity.this);
                notificationManager.notify(0,builder.build());

            }

            private void startBreakTimer(long timerLength) {
                pomodoroTimer = new CountDownTimer(timerLength, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft = millisUntilFinished;
                        long minutes = millisUntilFinished / 1000 / 60;
                        long seconds = millisUntilFinished / 1000 % 60;
                        timer_text_view.setText(minutes + ":" + seconds);
                    }

                    @Override
                    public void onFinish() {
                        if (pomodoroTimer != null) {
                            pomodoroTimer.cancel();
                        }
                        timerRunning = false;
                        AlertDialog.Builder builder = new AlertDialog.Builder(PomodoroActivity.this);
                        builder.setMessage("Start a 25-minute Pomodoro timer?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    mainOrBreak = true;
                                    startTimer(mainTime);
                                    timerRunning = true;
                                })
                                .setNegativeButton("No", (dialog, which) ->
                                        Toast.makeText(PomodoroActivity.this, "Thank you for using the Pomodoro timer!", Toast.LENGTH_SHORT).show())
                                .show();
                    }
                };
                pomodoroTimer.start();
            }



        };
        pomodoroTimer.start();
    }


}

