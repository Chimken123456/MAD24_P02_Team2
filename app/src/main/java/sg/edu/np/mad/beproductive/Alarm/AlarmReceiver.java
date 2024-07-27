package sg.edu.np.mad.beproductive.Alarm;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.User;

public class AlarmReceiver extends BroadcastReceiver {
    private User user0;
    @Override
    public void onReceive(Context context, Intent intent) {

        String time = intent.getStringExtra("Alarm_Setter_Time");
        int user_id = intent.getIntExtra("User_id",-1);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user"+String.valueOf(user_id+1 ));
        DatabaseReference alarmRef = userRef.child("alarm");



        alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        String alarm = snapshot.child("alarm").getValue().toString();
                        if(alarm.equals(time))
                        {
                            snapshot.child("checked").getRef().setValue(false);

                        }
                    }
                }
            }
        });
        //Notification
        Intent nextActivity = new Intent(context, AlarmNotification.class);
        Bundle extras = new Bundle();
        extras.putString("Alarm_Time" , time);
        extras.putInt("User_Id" , user_id);
        nextActivity.putExtras(extras);

        nextActivity.setAction("alarm_stop_music");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,nextActivity, PendingIntent.FLAG_CANCEL_CURRENT |PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm123")
                .setSmallIcon(R.drawable.alarm_icon)
                .setContentTitle("Alarm")
                .setContentText("Time to wake up~")
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(123,builder.build());


        CommonMethod.SoundPlayer(context, R.raw.alarm_music_proper);
        CommonMethod.player.setLooping(true);
    }
}
