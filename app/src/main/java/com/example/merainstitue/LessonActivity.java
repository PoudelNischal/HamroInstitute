package com.example.merainstitue;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class LessonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lesson_activity);

        // Get the courseId passed from CourseFragment
        String courseId = getIntent().getStringExtra("COURSE_ID");

        Log.d("datas" , "aa");
        if (courseId != null) {
            // Use the courseId as needed
            Log.d("LessonActivity", "Course ID: " + courseId);
        } else {
            Log.e("LessonActivity", "Course ID is null");
        }
    }
}
