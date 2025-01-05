package com.example.merainstitue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, signupButton; // Buttons for login and signup
    private EditText emailEditText, passwordEditText; // Input fields
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login state before showing login screen
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isValidLogin = prefs.getBoolean("isValidLogin", false);
        if (isValidLogin) {
            redirectToMainActivity(); // Skip login if already logged in
            return;
        }

        setContentView(R.layout.fragment_login_activity);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.register_button);
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        // Signup button click listener
        signupButton.setOnClickListener(v -> {
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

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the logged-in user's ID
                        String userId = mAuth.getCurrentUser().getUid();

                        // Fetch the user's role from Firestore
                        FirebaseFirestore.getInstance()
                                .collection("users") // Replace "Users" with your Firestore collection name
                                .document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Get the user's role
                                        String role = documentSnapshot.getString("role"); // Assumes "role" field exists in Firestore

                                        if (role != null) {
                                            // Save the role and login state in SharedPreferences
                                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putBoolean("isValidLogin", true);
                                            editor.putString("role", role);
                                            editor.apply();

                                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                            redirectToMainActivity();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User role not found!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("LoginActivity", "Error fetching user role", e);
                                    Toast.makeText(LoginActivity.this, "Error fetching user data!", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
