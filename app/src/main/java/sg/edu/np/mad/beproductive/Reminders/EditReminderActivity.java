package sg.edu.np.mad.beproductive.Reminders;

import android.annotation.SuppressLint;
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

import androidx.activity.EdgeToEdge;
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
    private int position;
    private String reminderTitle, reminderDatetime, reminderType;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reminder_dialog_edit);

        editTitle = findViewById(R.id.editTitle);
        editReminderType = findViewById(R.id.editReminderType);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        position = intent.getIntExtra("reminder_position", -1);
        reminderTitle = intent.getStringExtra("reminder_title");
        reminderDatetime = intent.getStringExtra("reminder_datetime");
        reminderType = intent.getStringExtra("reminder_type");

        if (reminderTitle != null) {
            editTitle.setText(reminderTitle);
        }

        if (reminderType != null) {
            // Get the array of reminder types from resources
            String[] reminderTypes = getResources().getStringArray(R.array.reminder_types);

            // Iterate through the array to find the position of the reminderType
            int position = -1; // Initialize position to -1, indicating not found
            for (int i = 0; i < reminderTypes.length; i++) {
                if (reminderType.equals(reminderTypes[i])) {
                    position = i; // Set position if reminderType matches an item in the array
                    break; // Exit loop once position is found
                }
            }

            // Set the selected item in the Spinner if position is valid
            if (position != -1) {
                editReminderType.setSelection(position);
            }
        }

        // Set initial text for reminder datetime button
        TextView editSelectButton = findViewById(R.id.editSelectButton); // Assuming this is the button for datetime selection

        // Create a Calendar instance with default values (e.g., the current date and time)
        Calendar defaultCalendar = Calendar.getInstance();
        // Call the helper method to update the button text with the default values
        updateDatetimeButtonText(defaultCalendar);

        // Implement click listener for "Select Date & Time" button
        editSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch date picker dialog
                launchDateTimePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update reminder data
                String title = editTitle.getText().toString();
                String datetime = reminderDatetime; // Update this with formatted date and time if selected
                String type = editReminderType.getSelectedItem().toString();

                // Send updated reminder data back to ReminderMain
                Intent resultIntent = new Intent();
                resultIntent.putExtra("reminder_position", position);
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

    // Method to launch date and time picker dialogs
    private void launchDateTimePicker() {
        // Get current date and time
        final Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set selected date
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create and show TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set selected time
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Update datetime button text
                        updateDatetimeButtonText(calendar);
                    }
                }, currentHour, currentMinute, DateFormat.is24HourFormat(EditReminderActivity.this));
                timePickerDialog.show();
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    // Method to update the text of the datetime button based on selected date and time
    private void updateDatetimeButtonText(Calendar calendar) {
        // Format the selected date and time into a suitable string representation
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String datetimeString = dateFormat.format(calendar.getTime());

        // Update the text of the datetime button
        TextView editSelectButton = findViewById(R.id.addDatetime); // Assuming this is the button for datetime selection
        editSelectButton.setText(datetimeString);
    }
}
