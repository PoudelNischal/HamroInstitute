package com.example.merainstitue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileBottomSheetFragment extends BottomSheetDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText;
    private Button saveChangesButton, uploadImageButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String profileImageBase64;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_profile_bottom_sheet, container, false);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        profileImageView = rootView.findViewById(R.id.profileImagePreview);
        nameEditText = rootView.findViewById(R.id.editNameEditText);
        emailEditText = rootView.findViewById(R.id.editEmailEditText);
        saveChangesButton = rootView.findViewById(R.id.saveProfileChangesButton);
        uploadImageButton = rootView.findViewById(R.id.uploadImageButton);

        // Fetch and display current user data
        fetchUserData();

        // Handle image upload button
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        // Handle save changes button
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());

        return rootView;
    }

    private void fetchUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    String email = documentSnapshot.getString("email");
                    String base64Image = documentSnapshot.getString("profileImage");

                    if (fullName != null) {
                        nameEditText.setText(fullName);
                    }

                    if (email != null) {
                        emailEditText.setText(email);
                    }

                    if (base64Image != null) {
                        try {
                            byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                            profileImageView.setImageBitmap(decodedBitmap);
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImageView.setImageBitmap(selectedImage);

                // Convert image to Base64
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] imageBytes = outputStream.toByteArray();
                profileImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProfileChanges() {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedEmail = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedEmail)) {
            Toast.makeText(getContext(), "Name and email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            // Fetch the current data from Firestore
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String currentFullName = documentSnapshot.getString("fullName");
                    String currentEmail = documentSnapshot.getString("email");
                    String currentProfileImage = documentSnapshot.getString("profileImage");

                    // Check if the name or email has changed
                    boolean nameChanged = !updatedName.equals(currentFullName);
                    boolean emailChanged = !updatedEmail.equals(currentEmail);
                    boolean imageChanged = profileImageBase64 != null && !profileImageBase64.equals(currentProfileImage);

                    // Prepare the update map
                    Map<String, Object> updateData = new HashMap<>();

                    if (nameChanged) {
                        updateData.put("fullName", updatedName);
                    }
                    if (emailChanged) {
                        updateData.put("email", updatedEmail);
                    }
                    if (imageChanged) {
                        updateData.put("profileImage", profileImageBase64);
                    }

                    // Only update the changed fields
                    if (!updateData.isEmpty()) {
                        userRef.update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No changes to update
                        Toast.makeText(getContext(), "No changes to update", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            });
        }
    }

}
