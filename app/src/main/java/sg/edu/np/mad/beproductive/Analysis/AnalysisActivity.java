package sg.edu.np.mad.beproductive.Analysis;

import static java.lang.Math.floor;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;


public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.analysis_appusage);

        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            // Navigate the user to the permission settings
            Intent getPermIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(getPermIntent);
        }
        /* Get perms */

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        //Back button
        ImageView backButton = findViewById(R.id.backbtn);
        //Start HomeMenu activity when clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AnalysisActivity.this, HomeMenu.class);
                startActivity(intent);
            }
        });


        ArrayList<String> appInfoList = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        // 86400000ms is 1 day
        long getOneDay = 86400000;
        long oneMonth = getOneDay * 30;
        long startTime = endTime - oneMonth;

        // get List of data
        List<UsageStats> statsUsageList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime);
        // Loop all data inside appInfoList
        if (statsUsageList != null) {
            ArrayList<ArrayList<String>> appList = new ArrayList<>();
            for (UsageStats usageStats : statsUsageList) {
                ArrayList<String> appInfo = new ArrayList<>();
                // Skips apps that has not been visible
                if (usageStats.getTotalTimeVisible()==0){
                    continue;
                }
                String appShortName = usageStats.getPackageName().substring(usageStats.getPackageName().lastIndexOf('.') + 1);
                appInfoList.add(appShortName);
                long timeUsed = usageStats.getTotalTimeInForeground();
                // Converting Time
                long hoursUsed = (long) floor(timeUsed/3600000);
                long minutesUsed = (long) floor((timeUsed%3600000)/60000);
                long secondsUsed = (long) floor((timeUsed%60000)/1000);
                appInfoList.add(String.format("%o Hours %o Minutes %o Seconds",hoursUsed,minutesUsed,secondsUsed));
            }
        }
        else{
            appInfoList.add("None");
        }
        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.appRecyclerView);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(appInfoList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}
