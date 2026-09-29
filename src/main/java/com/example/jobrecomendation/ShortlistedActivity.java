package com.example.jobrecomendation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ShortlistedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView emptyText;
    FirebaseFirestore db;
    List<Application> shortlistedList;
    ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortlisted);

        recyclerView = findViewById(R.id.shortlistedRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        shortlistedList = new ArrayList<>();
        adapter = new ApplicationAdapter(shortlistedList, this);
        recyclerView.setAdapter(adapter);

        loadShortlisted();
    }

    private void loadShortlisted() {
        db.collection("applications")
                .whereEqualTo("status", "shortlisted")
                .get()
                .addOnSuccessListener(snapshots -> {
                    shortlistedList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Application app = new Application();
                        app.setId(doc.getId());
                        app.setJobId(doc.getString("jobId"));
                        app.setJobTitle(doc.getString("jobTitle"));
                        app.setSeekerId(doc.getString("seekerId"));
                        app.setSeekerName(doc.getString("seekerName"));
                        app.setStatus(doc.getString("status"));
                        shortlistedList.add(app);
                    }
                    if (shortlistedList.isEmpty()) {
                        emptyText.setVisibility(android.view.View.VISIBLE);
                        recyclerView.setVisibility(android.view.View.GONE);
                    } else {
                        emptyText.setVisibility(android.view.View.GONE);
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}