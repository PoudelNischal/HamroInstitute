package com.example.merainstitue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CourseCreateDialogFragment extends DialogFragment {

    private EditText courseTitleEditText, courseDescriptionEditText, courseTagsEditText, coursePriceEditText;
    private ImageView courseImageView;
    private String imageBase64 = ""; // Store the image in Base64 format

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    try {
                        Bitmap selectedImage = BitmapFactory.decodeStream(
                                requireActivity().getContentResolver().openInputStream(Objects.requireNonNull(result.getData().getData()))
                        );

                        // Compress the image before encoding to Base64
                        Bitmap compressedImage = compressImage(selectedImage);
                        imageBase64 = encodeImageToBase64(compressedImage);

                        // Set the compressed image in the ImageView
                        courseImageView.setImageBitmap(compressedImage);
                        Log.d("CourseCreate", "Image selected and encoded successfully");
                    } catch (Exception e) {
                        Log.e("CourseCreate", "Error selecting image", e);
                        Toast.makeText(getContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the dialog view
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_course, null);

        // Initialize views
        courseTitleEditText = dialogView.findViewById(R.id.courseTitle);
        courseDescriptionEditText = dialogView.findViewById(R.id.courseDescription);
        courseTagsEditText = dialogView.findViewById(R.id.courseTags);
        coursePriceEditText = dialogView.findViewById(R.id.coursePrice); // New field for price
        Button createCourseButton = dialogView.findViewById(R.id.createCourseButton);
        Button selectImageButton = dialogView.findViewById(R.id.selectThumbnailButton);
        courseImageView = dialogView.findViewById(R.id.thumbnailPreview);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);

        // Handle the select image button click
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Handle the create course button click
        createCourseButton.setOnClickListener(v -> {
            String title = courseTitleEditText.getText().toString().trim();
            String description = courseDescriptionEditText.getText().toString().trim();
            String tags = courseTagsEditText.getText().toString().trim();
            String priceText = coursePriceEditText.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty() && !imageBase64.isEmpty() && !priceText.isEmpty()) {
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                    return;
                }
                addCourseToFirestore(title, description, tags, imageBase64, price);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private Bitmap compressImage(Bitmap image) {
        int maxWidth = 800; // Max width for the image
        int maxHeight = 800; // Max height for the image

        // Get the current image width and height
        int width = image.getWidth();
        int height = image.getHeight();

        // Calculate the scaling factor
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Resize the image if needed
        if (scaleFactor < 1) {
            int newWidth = Math.round(width * scaleFactor);
            int newHeight = Math.round(height * scaleFactor);
            image = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
        }

        return image;
    }

    private String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the image to JPEG with 40% quality
        image.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addCourseToFirestore(String title, String description, String tags, String imageBase64, double price) {
        List<String> tagsList = Arrays.asList(tags.split(",")); // Convert to List

        Map<String, Object> courseData = new HashMap<>();
        String courseId = UUID.randomUUID().toString();
        courseData.put("courseId", courseId);
        courseData.put("title", title);
        courseData.put("description", description);
        courseData.put("tags", tagsList); // Store as List
        courseData.put("imageBase64", imageBase64);
        courseData.put("price", price); // Add price to Firestore data
        courseData.put("teacherId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Courses")
                .add(courseData)
                .addOnSuccessListener(documentReference -> Log.d("FirestoreSuccess", "Course created: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to create course", e));
    }
}

