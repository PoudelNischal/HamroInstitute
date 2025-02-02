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
    private static final String TAG = "CourseFragment";

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
        courseAdapter = new CourseAdapter(courseList, this);
        coursesRecyclerView.setAdapter(courseAdapter);

        // Check user authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchCourses(user.getUid());
        } else {
            Log.e(TAG, "User is not authenticated");
            Toast.makeText(getContext(), "Please log in to view your courses.", Toast.LENGTH_SHORT).show();
        }

        // Add Course Button functionality
        view.findViewById(R.id.addCourseButton).setOnClickListener(v -> showCreateCourseDialog());
    }

    private void fetchCourses(String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Courses")
                .whereEqualTo("teacherId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String firestoreId = document.getId();
                                Course course = document.toObject(Course.class);
                                if (course != null) {
                                    course.setFirestoreId(firestoreId);
                                    fetchTotalPurchases(course, course.getCourseId());
                                } else {
                                    Log.d(TAG, "Course is null for document: " + firestoreId);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing course", e);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting courses: ", task.getException());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error fetching courses", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchTotalPurchases(Course course, String courseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("userCourses")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalPurchases = task.getResult().size();
                        course.setTotalPurchases(totalPurchases);
                        courseList.add(course);
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error fetching total purchases: ", task.getException());
                    }
                });
    }

    private void showCreateCourseDialog() {
        CourseCreateDialogFragment dialogFragment = new CourseCreateDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "course_create_dialog");
    }

    @Override
    public void onCourseClick(String courseId) {
        for (Course course : courseList) {
            if (course.getCourseId().equals(courseId)) {
                Intent intent = new Intent(getContext(), LessonActivity.class);
                intent.putExtra("COURSE_ID", course.getFirestoreId());
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onDeleteCourseClick(String courseId) {
        if (getContext() == null) return;

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    for (Course course : courseList) {
                        if (course.getCourseId().equals(courseId)) {
                            deleteCourse(course.getFirestoreId());
                            break;
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteCourse(String firestoreId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Courses")
                .document(firestoreId)
                .delete()
                .addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        Log.d(TAG, "Course deleted successfully");
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        courseList.removeIf(course -> course.getFirestoreId().equals(firestoreId));
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error deleting course", deleteTask.getException());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error deleting course", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}