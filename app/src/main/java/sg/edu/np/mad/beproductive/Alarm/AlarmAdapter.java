package sg.edu.np.mad.beproductive.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private ArrayList<Alarm> data;
    private Context context;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Integer count = 0;
    public AlarmAdapter(ArrayList<Alarm> input,Context c)
    {
        this.data = input;
        this.context = c;
    }

    public AlarmViewHolder onCreateViewHolder(ViewGroup parent,int viewtType)
    {

        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_alarm_recycler_layout,
                parent,
                false);
        return new AlarmViewHolder(item);
    }

    public void onBindViewHolder(AlarmViewHolder holder, int position)
    {
        Alarm alarm = data.get(position);
        holder.txt.setText(alarm.getTime());
        holder.alarm_switch.setChecked(alarm.getChecked());

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User");
        DatabaseReference userRef = myRef.child("user"+String.valueOf(Global.getUser_Id() +1 ));
        DatabaseReference alarmRef = userRef.child("alarm");

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(context, AlarmEditor.class);
                Bundle extras = new Bundle();

                extras.putInt("User_Id" , Global.getUser_Id());
                extras.putString("Alarm_Time" ,alarm.getTime());
                extras.putBoolean("Alarm_Checked", alarm.getChecked());
                activity.putExtras(extras);

                context.startActivity(activity);
            }
        });
        holder.alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.alarm_switch.isChecked())
                {
                    String[] time = alarm.getTime().split(":");
                    Calendar calendar;
                    calendar = Calendar.getInstance();

                    String timeCurrent = String.valueOf(calendar.getTime());
                    String[] timeCurrentFormatted = timeCurrent.split(" ");
                    String timeHourMinCurrent = timeCurrentFormatted[3];
                    String[] time_time_array = timeHourMinCurrent.split(":");
                    String timeCompare = time_time_array[0] + time_time_array[1];


                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
                    calendar.set(Calendar.MINUTE,Integer.valueOf(time[1]));
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);



                    String time0 = String.valueOf(calendar.getTime());
                    String[] test = time0.split(" ");
                    String[] time_time_array1 = test[3].split(":");
                    String timeCompareTo = time_time_array1[0] + time_time_array1[1];
                    if(Integer.valueOf(timeCompare) > Integer.valueOf(timeCompareTo))
                    {
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(timeCurrentFormatted[2])+ 1);
                    }
                    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context,AlarmReceiver.class);
                    intent.putExtra("Alarm_Setter_Time",alarm.getTime());
                    intent.putExtra("User_id", Global.getUser_Id());
                    pendingIntent = PendingIntent.getBroadcast(context,alarm.getAlarm_id(),intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
                    Toast.makeText(context,"Alarm Set", Toast.LENGTH_SHORT).show();
                    Log.i("MAOMAOO" , String.valueOf(alarm.getAlarm_id()));
                    alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DataSnapshot dataSnapshot = task.getResult();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    if(snapshot.child("alarm").getValue().toString().equals(alarm.getTime()))
                                    {
                                        snapshot.getRef().child("checked").setValue(true);
                                    }
                                }

                            }
                        }
                    });
                }
                else //Not checked
                {
                    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context,AlarmReceiver.class);
                    intent.putExtra("Alarm_Setter_Time",alarm.getTime());
                    intent.putExtra("User_id", Global.getUser_Id());
                    pendingIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show();

                    alarmRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DataSnapshot dataSnapshot = task.getResult();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    if(snapshot.child("alarm").getValue().toString().equals(alarm.getTime()))
                                    {
                                        snapshot.getRef().child("checked").setValue(false);
                                    }
                                }
                            }
                        }
                    });
                }
            }


        });
    }
    public int getItemCount()
    {
        return data.size();
    }

}
