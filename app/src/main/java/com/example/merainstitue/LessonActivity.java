package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.merainstitue.lesson.Lesson;
import com.example.merainstitue.lesson.LessonAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class LessonActivity extends AppCompatActivity {

    private static final String TAG = "LessonActivity";
    private String uploadedVideoUrl = null;
    private String thumbnailBase64 = null;
    private String courseId;

    private EditText lessonTitleInput, lessonDescriptionInput;
    private VideoView videoView;
    private ImageView thumbnailImageView;
    private Cloudinary cloudinary;

    private RecyclerView lessonsRecyclerView;
    private LessonAdapter lessonAdapter;
    private List<Lesson> lessonList;



    private ActivityResultLauncher<Intent> videoPickerLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lesson_activity); // Update layout file name if necessary

        // Java
        Button addLessonButton = findViewById(R.id.addLessonButton);
        ScrollView formSection = findViewById(R.id.formSection);

        addLessonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formSection.getVisibility() == View.GONE) {
                    formSection.setVisibility(View.VISIBLE);
                } else {
                    formSection.setVisibility(View.GONE);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", getString(R.string.cloud_name),
                "api_key", getString(R.string.api_key),
                "api_secret", getString(R.string.api_secret)
        ));

        lessonTitleInput = findViewById(R.id.lessonTitleInput);
        lessonDescriptionInput = findViewById(R.id.lessonDescriptionInput);
        videoView = findViewById(R.id.videoView);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);

        // Get courseId from intent
        courseId = getIntent().getStringExtra("COURSE_ID");

        Button uploadVideoButton = findViewById(R.id.uploadVideoButton);
        Button saveLessonButton = findViewById(R.id.saveLessonButton);
        Button selectThumbnailButton = findViewById(R.id.selectThumbnailButton);

        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lessonList = new ArrayList<>();
        lessonAdapter = new LessonAdapter(lessonList);
        lessonsRecyclerView.setAdapter(lessonAdapter);

        // Fetch lessons from Firestore
        fetchLessons();

        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedVideoUri = result.getData().getData();
                        String videoPath = getRealPathFromURI(selectedVideoUri);

                        if (videoPath != null) {
                            uploadVideo(videoPath);
                        } else {
                            Toast.makeText(this, "Failed to get video path", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        processThumbnail(selectedImageUri);
                    }
                }
        );

        uploadVideoButton.setOnClickListener(v -> selectVideo());
        selectThumbnailButton.setOnClickListener(v -> selectThumbnail());

        saveLessonButton.setOnClickListener(v -> {
            String title = lessonTitleInput.getText().toString();
            String description = lessonDescriptionInput.getText().toString();

            if (!title.isEmpty() && !description.isEmpty() && uploadedVideoUrl != null && thumbnailBase64 != null) {
                saveLessonToFirestore(title, description, uploadedVideoUrl, thumbnailBase64);
            } else {
                Toast.makeText(LessonActivity.this, "Please complete all fields, upload video, and select thumbnail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLessons() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Lessons")
                .whereEqualTo("courseId", courseId)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            List<Lesson> lessons = documentSnapshots.toObjects(Lesson.class);
                            Log.d(TAG, "Fetched lessons: " + lessons.size());
                            lessonList.clear();
                            lessonList.addAll(lessons);
                            lessonAdapter.notifyDataSetChanged(); // Update the RecyclerView
                        }
                    } else {
                        Log.e(TAG, "Error getting lessons", task.getException());
                    }
                });
    }


    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        videoPickerLauncher.launch(intent);
    }

    private void selectThumbnail() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void processThumbnail(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            thumbnailImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            thumbnailBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process thumbnail", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo(String filePath) {

        new Thread(() -> {
            try {
                Map uploadResult = cloudinary.uploader().upload(new File(filePath),
                        ObjectUtils.asMap("resource_type", "video"));
                uploadedVideoUrl = (String) uploadResult.get("secure_url");

                runOnUiThread(() -> {
                    if (uploadedVideoUrl != null) {
                        Toast.makeText(this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();
                        videoView.setVideoURI(Uri.parse(uploadedVideoUrl));
                        videoView.pause();
                    } else {
                        Toast.makeText(this, "Video upload failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveLessonToFirestore(String title, String description, String videoUrl, String thumbnailBase64) {
        ScrollView formSection = findViewById(R.id.formSection);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uniqueLessonId = UUID.randomUUID().toString();

        if (videoUrl != null) {
            Lesson lesson = new Lesson(uniqueLessonId, title, description, videoUrl, thumbnailBase64, courseId);

            db.collection("Lessons")
                    .document(uniqueLessonId)
                    .set(lesson)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(LessonActivity.this, "Lesson added successfully", Toast.LENGTH_SHORT).show();
                        lessonTitleInput.setText(""); // Clear the lesson title input
                        lessonDescriptionInput.setText("");
                        formSection.setVisibility(View.GONE);
                        fetchLessons();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LessonActivity.this, "Error adding lesson", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Helper method to get the real file path from a URI
    private String getRealPathFromURI(Uri uri) {
        try {
            File tempFile = new File(getFilesDir(), "temp_video");
            try (InputStream input = getContentResolver().openInputStream(uri);
                 OutputStream output = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to process video. Please try again later.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to process video URI", e);
            return null;
        }
    }

    // Method to send notification (if needed)
    private void sendNotification(String title, String message) {
        // Implement Firebase Cloud Messaging (FCM) logic here to send notifications
    }

}
