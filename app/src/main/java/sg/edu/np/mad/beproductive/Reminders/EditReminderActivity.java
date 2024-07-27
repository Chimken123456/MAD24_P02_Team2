package sg.edu.np.mad.beproductive.Reminders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sg.edu.np.mad.beproductive.R;

public class EditReminderActivity extends AppCompatActivity {

    private EditText editTitle;
    private Spinner editReminderType;
    private Button saveButton, cancelButton;
    private TextView editSelectButton;
    private int position, id;
    private String reminderTitle, reminderDatetime, reminderType;
    private Calendar calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_dialog_edit);

        editTitle = findViewById(R.id.editTitle);
        editReminderType = findViewById(R.id.editReminderType);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        editSelectButton = findViewById(R.id.editSelectButton);

        Intent intent = getIntent();
        position = intent.getIntExtra("reminder_position", -1);
        id = intent.getIntExtra("reminder_id", -1);
        reminderTitle = intent.getStringExtra("reminder_title");
        reminderDatetime = intent.getStringExtra("reminder_datetime");
        reminderType = intent.getStringExtra("reminder_type");

        if (reminderTitle != null) {
            editTitle.setText(reminderTitle);
        }

        if (reminderType != null) {
            // Get the array of reminder types from resources
            String[] reminderTypes = getResources().getStringArray(R.array.reminder_types);
            for (int i = 0; i < reminderTypes.length; i++) {
                if (reminderType.equals(reminderTypes[i])) {
                    editReminderType.setSelection(i);
                    break;
                }
            }
        }

        calendar = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            calendar.setTime(dateFormat.parse(reminderDatetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateDatetimeButtonText(calendar);

        editSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDateTimePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.getTime());
                String type = editReminderType.getSelectedItem().toString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("reminder_position", position);
                resultIntent.putExtra("reminder_id", id);
                resultIntent.putExtra("reminder_title", title);
                resultIntent.putExtra("reminder_datetime", datetime);
                resultIntent.putExtra("reminder_type", type);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void launchDateTimePicker() {
        final Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        updateDatetimeButtonText(calendar);
                    }
                }, currentHour, currentMinute, DateFormat.is24HourFormat(EditReminderActivity.this));
                timePickerDialog.show();
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    private void updateDatetimeButtonText(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String datetimeString = dateFormat.format(calendar.getTime());
        editSelectButton.setText(datetimeString);
    }
}
