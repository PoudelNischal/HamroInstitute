package com.example.merainstitue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        lessonsContainer = findViewById(R.id.lessonsContainer);

        // Retrieve intent extras
        int id = getIntent().getIntExtra("id", -1);
        String title = getIntent().getStringExtra("title");
        String subtitle = getIntent().getStringExtra("subtitle");
        int progress = getIntent().getIntExtra("progress", 0);
        String lessons = getIntent().getStringExtra("lessons");
        String courseId = getIntent().getStringExtra("courseId");

        Log.d("courseID data" , "a"+ courseId);

        // Bind data to views
        ((TextView) findViewById(R.id.detailTitle)).setText(title);
        ((TextView) findViewById(R.id.detailSubtitle)).setText(subtitle);
        ((ProgressBar) findViewById(R.id.detailProgress)).setProgress(progress);
        ((TextView) findViewById(R.id.detailLessons)).setText(lessons);

        // Fetch and display lessons for this course
        fetchLessons(courseId);
    }

    private void fetchLessons(String courseId) {
        db.collection("Lessons")
                .whereEqualTo("courseId", courseId) // Query by courseId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous lesson data
                        lessonsContainer.removeAllViews();

                        // Log the response to see if any documents are returned
                        Log.d("DetailFire", "Query successful. Documents: " + task.getResult().size());

                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Loop through the lessons and add them to the layout
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String lessonTitle = document.getString("title");
                                String lessonDescription = document.getString("description");

                                // Log fetched lesson data
                                Log.d("DetailFire", "Fetched lesson - Title: " + lessonTitle + ", Description: " + lessonDescription);

                                // Dynamically create and add lesson views
                                addLessonToContainer(lessonTitle, lessonDescription);
                            }
                        } else {
                            Log.d("DetailFire", "No lessons found for courseId: " + courseId);  // Log if no lessons are found
                        }
                    } else {
                        // Handle error: show a log or toast
                        Log.e("DetailFire", "Error getting documents: ", task.getException());
                    }
                });
    }


    private void addLessonToContainer(String lessonTitle, String lessonDescription) {
        // Inflate a new lesson view
        View lessonView = LayoutInflater.from(this).inflate(R.layout.lesson_item, null);

        // Bind lesson data to the views
        TextView lessonTitleView = lessonView.findViewById(R.id.lessonTitle);
        TextView lessonDescriptionView = lessonView.findViewById(R.id.lessonDescription);

        lessonTitleView.setText(lessonTitle);
        lessonDescriptionView.setText(lessonDescription);

        // Add the new lesson view to the container
        lessonsContainer.addView(lessonView);
    }
}
