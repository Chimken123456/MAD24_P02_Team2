package sg.edu.np.mad.beproductive.Reminders;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.R;

public class ReminderMain extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int REQUEST_ADD_REMINDER = 2;
    private static final int REQUEST_EDIT_REMINDER = 3;

    private List<Reminder> reminderList = new ArrayList<>();
    private ReminderAdapter reminderAdapter;
    private DatabaseHandler db;

    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reminder_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHandler(this);
        reminderList = db.getAllReminders();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditDeleteDialog(position);
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteConfirmationDialog(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reminderAdapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ReminderMain.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReminderMain.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                } else {
                    proceedToReminderActivity();
                }
            }
        });

        Button deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllReminders();
            }
        });

        findViewById(R.id.backbtn).setOnClickListener(v -> finish());

        if (reminderList.isEmpty()) {
            findViewById(R.id.recyclerView).setVisibility(View.GONE);
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    private void showEditDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Reminder reminder = reminderList.get(position);
                            Intent intent = new Intent(ReminderMain.this, EditReminderActivity.class);
                            intent.putExtra("reminder_position", position);
                            intent.putExtra("reminder_id", reminder.getId());
                            intent.putExtra("reminder_title", reminder.getTitle());
                            intent.putExtra("reminder_datetime", reminder.getDatetime());
                            intent.putExtra("reminder_type", reminder.getType());
                            startActivityForResult(intent, REQUEST_EDIT_REMINDER);
                        } else if (which == 1) {
                            showDeleteConfirmationDialog(position);
                        }
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReminder(position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteReminder(int position) {
        db.deleteReminder(reminderList.get(position));
        reminderList.remove(position);
        reminderAdapter.notifyItemRemoved(position);
    }

    private void deleteAllReminders() {
        for (Reminder reminder : reminderList) {
            db.deleteReminder(reminder);
        }
        reminderList.clear();
        reminderAdapter.notifyDataSetChanged();
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_REMINDER) {
                String title = data.getStringExtra("reminder_title");
                String datetime = data.getStringExtra("reminder_datetime");
                String type = data.getStringExtra("reminder_type");

                Reminder newReminder = new Reminder(title, datetime, type);
                db.addReminder(newReminder);
                reminderList.add(newReminder);
                reminderAdapter.notifyItemInserted(reminderList.size() - 1);

                findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                findViewById(R.id.empty).setVisibility(View.GONE);
            } else if (requestCode == REQUEST_EDIT_REMINDER) {
                int position = data.getIntExtra("reminder_position", -1);
                int id = data.getIntExtra("reminder_id", -1);
                String title = data.getStringExtra("reminder_title");
                String datetime = data.getStringExtra("reminder_datetime");
                String type = data.getStringExtra("reminder_type");

                if (position != -1 && id != -1) {
                    Reminder reminder = reminderList.get(position);
                    reminder.setTitle(title);
                    reminder.setDatetime(datetime);
                    reminder.setType(type);

                    db.updateReminder(reminder);
                    reminderAdapter.notifyItemChanged(position);
                }
            }
        }
    }



    private void proceedToReminderActivity() {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivityForResult(intent, REQUEST_ADD_REMINDER);
    }
}
