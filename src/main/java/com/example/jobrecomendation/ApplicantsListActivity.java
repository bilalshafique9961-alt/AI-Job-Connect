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

public class ApplicantsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView emptyText;
    FirebaseFirestore db;
    List<Application> applicationList;
    ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);

        recyclerView = findViewById(R.id.applicantsRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        applicationList = new ArrayList<>();
        adapter = new ApplicationAdapter(applicationList, this);
        recyclerView.setAdapter(adapter);

        loadApplicants();
    }

    private void loadApplicants() {
        db.collection("applications")
                .get()
                .addOnSuccessListener(snapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Application app = new Application();
                        app.setId(doc.getId());
                        app.setJobId(doc.getString("jobId"));
                        app.setJobTitle(doc.getString("jobTitle"));
                        app.setSeekerId(doc.getString("seekerId"));
                        app.setSeekerName(doc.getString("seekerName"));
                        app.setStatus(doc.getString("status"));
                        applicationList.add(app);
                    }
                    if (applicationList.isEmpty()) {
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