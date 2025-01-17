package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout lessonsContainer;
    private Button btnBuyCourse;
    private String courseId;
    private String userId;

    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();
        lessonsContainer = findViewById(R.id.lessonsContainer);
        btnBuyCourse = findViewById(R.id.btnBuyCourse);

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Retrieve intent extras
        String title = getIntent().getStringExtra("title");
        String subtitle = getIntent().getStringExtra("subtitle");
        courseId = getIntent().getStringExtra("courseId");
        teacherId = getIntent().getStringExtra("teacherid");
        double price = getIntent().getDoubleExtra("price", 0.0);

        // Set course data to views
        ((TextView) findViewById(R.id.detailTitle)).setText(title);
        ((TextView) findViewById(R.id.detailSubtitle)).setText(subtitle);

        // Check if user has purchased the course
        checkCoursePurchase();

        // Set up the Buy Course button
        btnBuyCourse.setOnClickListener(v -> {
            navigateToPayment(courseId, title, subtitle, price , teacherId);
        });

        // Fetch lessons
        fetchLessons(courseId);
    }

    private void checkCoursePurchase() {
        db.collection("UserCourses")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User has purchased the course
                        btnBuyCourse.setVisibility(View.GONE);
                    } else {
                        // User hasn't purchased the course
                        btnBuyCourse.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void fetchLessons(String courseId) {
        db.collection("Lessons")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lessonsContainer.removeAllViews();
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String lessonTitle = document.getString("title");
                                String lessonDescription = document.getString("description");
                                String thumbnailBase64 = document.getString("thumbnailBase64");
                                String videoUrl = document.getString("videoUrl");
                                addLessonToContainer(lessonTitle, lessonDescription, thumbnailBase64, videoUrl);
                            }
                        } else {
                            Toast.makeText(this, "No lessons found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("DetailActivity", "Error fetching lessons: ", task.getException());
                        Toast.makeText(this, "Failed to fetch lessons.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addLessonToContainer(String lessonTitle, String lessonDescription, String thumbnailBase64, String videoUrl) {
        View lessonView = LayoutInflater.from(this).inflate(R.layout.lesson_item, null);

        TextView lessonTitleView = lessonView.findViewById(R.id.lessonTitle);
        TextView lessonDescriptionView = lessonView.findViewById(R.id.lessonDescription);
        ImageView lessonImage = lessonView.findViewById(R.id.lessonThumbnail);

        lessonTitleView.setText(lessonTitle);
        lessonDescriptionView.setText(lessonDescription);

        if (thumbnailBase64 != null && !thumbnailBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(thumbnailBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                lessonImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("DetailActivity", "Error decoding thumbnail: ", e);
            }
        } else {
            lessonImage.setImageResource(R.drawable.ic_image);
        }

        // Set the click listener for each lesson item
        lessonView.setOnClickListener(v -> {
            checkLessonAccess(videoUrl, lessonTitle);
        });

        lessonsContainer.addView(lessonView);
    }

    private void checkLessonAccess(String videoUrl, String lessonTitle) {
        db.collection("userCourses")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User has access to the course, show video
                        showVideo(videoUrl, lessonTitle);
                    } else {
                        // User hasn't purchased the course
                        Toast.makeText(this, "Please purchase the course to access this lesson", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showVideo(String videoUrl, String lessonTitle) {
        MediaActivity mediaFragment = new MediaActivity();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_URL", videoUrl);
        bundle.putString("LESSON_TITLE", lessonTitle);
        mediaFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mediaFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToPayment(String courseId, String title, String subtitle, double price, String teacherId) {
        Intent intent = new Intent(DetailActivity.this, PaymentActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("courseTitle", title);
        intent.putExtra("courseSubtitle", subtitle);
        intent.putExtra("coursePrice", price);
        intent.putExtra("teacherid", teacherId);
        startActivity(intent);
    }
}