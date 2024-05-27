package sg.edu.np.mad.beproductive.Timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import sg.edu.np.mad.beproductive.R;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {
    ArrayList<Timeslot> timeslotList;
    TimetableActivity context;

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
        holder.timeslot.setText(tempName);
        holder.desc.setText(temp.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //implement edit timetable functionality

        builder.setTitle("Enter the activity for this timeslot");
        builder.setCancelable(true);
        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                    }
                });
        builder.setNegativeButton("Close", null);
        //holder.edit.setOnClickListener(); //fix later
    }

    @Override
    public int getItemCount() {
        return timeslotList.size();
    }
}
