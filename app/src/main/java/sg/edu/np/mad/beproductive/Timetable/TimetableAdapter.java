package sg.edu.np.mad.beproductive.Timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.R;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {
    ArrayList<Timeslot> timeslotList;
    TimetableActivity context;

    private String activity_text = "";
    public TimetableAdapter(ArrayList<Timeslot> input, TimetableActivity activity) {
        timeslotList = input;
        context = activity;
    }

    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){
        View timeslot = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_item, parent, false);
        return new TimetableViewHolder(timeslot);
    }

    public void onBindViewHolder(TimetableViewHolder holder, int position) {
        Timeslot temp = timeslotList.get(position);
        String tempName = temp.getTime();
        int timeslot_id = temp.getTimeslot_id();
        holder.timeslot.setText(tempName);
        holder.desc.setText(temp.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //implement edit timetable functionality
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setTitle("Enter the activity for this timeslot");
        builder.setCancelable(true);

        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseHandler dbHandler = new DatabaseHandler(context);
                        activity_text = input.getText().toString();
                        dbHandler.updateActivity(activity_text, timeslot_id);
                        temp.setDescription(activity_text);
                        holder.desc.setText(temp.getDescription());

                    }
                });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog editInterface = builder.create();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInterface.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeslotList.size();
    }
}
