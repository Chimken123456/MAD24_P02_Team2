package sg.edu.np.mad.beproductive.ChatRooms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.R;

public class ChatMain extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private FirebaseFirestore db;
    private String chatRoomId;
    private String userId;
    private String username;
    private List<ChatMessage> messageList;
    private ChatMessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        // Retrieve user ID and other information from intent
        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        Log.d("ChatMain", "Retrieved chat room ID: " + chatRoomId);

        if (chatRoomId == null) {
            // Handle the error, e.g., show a message and finish the activity
            Toast.makeText(this, "Chat room ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            messageInput.setError("Message is required");
            return;
        }

        ChatMessage chatMessage = new ChatMessage(userId, message);

        db.collection("chatrooms")
                .document(chatRoomId)
                .collection("messages")
                .add(chatMessage)
                .addOnSuccessListener(documentReference -> {
                    messageInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(ChatMain.this, "Error sending message", Toast.LENGTH_SHORT).show());
    }

    private void loadMessages() {
        if (chatRoomId == null) {
            Toast.makeText(this, "Chat room ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("chatrooms")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(ChatMain.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (documentChange.getType()) {
                                case ADDED:
                                    ChatMessage chatMessage = documentChange.getDocument().toObject(ChatMessage.class);
                                    messageList.add(chatMessage);
                                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                                    messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                                    break;
                                case MODIFIED:
                                case REMOVED:
                                    // Handle modifications or removals if needed
                                    break;
                            }
                        }
                    }
                });
    }
}
