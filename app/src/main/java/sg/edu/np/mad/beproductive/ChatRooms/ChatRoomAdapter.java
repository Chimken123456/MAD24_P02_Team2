package sg.edu.np.mad.beproductive.ChatRooms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad.beproductive.R;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoom> chatRoomList;
    private Context context;

    public ChatRoomAdapter(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.chatRoomName.setText(chatRoom.getName());
        holder.chatRoomDescription.setText(chatRoom.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatMain.class);
            intent.putExtra("chatRoomId", chatRoom.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        TextView chatRoomName;
        TextView chatRoomDescription;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            chatRoomName = itemView.findViewById(R.id.chatRoomName);
            chatRoomDescription = itemView.findViewById(R.id.chatRoomDescription);
        }
    }
}
