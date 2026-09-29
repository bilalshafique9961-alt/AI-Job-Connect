package com.example.jobrecomendation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText resetEmailInput;
    private Button sendResetLinkButton;
    private TextView backToLoginText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        resetEmailInput = findViewById(R.id.resetEmailInput);
        sendResetLinkButton = findViewById(R.id.sendResetLinkButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        // Pre-fill email if passed from LoginActivity
        String email = getIntent().getStringExtra("EMAIL");
        if (email != null && !email.isEmpty()) {
            resetEmailInput.setText(email);
        }

        sendResetLinkButton.setOnClickListener(v -> {
            String mail = resetEmailInput.getText().toString().trim();
            if (mail.isEmpty()) {
                resetEmailInput.setError("Email is required");
            } else {
                sendResetEmail(mail);
            }
        });

        backToLoginText.setOnClickListener(v -> finish());
    }

    private void sendResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                    finish(); // Go back to login after sending
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}