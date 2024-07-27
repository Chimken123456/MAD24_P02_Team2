package sg.edu.np.mad.beproductive.NotesPage;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.Timetable.Timeslot;
import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;
import sg.edu.np.mad.beproductive.Timetable.TimetableAdapter;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListViewHolder> {
    ArrayList<Note> noteList;
    NotesListActivity context;
    int userId;
    String username;
    String password;
    String email;

    String path;

    HashMap updatemap;

    FirebaseDatabase database;

    DatabaseReference dbRef;

    private String header_text = "";

    public NotesListAdapter(ArrayList<Note> list,NotesListActivity activity, int id, String name, String pw, String mail) {
        noteList = list;
        context = activity;
        userId = id;
        username = name;
        password = pw;
        email = mail;
        path = "User/user" + String.valueOf(id+1) + "/notes";
        database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = database.getReference(path);
    }

    public NotesListViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){

        View noteListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_element, parent, false);
        return new NotesListViewHolder(noteListItem);
    }

    public void onBindViewHolder(NotesListViewHolder holder, int position) {
        Note temp = noteList.get(position);
        header_text = temp.getNote_header();
        holder.header.setText(header_text); //Change to proper note aft implementing db connection
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NLAD", String.valueOf(position));
                Bundle extras = new Bundle();
                extras.putInt("ID", userId);
                extras.putString("Username",username);
                extras.putString("Password",password);
                extras.putString("Email",email);
                extras.putInt("noteId", position);
                Intent intent = new Intent(context, NoteItemActivity.class);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete this note?");
        builder.setCancelable(true);
        //Restarts the activity on click
        builder.setPositiveButton("Confirm", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (noteList.size() == 1) {
                            dbRef.child(String.valueOf(position)).removeValue();
                        }

                        else {
                            for (int i = position; i < noteList.size(); i++){
                                if (i+1 == noteList.size()) {
                                    dbRef.child(String.valueOf(i)).removeValue();
                                    break;
                                }

                                DatabaseReference updateRef = dbRef.child(String.valueOf(i));
                                Note tempnote = noteList.get(i+1);

                                updatemap = new HashMap<>();
                                updatemap.put("header", tempnote.getNote_header());
                                updatemap.put("content", tempnote.getNote_content());
                                updateRef.setValue(updatemap);

                            }

                        }

                        context.restartActivity();
                    }
                });
        //Closes the alert dialog on click
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog deleteDialog = builder.create();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return noteList.size();
    }

}
