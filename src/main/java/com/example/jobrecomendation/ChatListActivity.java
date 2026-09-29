package com.example.jobrecomendation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView emptyText;
    FirebaseFirestore db;
    String currentUserId;
    List<Chat> chatList;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.chatRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, this);
        recyclerView.setAdapter(chatAdapter);

        loadChats();
    }

    private void loadChats() {
        db.collection("chats")
                .whereEqualTo("employerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    chatList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Chat chat = new Chat();
                        chat.setId(doc.getId());
                        chat.setSeekerId(doc.getString("seekerId"));
                        chat.setSeekerName(doc.getString("seekerName"));
                        chat.setLastMessage(doc.getString("lastMessage"));
                        chatList.add(chat);
                    }
                    if (chatList.isEmpty()) {
                        emptyText.setVisibility(android.view.View.VISIBLE);
                        recyclerView.setVisibility(android.view.View.GONE);
                    } else {
                        emptyText.setVisibility(android.view.View.GONE);
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                        chatAdapter.notifyDataSetChanged();
                    }
                });
    }
}