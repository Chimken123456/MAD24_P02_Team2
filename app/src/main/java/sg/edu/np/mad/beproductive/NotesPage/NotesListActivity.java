package sg.edu.np.mad.beproductive.NotesPage;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.Timetable.Timeslot;
import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;
import sg.edu.np.mad.beproductive.Timetable.TimetableAdapter;
import sg.edu.np.mad.beproductive.User;

public class NotesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Receive user information and create instance of User
        Intent receive = getIntent();
        int id = receive.getIntExtra("ID",0);
        String username = receive.getStringExtra("Username");
        String password = receive.getStringExtra("Password");
        String email = receive.getStringExtra("Email");
        User user = new User(username,password,email);
        user.setId(id);


        //Firebase
        String path = "User/user" + String.valueOf(id+1) + "/notes";
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference dbRef = database.getReference(path);


        ImageView backButton = findViewById(R.id.backbtn);
        //Start HomeMenu activity when clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("ID",id);
                extras.putString("Username",username);
                extras.putString("Password",password);
                extras.putString("Email",email);
                Intent intent = new Intent(NotesListActivity.this, HomeMenu.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        ArrayList<Note> noteList = new ArrayList<>();

        //Firebase implementation
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String header = String.valueOf(item.child("header").getValue());
                        String content = String.valueOf(item.child("content").getValue());

                        Note tempNote = new Note(header, content);
                        noteList.add(tempNote);
                        Log.d("firebase",item.getKey() + " " + header + " " + content);
                        //fix update text content on save
                    }
                    Log.d("firebase", "Fetch table success");

                    //Inflate recyclerview
                    RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);
                    LinearLayoutManager linLayoutManager = new LinearLayoutManager(NotesListActivity.this);
                    NotesListAdapter nAdapter = new NotesListAdapter(noteList,NotesListActivity.this, id, username, password, email);

                    recyclerView.setLayoutManager(linLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linLayoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setAdapter(nAdapter);

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });





        FloatingActionButton addNote = findViewById(R.id.addNote);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newnoteId = noteList.size();
                DatabaseReference newnote = dbRef.child(String.valueOf(newnoteId));
                HashMap noteMap = new HashMap();

                noteMap.put("header", "New note");
                noteMap.put("content", "Tap to edit note");

                newnote.setValue(noteMap);

                Bundle extras = new Bundle();
                extras.putInt("ID", id);
                extras.putString("Username",username);
                extras.putString("Password",password);
                extras.putString("Email",email);
                extras.putInt("noteId", newnoteId);
                Intent intent = new Intent(NotesListActivity.this, NoteItemActivity.class);
                intent.putExtras(extras);
                startActivity(intent);

            }
        });


    }
    //Method to restart the activity without transition
    protected void restartActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
