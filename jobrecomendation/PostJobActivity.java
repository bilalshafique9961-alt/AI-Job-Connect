package com.example.jobrecomendation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostJobActivity extends AppCompatActivity {

    EditText jobTitleInput, companyNameInput, locationInput, salaryInput;
    EditText experienceInput, skillsInput, descriptionInput;
    EditText question1Input, question2Input, question3Input;
    Button submitButton;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        jobTitleInput = findViewById(R.id.jobTitleInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        locationInput = findViewById(R.id.locationInput);
        salaryInput = findViewById(R.id.salaryInput);
        experienceInput = findViewById(R.id.experienceInput);
        skillsInput = findViewById(R.id.skillsInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        question1Input = findViewById(R.id.question1Input);
        question2Input = findViewById(R.id.question2Input);
        question3Input = findViewById(R.id.question3Input);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        String title = jobTitleInput.getText().toString().trim();
        String company = companyNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String salary = salaryInput.getText().toString().trim();
        String experience = experienceInput.getText().toString().trim();
        String skillsStr = skillsInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            jobTitleInput.setError("Job title required");
            return;
        }
        if (company.isEmpty()) {
            companyNameInput.setError("Company name required");
            return;
        }
        if (skillsStr.isEmpty()) {
            skillsInput.setError("Skills required");
            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Description required");
            return;
        }

        // Convert skills to list
        List<String> skills = Arrays.asList(skillsStr.split(","));
        for (int i = 0; i < skills.size(); i++) {
            skills.set(i, skills.get(i).trim());
        }

        // Collect interview questions
        List<String> questions = new ArrayList<>();
        if (!question1Input.getText().toString().trim().isEmpty()) {
            questions.add(question1Input.getText().toString().trim());
        }
        if (!question2Input.getText().toString().trim().isEmpty()) {
            questions.add(question2Input.getText().toString().trim());
        }
        if (!question3Input.getText().toString().trim().isEmpty()) {
            questions.add(question3Input.getText().toString().trim());
        }

        // Create job object
        Map<String, Object> job = new HashMap<>();
        job.put("title", title);
        job.put("company", company);
        job.put("location", location.isEmpty() ? "Not specified" : location);
        job.put("salary", salary.isEmpty() ? "Not specified" : salary);
        job.put("experience", experience.isEmpty() ? "Not specified" : experience);
        job.put("skills", skills);
        job.put("description", description);
        job.put("questions", questions);
        job.put("employerId", auth.getCurrentUser().getUid());
        job.put("createdAt", System.currentTimeMillis());

        db.collection("jobs").add(job)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Job posted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}