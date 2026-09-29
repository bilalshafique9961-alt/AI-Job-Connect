package com.example.jobrecomendation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    TextView seekerNameText;
    EditText messageInput;
    Button sendButton;
    RecyclerView recyclerView;

    FirebaseFirestore db;
    String currentUserId;
    String seekerId, seekerName;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        seekerId = getIntent().getStringExtra("SEEKER_ID");
        seekerName = getIntent().getStringExtra("SEEKER_NAME");

        seekerNameText = findViewById(R.id.seekerNameText);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.chatRecyclerView);

        if (seekerName != null) {
            seekerNameText.setText(seekerName);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setAdapter(messageAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        String chatId = currentUserId + "_" + seekerId;
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {
                        messageList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Message msg = new Message();
                            msg.setSenderId(doc.getString("senderId"));
                            msg.setText(doc.getString("text"));
                            msg.setTimestamp(doc.getLong("timestamp"));
                            messageList.add(msg);
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", currentUserId);
        message.put("text", text);
        message.put("timestamp", System.currentTimeMillis());

        String chatId = currentUserId + "_" + seekerId;

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(aVoid -> {
                    messageInput.setText("");

                    // Update last message in chat document
                    Map<String, Object> lastMsg = new HashMap<>();
                    lastMsg.put("lastMessage", text);
                    lastMsg.put("lastMessageTime", System.currentTimeMillis());
                    db.collection("chats").document(chatId).set(lastMsg);
                });
    }
}