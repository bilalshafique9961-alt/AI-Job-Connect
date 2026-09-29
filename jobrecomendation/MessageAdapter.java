package com.example.jobrecomendation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messageList.get(position);
        holder.messageText.setText(msg.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();

        if (msg.getSenderId().equals(currentUserId)) {
            params.gravity = Gravity.END;
            holder.messageText.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_500));
            holder.messageText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            params.gravity = Gravity.START;
            holder.messageText.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_light));
            holder.messageText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
        holder.messageText.setLayoutParams(params);
        holder.messageText.setPadding(30, 20, 30, 20);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}