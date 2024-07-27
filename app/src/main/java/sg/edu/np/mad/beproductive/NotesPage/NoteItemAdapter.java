package sg.edu.np.mad.beproductive.NotesPage;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.beproductive.NotesAppWidgetProvider;
import sg.edu.np.mad.beproductive.R;

public class NoteItemAdapter extends RecyclerView.Adapter<NoteItemViewHolder> {
    Note currentnote;
    NoteItemActivity context;
    int userId;
    int noteId;
    String username;
    String password;
    String email;


    String path;

    FirebaseDatabase database;

    DatabaseReference dbRef;

    private String header_text = "";
    private String content;
    public NoteItemAdapter(Note note, NoteItemActivity activity, int id, String name, String pw, String mail, int noteid) {
        currentnote = note;
        context = activity;
        userId = id;
        username = name;
        password = pw;
        email = mail;
        noteId = noteid;
        path = "User/user" + String.valueOf(id+1) + "/notes";
        database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dbRef = database.getReference(path);
    }

    public NoteItemViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){

        View noteListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteItemViewHolder(noteListItem);
    }

    public void onBindViewHolder(NoteItemViewHolder holder, int position) {
        content = currentnote.getNote_content();
        header_text = currentnote.getNote_header();
        holder.notes_field.setText(content);
        holder.edit_note_header.setText(header_text);

        holder.edit_note_header.addTextChangedListener(new CustomTextWatcher() {
            Boolean hwasEdited = false;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (hwasEdited) {
                    hwasEdited = false;
                    return;
                }

                // prevent infinite loop
                hwasEdited = true;
                currentnote.setNote_header(s.toString());
                Log.i("CTW", "afterHeaderChanged: " + s);
                Log.i("CTW", "noteHeaderUpdate: " + currentnote.getNote_header());
            }

        });

        holder.notes_field.addTextChangedListener(new CustomTextWatcher() {
            Boolean wasEdited = false;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (wasEdited) {
                    wasEdited = false;
                    return;
                }

                // prevent infinite loop
                wasEdited = true;
                currentnote.setNote_content(s.toString());
                Log.i("CTW", "afterTextChanged: " + s);
                Log.i("CTW", "noteUpdate: " + currentnote.getNote_content());
            }

        });

        holder.back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DatabaseReference updatenote = dbRef.child(String.valueOf(noteId));
                HashMap tempmap = new HashMap();
                tempmap.put("header", currentnote.getNote_header());
                tempmap.put("content", currentnote.getNote_content());
                updatenote.setValue(tempmap);



                Bundle extras = new Bundle();
                extras.putInt("ID",userId);
                extras.putString("Username",username);
                extras.putString("Password",password);
                extras.putString("Email",email);
                Intent intent = new Intent(context, NotesListActivity.class);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
