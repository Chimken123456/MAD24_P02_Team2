package sg.edu.np.mad.beproductive.ChatRooms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.beproductive.R;

public class AddChatRoomActivity extends AppCompatActivity {
    private EditText etChatRoomName, etChatRoomDescription;
    private Button btnSaveChatRoom;
    private FirebaseFirestore db;
    private String userId; // Add this
    private String username; // Add this

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_room);

        db = FirebaseFirestore.getInstance();

        etChatRoomName = findViewById(R.id.etChatRoomName);
        etChatRoomDescription = findViewById(R.id.etChatRoomDescription);
        btnSaveChatRoom = findViewById(R.id.btnSaveChatRoom);

        // Retrieve user ID and username from intent
        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        btnSaveChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChatRoom();
            }
        });
    }

    private void saveChatRoom() {
        String name = etChatRoomName.getText().toString().trim();
        String description = etChatRoomDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etChatRoomName.setError("Name is required");
            return;
        }

        Map<String, Object> chatRoom = new HashMap<>();
        chatRoom.put("name", name);
        chatRoom.put("description", description);

        db.collection("chatrooms")
                .add(chatRoom)
                .addOnSuccessListener(documentReference -> {
                    String newChatRoomId = documentReference.getId();
                    Log.d("AddChatRoomActivity", "New chat room ID: " + newChatRoomId);
                    Toast.makeText(AddChatRoomActivity.this, "Chat Room added", Toast.LENGTH_SHORT).show();
                    // Pass the newly created chatRoomId to ChatMain activity
                    Intent intent = new Intent(AddChatRoomActivity.this, ChatMain.class);
                    intent.putExtra("chatRoomId", newChatRoomId); // Pass the new chatRoomId
                    intent.putExtra("userId", userId); // Pass other required information
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddChatRoomActivity.this, "Error adding chat room", Toast.LENGTH_SHORT).show());
    }
}

