package com.example.jobrecomendation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> chatList;
    private Context context;

    public ChatAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.nameText.setText(chat.getSeekerName());

        String lastMsg = chat.getLastMessage();
        if (lastMsg != null && !lastMsg.isEmpty()) {
            holder.messageText.setText(lastMsg);
        } else {
            holder.messageText.setText("No messages yet");
        }

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatDetailActivity.class);
            intent.putExtra("SEEKER_ID", chat.getSeekerId());
            intent.putExtra("SEEKER_NAME", chat.getSeekerName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, messageText;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.seekerNameText);
            messageText = itemView.findViewById(R.id.lastMessageText);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}