package com.example.merainstitue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton; // Login button
    private Button signupButton; // Signup button
    private EditText emailEditText, passwordEditText; // Input fields
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login_activity);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.register_button);
        emailEditText = findViewById(R.id.email); // Ensure your layout has this ID
        passwordEditText = findViewById(R.id.password); // Ensure your layout has this ID

        // Signup button click listener
        signupButton.setOnClickListener(v -> {
            Log.d("LoginActivity", "Signup button clicked");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required!");
            passwordEditText.requestFocus();
            return;
        }

        // Authenticate with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful, navigate to MainActivity
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Login failed
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Log.e("LoginActivity", "Login error: " + errorMessage);
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
