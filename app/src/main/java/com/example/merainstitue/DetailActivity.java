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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

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

        ((TextView) findViewById(R.id.detailTitle)).setText(title);
        ((TextView) findViewById(R.id.detailSubtitle)).setText(subtitle);
        ((ProgressBar) findViewById(R.id.detailProgress)).setProgress(progress);
        ((TextView) findViewById(R.id.detailLessons)).setText(lessons);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

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
            lessonImage.setImageResource(R.drawable.ic_image); // Placeholder image
        }

        // Set the click listener for each lesson item
        lessonView.setOnClickListener(v -> {
            // Create the MediaActivity fragment and pass data to it
            MediaActivity mediaFragment = new MediaActivity();
            Bundle bundle = new Bundle();
            bundle.putString("VIDEO_URL", videoUrl);
            bundle.putString("LESSON_TITLE", lessonTitle);
            mediaFragment.setArguments(bundle);

            // Replace the current container with the MediaActivity fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mediaFragment);
            transaction.addToBackStack(null); // Add fragment to back stack so the user can navigate back
            transaction.commit();
        });

        // Add the lesson view to the container
        lessonsContainer.addView(lessonView);
    }
}
