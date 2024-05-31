package sg.edu.np.mad.beproductive.Reminders;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import sg.edu.np.mad.beproductive.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return; // Exit if the permission is not granted
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "beproductive")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("beproductive Notification Reminder")
                .setContentText("This is a reminder for your tasks!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, builder.build());
    }
}

