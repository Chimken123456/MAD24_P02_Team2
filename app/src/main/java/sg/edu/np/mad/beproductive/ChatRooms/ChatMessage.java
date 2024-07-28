package sg.edu.np.mad.beproductive.ChatRooms;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String senderId;
    private String message;
    private Timestamp timestamp;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = Timestamp.now();
    }

    // Getters and setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
