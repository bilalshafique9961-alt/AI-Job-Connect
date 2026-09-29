package com.example.jobrecomendation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployerDashboardActivity extends AppCompatActivity {

    TextView welcomeText, userNameText, roleText, statusText;
    Button logoutButton, postJobButton, myJobsButton, applicantsButton, shortlistedButton, chatButton;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        employerId = auth.getCurrentUser().getUid();

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        userNameText = findViewById(R.id.userNameText);
        roleText = findViewById(R.id.roleText);
        statusText = findViewById(R.id.statusText);
        logoutButton = findViewById(R.id.logoutButton);
        postJobButton = findViewById(R.id.postJobButton);
        myJobsButton = findViewById(R.id.myJobsButton);
        applicantsButton = findViewById(R.id.applicantsButton);
        shortlistedButton = findViewById(R.id.shortlistedButton);
        chatButton = findViewById(R.id.chatButton);

        // Get user data from intent
        String userName = getIntent().getStringExtra("USER_NAME");

        if (userName != null && !userName.isEmpty()) {
            userNameText.setText(userName);
            welcomeText.setText("Welcome " + userName + "!");
        } else if (auth.getCurrentUser() != null) {
            userNameText.setText(auth.getCurrentUser().getEmail());
            welcomeText.setText("Welcome!");
        }

        // Set role text
        roleText.setText("🏢 Employer");
        roleText.setTextColor(getColor(R.color.purple_500));
        statusText.setText("✅ Post jobs and find candidates!");

        // Load stats
        loadStats();

        // Post Job Button
        postJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerDashboardActivity.this, PostJobActivity.class);
            startActivity(intent);
        });

        // My Jobs Button
        myJobsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerDashboardActivity.this, EmployerJobsActivity.class);
            startActivity(intent);
        });

        // Applicants Button
        applicantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerDashboardActivity.this, ApplicantsListActivity.class);
            startActivity(intent);
        });

        // Shortlisted Button
        shortlistedButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerDashboardActivity.this, ShortlistedActivity.class);
            startActivity(intent);
        });

        // Chat Button
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerDashboardActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EmployerDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadStats() {
        // Count total jobs
        db.collection("jobs")
                .whereEqualTo("employerId", employerId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    TextView totalJobsText = findViewById(R.id.totalJobsText);
                    if (totalJobsText != null) {
                        totalJobsText.setText(String.valueOf(snapshots.size()));
                    }
                });

        // Count total applicants
        db.collection("applications")
                .whereEqualTo("employerId", employerId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    TextView totalApplicantsText = findViewById(R.id.totalApplicantsText);
                    if (totalApplicantsText != null) {
                        totalApplicantsText.setText(String.valueOf(snapshots.size()));
                    }
                });
    }
}