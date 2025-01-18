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
                                    // Fetch the total purchases for the course
                                    fetchTotalPurchases(course, courseId);
                                } else {
                                    Log.d("CourseFragment", "Course is null for document: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e("CourseFragment", "Error parsing course", e);
                            }
                        }
                    } else {
                        Log.e("CourseFragment", "Error getting courses: " + task.getException());
                    }
                });
    }

    private void fetchTotalPurchases(Course course, String courseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "userCourses" collection to count how many purchases are made for this course
        db.collection("userCourses")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalPurchases = task.getResult().size();  // Get the number of purchases for this course
                        course.setTotalPurchases(totalPurchases);  // Set the total purchases for this course
                        courseList.add(course);  // Add the course to the list
                        courseAdapter.notifyDataSetChanged();  // Notify adapter to update UI
                    } else {
                        Log.e("CourseFragment", "Error fetching total purchases: " + task.getException());
                    }
                });
    }

    private void showCreateCourseDialog() {
        CourseCreateDialogFragment dialogFragment = new CourseCreateDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "course_create_dialog");
    }

    @Override
    public void onCourseClick(String courseId) {
        // Open the LessonActivity and pass the courseId
        Intent intent = new Intent(getContext(), LessonActivity.class);
        intent.putExtra("COURSE_ID", courseId);
        startActivity(intent);
    }

    @Override
    public void onDeleteCourseClick(String courseId) {
        // Show confirmation dialog for deleting the course
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCourse(courseId))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteCourse(String courseId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the Courses collection to find the document with the matching courseId
        db.collection("Courses")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the first document where the courseId matches
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String documentId = document.getId();  // Get the document ID

                        // Now delete the document using the document ID
                        db.collection("Courses")
                                .document(documentId)
                                .delete()
                                .addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Log.d("DeleteCourse", "Course deleted successfully.");
                                        Toast.makeText(getContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Remove course from local list
                                        for (Course course : courseList) {
                                            if (course.getCourseId().equals(courseId)) {
                                                courseList.remove(course);
                                                break;
                                            }
                                        }
                                        courseAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
                                    } else {
                                        Log.e("DeleteCourse", "Error deleting course", deleteTask.getException());
                                    }
                                });
                    } else {
                        Log.e("DeleteCourse", "No matching course found.");
                    }
                });
    }


}
