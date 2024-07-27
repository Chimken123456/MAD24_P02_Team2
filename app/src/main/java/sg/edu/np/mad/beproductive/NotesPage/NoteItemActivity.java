package sg.edu.np.mad.beproductive.NotesPage;

import android.app.PendingIntent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.Timetable.Timeslot;
import sg.edu.np.mad.beproductive.Timetable.TimetableActivity;
import sg.edu.np.mad.beproductive.Timetable.TimetableAdapter;
import sg.edu.np.mad.beproductive.User;

public class NoteItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.noteitem_layout);
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
        int noteid = receive.getIntExtra("noteId", 0);
        User user = new User(username,password,email);
        user.setId(id);

        Note currentNote = new Note("empty", "empty");

        //Firebase
        String path = "User/user" + String.valueOf(id+1) + "/notes/" + String.valueOf(noteid);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference dbRef = database.getReference(path);

        //Firebase implementation
        // change this to fetch single note
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String content = "";
                    String header = "";
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (Objects.equals(item.getKey(), "content")) {
                             content = String.valueOf(item.getValue());
                        }
                        if (Objects.equals(item.getKey(), "header")) {
                             header = String.valueOf(item.getValue());
                        }

                        Log.d("firebase", "key: " + item.getKey());

                    }
                    currentNote.setNote_header(header);
                    currentNote.setNote_content(content);

                    Log.d("firebase", "header: " + header);
                    Log.d("firebase", "content: " + content);
                    Log.d("firebase", "Fetch note" + String.valueOf(noteid) + "success");
                    //Inflate recyclerview
                    RecyclerView recyclerView = findViewById(R.id.noteitemRecyclerView);
                    LinearLayoutManager linLayoutManager = new LinearLayoutManager(NoteItemActivity.this);
                    NoteItemAdapter nAdapter = new NoteItemAdapter(currentNote, NoteItemActivity.this, id, username, password, email, noteid);

                    recyclerView.setLayoutManager(linLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(nAdapter);

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });




    }

}

