package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout lessonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();
        lessonsContainer = findViewById(R.id.lessonsContainer);

        // Retrieve intent extras
        String title = getIntent().getStringExtra("title");
        String subtitle = getIntent().getStringExtra("subtitle");
        int progress = getIntent().getIntExtra("progress", 0);
        String lessons = getIntent().getStringExtra("lessons");
        String courseId = getIntent().getStringExtra("courseId");
        double price = getIntent().getDoubleExtra("price", 0.0); // Add price

        // Set course data to views
        ((TextView) findViewById(R.id.detailTitle)).setText(title);
        ((TextView) findViewById(R.id.detailSubtitle)).setText(subtitle);
        ((ProgressBar) findViewById(R.id.detailProgress)).setProgress(progress);
        ((TextView) findViewById(R.id.detailLessons)).setText(lessons);

        // Set up the Buy Course button
        findViewById(R.id.btnBuyCourse).setOnClickListener(v -> {
            navigateToPayment(courseId, title, subtitle, price);
        });

        // Fetch lessons
        fetchLessons(courseId);
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
        }

        lessonsContainer.addView(lessonView);
    }

    private void navigateToPayment(String courseId, String title, String subtitle, double price) {
        Intent intent = new Intent(DetailActivity.this, PaymentActivity.class);

        // Pass the course data to the PaymentActivity
        intent.putExtra("courseId", courseId);
        intent.putExtra("courseTitle", title);
        intent.putExtra("courseSubtitle", subtitle);
        intent.putExtra("coursePrice", price);  // Pass price

        startActivity(intent);
    }
}
