package com.example.jobrecomendation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// ✅ ANDROID PDFBox (CV analysis ke liye)
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeekerDashboardActivity extends AppCompatActivity {

    TextView welcomeText, userNameText, roleText, cvStatusText, skillsText;
    Button logoutButton, uploadCvButton, analyzeSkillsButton;
    RecyclerView jobsRecyclerView;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    Uri pdfUri;
    String currentUserId;

    List<String> userSkills = new ArrayList<>();
    List<Job> jobList = new ArrayList<>();
    JobAdapter jobAdapter;

    private static final int PICK_PDF_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Initialize PDFBox for Android
        PDFBoxResourceLoader.init(getApplicationContext());

        setContentView(R.layout.activity_seeker_dashboard);

        // Firebase Initialization
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Check login session
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Views
        welcomeText = findViewById(R.id.welcomeText);
        userNameText = findViewById(R.id.userNameText);
        roleText = findViewById(R.id.roleText);
        cvStatusText = findViewById(R.id.cvStatusText);
        skillsText = findViewById(R.id.skillsText);

        logoutButton = findViewById(R.id.logoutButton);
        uploadCvButton = findViewById(R.id.uploadCvButton);
        analyzeSkillsButton = findViewById(R.id.analyzeSkillsButton);

        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobAdapter = new JobAdapter(jobList, this);
        jobsRecyclerView.setAdapter(jobAdapter);

        // Get user info from Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null) {
            userNameText.setText(userName);
            welcomeText.setText("Welcome " + userName + "!");
        }

        roleText.setText("Job Seeker");

        loadUserSkills();
        loadAllJobs();

        uploadCvButton.setOnClickListener(v -> pickPDF());
        analyzeSkillsButton.setOnClickListener(v -> analyzePDFAndExtractSkills());

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void pickPDF() {
        // ✅ Professional way to pick PDF (Standard Intent)
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CV (PDF)"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            cvStatusText.setText("CV selected. Click 'Analyze My Skills'");
            analyzeSkillsButton.setVisibility(View.VISIBLE);
        }
    }

    private void analyzePDFAndExtractSkills() {
        if (pdfUri == null) {
            Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
            PDDocument document = PDDocument.load(inputStream);

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            document.close();

            extractSkillsFromText(text);
            uploadPDFToStorage();

        } catch (Exception e) {
            Toast.makeText(this, "PDF Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void extractSkillsFromText(String text) {
        String[] commonSkills = {
                "Java", "Python", "Android", "Kotlin", "JavaScript", "React",
                "Node.js", "SQL", "Firebase", "AWS", "Docker", "Git",
                "C++", "PHP", "HTML", "CSS", "Machine Learning", "AI",
                "Data Science", "Flutter"
        };

        userSkills.clear();
        String lowerText = text.toLowerCase();

        for (String skill : commonSkills) {
            if (lowerText.contains(skill.toLowerCase())) {
                userSkills.add(skill);
            }
        }

        if (userSkills.isEmpty()) {
            skillsText.setText("No common skills found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : userSkills) {
                sb.append("• ").append(s).append("\n");
            }
            skillsText.setText(sb.toString());
            saveSkillsToFirestore();
        }
    }

    private void saveSkillsToFirestore() {
        Map<String, Object> map = new HashMap<>();
        map.put("skills", userSkills);
        map.put("updatedAt", System.currentTimeMillis());

        db.collection("userSkills").document(currentUserId).set(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Skills Analyzed & Saved!", Toast.LENGTH_SHORT).show();
                    matchJobsWithSkills();
                });
    }

    private void uploadPDFToStorage() {
        StorageReference ref = storageRef.child("cvs/" + currentUserId + ".pdf");
        ref.putFile(pdfUri)
                .addOnSuccessListener(t -> cvStatusText.setText("CV uploaded ✅"))
                .addOnFailureListener(e -> cvStatusText.setText("Upload failed"));
    }

    private void loadUserSkills() {
        db.collection("userSkills").document(currentUserId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        userSkills = (List<String>) doc.get("skills");
                        if (userSkills != null) matchJobsWithSkills();
                    }
                });
    }

    private void loadAllJobs() {
        db.collection("jobs").get()
                .addOnSuccessListener(snapshot -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Job job = new Job();
                        job.setId(doc.getId());
                        job.setTitle(doc.getString("title"));
                        job.setCompany(doc.getString("company"));
                        job.setLocation(doc.getString("location"));
                        job.setSalary(doc.getString("salary"));
                        
                        List<String> skills = (List<String>) doc.get("skills");
                        job.setSkills(skills != null ? skills : new ArrayList<>());
                        
                        jobList.add(job);
                    }
                    matchJobsWithSkills();
                });
    }

    private void matchJobsWithSkills() {
        if (userSkills == null || userSkills.isEmpty() || jobList.isEmpty()) {
            jobAdapter.notifyDataSetChanged();
            return;
        }

        for (Job job : jobList) {
            int matchCount = 0;
            List<String> jobSkills = job.getSkills();
            if (jobSkills != null && !jobSkills.isEmpty()) {
                for (String uSkill : userSkills) {
                    for (String jSkill : jobSkills) {
                        if (uSkill.equalsIgnoreCase(jSkill)) {
                            matchCount++;
                            break;
                        }
                    }
                }
                int score = (matchCount * 100) / jobSkills.size();
                job.setMatchScore(score);
            } else {
                job.setMatchScore(0);
            }
        }

        // ✅ Stable sorting using Collections.sort and Comparator
        Collections.sort(jobList, new Comparator<Job>() {
            @Override
            public int compare(Job a, Job b) {
                return Integer.compare(b.getMatchScore(), a.getMatchScore());
            }
        });

        jobAdapter.notifyDataSetChanged();
    }
}
