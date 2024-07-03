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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.R;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {
    ArrayList<Timeslot> timeslotList;
    TimetableActivity context;
    int userId;
    String path;
    FirebaseDatabase database;
    DatabaseReference dbRef;

    private String activity_text = "";
    public TimetableAdapter(ArrayList<Timeslot> input, TimetableActivity activity, int userid) {
        timeslotList = input;
        context = activity;
        userId = userid;
        path = "User/user" + String.valueOf(userid) + "/schedule";
        database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = database.getReference(path);
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

        //Alert dialog that prompts the user for input
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(temp.getDescription());
        builder.setView(input);
        builder.setTitle("Enter the activity for this timeslot");
        builder.setCancelable(true);

        //Update the text shown for the current timeslot and updates the corresponding entry in the databse
        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        DatabaseHandler dbHandler = new DatabaseHandler(context);
                        activity_text = input.getText().toString();
//                        dbHandler.updateActivity(activity_text, userId, tempName);
                        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful())
                                {

                                    DatabaseReference timeslot = dbRef.child(String.valueOf(id));

                                    HashMap tempMap = new HashMap();

                                    tempMap.put("time", tempName);
                                    tempMap.put("desc", activity_text);

                                    timeslot.setValue(tempMap);

                                }

                            }
                        });
                        temp.setDescription(activity_text);
                        holder.desc.setText(temp.getDescription());

                    }
                });
        //Close the alert dialog on click
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        //Initialise an instance of the alert dialog
        AlertDialog editInterface = builder.create();
        //Show the alert dialog on click
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
