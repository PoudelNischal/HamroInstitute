package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText fullNameEditText, emailEditText, passwordEditText;
    private MaterialButton registerButton;
    private ImageView profileImageView;
    private MaterialButton selectImageButton;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize Firebase Authentication and Realtime Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        fullNameEditText = findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);
        profileImageView = findViewById(R.id.profile_image);
        selectImageButton = findViewById(R.id.select_image_button);

        // Set up the register button listener
        registerButton.setOnClickListener(v -> registerUser());

        // Set up the image select button listener
        selectImageButton.setOnClickListener(v -> openImageChooser());
    }

    private void registerUser() {
        // Get input values
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input fields
        if (!validateInputs(fullName, email, password)) return;

        // Create a user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the currently authenticated user
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Show success message
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Save user to Firebase Realtime Database
                            saveUserToDatabase(user.getUid(), fullName, email);

                            // Navigate to MainActivity
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        }
                    } else {
                        // Handle registration failure
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed. Try again!";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String fullName, String email, String password) {
        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Full name is required!");
            fullNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required!");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required!");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters!");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void saveUserToDatabase(String userId, String fullName, String email) {
        // Prepare user data for the database
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);

        // Save user data to Firebase Realtime Database
        databaseReference.child(userId).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Failed to save user data. Try again.";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Convert the selected image into a Bitmap
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Display the image in the ImageView
                profileImageView.setImageBitmap(selectedImage);

                // Optionally, save the image locally (in your case, it will be stored in internal storage)
                saveImageLocally(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("RegisterActivity", "Error selecting image: " + e.getMessage());
            }
        }
    }

    private void saveImageLocally(Bitmap image) {
        try {
            // Save the image as a PNG file in the app's internal storage
            FileOutputStream fos = openFileOutput("profile_image.png", MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.d("RegisterActivity", "Image saved locally");

            // Optionally, you could save the image path in SharedPreferences or Firebase
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("RegisterActivity", "Error saving image locally: " + e.getMessage());
        }
    }
}
