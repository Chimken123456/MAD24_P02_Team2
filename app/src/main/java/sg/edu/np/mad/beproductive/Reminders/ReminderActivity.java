package sg.edu.np.mad.beproductive.Reminders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
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
    private TextView addDatetime;
    private TextInputEditText etTitle;
    private Spinner reminderTypeSpinner;

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

        addDatetime = findViewById(R.id.addDatetime);
        etTitle = findViewById(R.id.etTitle);
        reminderTypeSpinner = findViewById(R.id.reminderType);

        findViewById(R.id.selectButton).setOnClickListener(v -> openDatePickerDialog());
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            if (selectedYear == 0 || selectedHour == 0) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else if (etTitle.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            } else {
                String selectedType = reminderTypeSpinner.getSelectedItem().toString();
                storeDateTime(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, selectedType);
                finish();
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
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

            String selectedDateTime = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay + " " + selectedHour + ":" + selectedMinute;
            addDatetime.setText(selectedDateTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void storeDateTime(int year, int month, int day, int hour, int minute, String type) {
        String title = etTitle.getText().toString();
        String selectedDateTime = year + "/" + (month + 1) + "/" + day + " " + hour + ":" + minute;

        createNotification(title, selectedDateTime);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("reminder_title", title);
        resultIntent.putExtra("reminder_datetime", selectedDateTime);
        resultIntent.putExtra("reminder_type", type);
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    @SuppressLint("MissingPermission")
    private void createNotification(String title, String datetime) {
        String content = "Reminder set for: " + datetime;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "beproductive")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(123, builder.build());
    }
}
