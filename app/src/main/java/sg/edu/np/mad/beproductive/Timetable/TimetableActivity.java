package sg.edu.np.mad.beproductive.Timetable;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import sg.edu.np.mad.beproductive.R;

public class TimetableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.timetable_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ZoneId zone = ZoneId.of("Singapore");
        LocalDate today = LocalDate.now(zone);
        String currentDate = "Date: " + today.toString();
        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(currentDate);

        Schedule userSchedule = new Schedule();
        userSchedule.onCreate();

        RecyclerView recyclerView = findViewById(R.id.timetableRecyclerView);
        LinearLayoutManager linLayoutManager = new LinearLayoutManager(this);
        TimetableAdapter tAdapter = new TimetableAdapter(userSchedule.getTimeslots(), this);

        recyclerView.setLayoutManager(linLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tAdapter);


    }
}
