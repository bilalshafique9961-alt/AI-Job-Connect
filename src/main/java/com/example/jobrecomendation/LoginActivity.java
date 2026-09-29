package com.example.jobrecomendation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView signupLinkText, forgotPasswordText;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupLinkText = findViewById(R.id.signupLinkText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Check if already logged in
        if (auth.getCurrentUser() != null) {
            checkUserTypeAndNavigate(auth.getCurrentUser().getUid());
        }

        // Login button click
        loginButton.setOnClickListener(v -> performLogin());

        // Forgot password click - Opens new activity
        forgotPasswordText.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("EMAIL", email); // Pass email if user already typed it
            startActivity(intent);
        });

        // Signup link click
        signupLinkText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    checkUserTypeAndNavigate(authResult.getUser().getUid());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserTypeAndNavigate(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String userType = document.getString("userType");
                        String fullName = document.getString("fullName");

                        // Default to seeker if userType is null
                        if (userType == null) {
                            userType = "seeker";
                        }

                        if (userType.equals("employer")) {
                            // Navigate to Employer Dashboard
                            Intent intent = new Intent(LoginActivity.this, EmployerDashboardActivity.class);
                            intent.putExtra("USER_NAME", fullName);
                            intent.putExtra("USER_TYPE", userType);
                            startActivity(intent);
                        } else {
                            // Navigate to Job Seeker Dashboard
                            Intent intent = new Intent(LoginActivity.this, SeekerDashboardActivity.class);
                            intent.putExtra("USER_NAME", fullName);
                            intent.putExtra("USER_TYPE", userType);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}