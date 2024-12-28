package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText fullNameEditText, emailEditText, passwordEditText;
    private MaterialButton registerButton;
    private ImageView profileImageView;
    private MaterialButton selectImageButton;
    private RadioGroup roleGroup;
    private RadioButton studentRadioButton, teacherRadioButton;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // ActivityResultLauncher for image selection
    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        // Convert the selected image into a Bitmap
                        Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // Display the image in the ImageView
                        profileImageView.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("RegisterActivity", "Error selecting image: " + e.getMessage());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize Firebase Authentication and Firestore Reference
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        fullNameEditText = findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);
        profileImageView = findViewById(R.id.profile_image);
        selectImageButton = findViewById(R.id.select_image_button);

        // Initialize RadioGroup for role selection
        roleGroup = findViewById(R.id.role_group);
        studentRadioButton = findViewById(R.id.student_radio_button);
        teacherRadioButton = findViewById(R.id.teacher_radio_button);

        // Set up the register button listener
        registerButton.setOnClickListener(v -> registerUser());

        // Set up the image select button listener
        selectImageButton.setOnClickListener(v -> selectImageLauncher.launch("image/*"));
    }

    private void registerUser() {
        // Get input values
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input fields
        if (!validateInputs(fullName, email, password)) return;

        // Get the selected role
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        String role = selectedRoleId == R.id.student_radio_button ? "student" : "teacher";

        // Create a user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the currently authenticated user
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Show success message
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Convert the image to Base64 string and save user data
                            convertAndSaveUserData(user.getUid(), fullName, email, role);

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

    private void convertAndSaveUserData(String userId, String fullName, String email, String role) {
        // Check if an image is selected
        if (profileImageView.getDrawable() == null) {
            Toast.makeText(RegisterActivity.this, "Please select a profile image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the ImageView's drawable to a Bitmap
        Bitmap profileImage = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();

        // Resize the image to reduce resolution
        int maxWidth = 800;  // Max width for the image
        int maxHeight = 800; // Max height for the image

        // Get the current image width and height
        int width = profileImage.getWidth();
        int height = profileImage.getHeight();

        // Calculate the scaling factor
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Resize the image if needed
        if (scaleFactor < 1) {
            int newWidth = Math.round(width * scaleFactor);
            int newHeight = Math.round(height * scaleFactor);
            profileImage = Bitmap.createScaledBitmap(profileImage, newWidth, newHeight, true);
        }

        // Compress the image with a reduced quality
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress to JPEG with 40% quality (adjust quality as needed)
        profileImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Encode the compressed image to Base64
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Check if the compressed image still exceeds the size limit
        if (encodedImage.length() > 10000000) { // 10 MB in characters
            Toast.makeText(this, "Image too large, even after compression!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare user data for Firestore
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        userMap.put("role", role); // Save role as teacher or student
        userMap.put("profileImage", encodedImage); // Save the Base64 encoded image

        // Create a reference to the "users" collection
        firestore.collection("users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Failed to save user data. Try again.";
                        Log.e("Firestore", "Error saving data: " + errorMessage);
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private String encodeImageToBase64(Bitmap image) {
        // Convert the Bitmap image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
