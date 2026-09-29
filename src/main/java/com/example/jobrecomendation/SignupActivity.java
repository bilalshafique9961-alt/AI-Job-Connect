package com.example.jobrecomendation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText fullNameInput, emailInput, passwordInput, confirmPasswordInput;
    RadioGroup userTypeRadioGroup;
    Button signupButton;
    TextView loginLinkText;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String selectedUserType = "seeker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        signupButton = findViewById(R.id.signupButton);
        loginLinkText = findViewById(R.id.loginLinkText);

        // User type selection
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioSeeker) {
                selectedUserType = "seeker";
            } else if (checkedId == R.id.radioEmployer) {
                selectedUserType = "employer";
            }
        });

        // Signup button click
        signupButton.setOnClickListener(v -> performSignup());

        // Login link click
        loginLinkText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performSignup() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validation
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();

                    // Save user data to Firestore with role
                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", fullName);
                    user.put("email", email);
                    user.put("userType", selectedUserType);  // ← ROLE SAVE HO RAHA HAI
                    user.put("createdAt", System.currentTimeMillis());

                    db.collection("users").document(userId).set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Signup Successful! Role: " + selectedUserType, Toast.LENGTH_LONG).show();

                                // Navigate based on role
                                if (selectedUserType.equals("employer")) {
                                    Intent intent = new Intent(SignupActivity.this, EmployerDashboardActivity.class);
                                    intent.putExtra("USER_NAME", fullName);
                                    intent.putExtra("USER_TYPE", selectedUserType);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(SignupActivity.this, SeekerDashboardActivity.class);
                                    intent.putExtra("USER_NAME", fullName);
                                    intent.putExtra("USER_TYPE", selectedUserType);
                                    startActivity(intent);
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}