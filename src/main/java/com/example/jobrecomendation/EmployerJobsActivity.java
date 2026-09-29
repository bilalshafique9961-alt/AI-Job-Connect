package com.example.jobrecomendation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class EmployerJobsActivity extends AppCompatActivity {

    RecyclerView jobsRecyclerView;
    FirebaseAuth auth;
    FirebaseFirestore db;
    List<Job> jobList;
    JobAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList, this);
        jobsRecyclerView.setAdapter(jobAdapter);

        loadJobs();
    }

    private void loadJobs() {
        db.collection("jobs")
                .whereEqualTo("employerId", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(snapshots -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Job job = new Job();
                        job.setId(doc.getId());
                        job.setTitle(doc.getString("title"));
                        job.setCompany(doc.getString("company"));
                        job.setLocation(doc.getString("location"));
                        job.setSalary(doc.getString("salary"));
                        job.setApplicantsCount(0); // Will be updated
                        jobList.add(job);
                    }
                    jobAdapter.notifyDataSetChanged();
                });
    }
}