package sg.edu.np.mad.beproductive.ChatRooms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.R;

public class ChatRoomsActivity extends AppCompatActivity {
    private RecyclerView chatRoomsRecyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private List<ChatRoom> chatRoomList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        db = FirebaseFirestore.getInstance();

        chatRoomsRecyclerView = findViewById(R.id.chatRoomsRecyclerView);
        chatRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRoomList = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(chatRoomList);
        chatRoomsRecyclerView.setAdapter(chatRoomAdapter);

        FloatingActionButton fabAddChatRoom = findViewById(R.id.fabAddChatRoom);
        fabAddChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to add chat room activity
                Intent intent = new Intent(ChatRoomsActivity.this, AddChatRoomActivity.class);
                startActivity(intent);
            }
        });

        loadChatRooms();
    }

    private void loadChatRooms() {
        db.collection("chatrooms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatRoomList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            ChatRoom chatRoom = document.toObject(ChatRoom.class);
                            chatRoomList.add(chatRoom);
                        }
                        chatRoomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatRoomsActivity.this, "Error getting chat rooms", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
