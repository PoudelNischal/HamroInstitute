package com.example.merainstitue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment implements CourseAdapter.OnCourseClickListener {

    private List<Course> courseList;
    private CourseAdapter courseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        RecyclerView coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList, this); // Pass the click listener to the adapter
        coursesRecyclerView.setAdapter(courseAdapter);

        // Check user authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchCourses(user.getUid());
        } else {
            Log.e("CourseFragment", "User is not authenticated");
            Toast.makeText(getContext(), "Please log in to view your courses.", Toast.LENGTH_SHORT).show();
        }

        // Add Course Button functionality
        view.findViewById(R.id.addCourseButton).setOnClickListener(v -> showCreateCourseDialog());
    }

    private void fetchCourses(String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Access the "Courses" collection in Firestore
        db.collection("Courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Parse course from document
                                String courseId = document.getId();  // This is the unique ID of the document
                                Course course = document.toObject(Course.class);
                                if (course != null) {
                                    // Log the Base64 string (using the correct field name)
                                    Log.d("CourseFragment", "Base64 image: " + course.getImageBase64());
                                    course.setCourseId(courseId);
                                    courseList.add(course);
                                } else {
                                    Log.d("CourseFragment", "Course is null for document: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e("CourseFragment", "Error parsing course", e);
                            }
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("CourseFragment", "Error getting courses: " + task.getException());
                    }
                });
    }

    private void showCreateCourseDialog() {
        CourseCreateDialogFragment dialogFragment = new CourseCreateDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "course_create_dialog");
    }

    @Override
    public void onCourseClick(String courseId) {
        Log.d("devs" , "id" + courseId);
        // Open the LessonActivity and pass the courseId
        Intent intent = new Intent(getContext(), LessonActivity.class);
        intent.putExtra("COURSE_ID", courseId);  // Pass the courseId to the next activity
        startActivity(intent);
    }
}
