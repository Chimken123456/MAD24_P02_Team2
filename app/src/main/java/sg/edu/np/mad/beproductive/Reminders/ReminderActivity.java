package sg.edu.np.mad.beproductive.Reminders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import sg.edu.np.mad.beproductive.R;

public class ReminderActivity extends AppCompatActivity {

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reminder_dialog);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reminder_dialog), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.selectButton).setOnClickListener(v -> openDatePickerDialog());
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            // Ensure the user has selected a date and time
            if (selectedYear == 0 || selectedHour == 0) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else {
                storeDateTime(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> {
            // Close the current activity and go back to the previous one
            finish();
        });
    }

    private void openDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedYear = year1;
            selectedMonth = month1;
            selectedDay = dayOfMonth;

            openTimePickerDialog();
        }, year, month, day);
        dialog.show();
    }

    private void openTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedHour = hourOfDay;
            selectedMinute = minute1;

            storeDateTime(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void storeDateTime(int year, int month, int day, int hour, int minute){
        String title = ((TextInputEditText) findViewById(R.id.etTitle)).getText().toString();
        String selectedDateTime = year + "/" + (month + 1) + "/" + day + " " + hour + ":" + minute;

        // Create a notification with the title and datetime
        createNotification(title, selectedDateTime);

        // Prepare the result intent and finish the activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("reminder_title", title);
        resultIntent.putExtra("reminder_datetime", selectedDateTime);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @SuppressLint("MissingPermission")
    private void createNotification(String title, String datetime) {
        // Convert the datetime string to a more readable format if necessary
        // You can use SimpleDateFormat for this purpose

        // Create the notification content
        String content = "Reminder set for: " + datetime;

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "beproductive")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Notify the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(123, builder.build());
    }
}
