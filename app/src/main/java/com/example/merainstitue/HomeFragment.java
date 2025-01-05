package com.example.merainstitue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "FirestoreDebug";
    private FirebaseFirestore db;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Prepare list and adapter
        cardList = new ArrayList<>();
        adapter = new CardAdapter(cardList, getContext());
        recyclerView.setAdapter(adapter);

        // Fetch courses and their lesson counts
        fetchCourses();

        return view;
    }

    private void fetchCourses() {
        db.collection("Courses")
                .get()
                .addOnCompleteListener(courseTask -> {
                    if (courseTask.isSuccessful()) {
                        Log.d(TAG, "Courses fetched successfully");
                        for (QueryDocumentSnapshot courseDoc : courseTask.getResult()) {
                            String id = courseDoc.getId(); // Document ID of the course (Firestore ID)
                            String courseId = courseDoc.getString("courseId"); // The courseId field within the document
                            String title = courseDoc.getString("title");
                            String description = courseDoc.getString("description");
                            String imageBase64 = courseDoc.getString("imageBase64");
                            String teacherId = courseDoc.getString("teacherId");

                            // Fetch price as Double
                            Double price = courseDoc.getDouble("price");

                            // Log the entire document for debugging
                            Log.d(TAG, "Course document: " + courseDoc.getData());
                            Log.d(TAG, "Fetched Course ID: " + courseId);

                            // Check for null courseId
                            if (courseId == null) {
                                Log.e(TAG, "Course ID is null for document: " + courseDoc.getId());
                                continue; // Skip this course if courseId is null
                            }

                            // Fetch lessons count for each course
                            fetchLessonsCount(id, courseId, title, description, imageBase64, teacherId, price);  // Pass price here
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch courses", courseTask.getException());
                    }
                });
    }

    private void fetchLessonsCount(String id, String courseId, String title, String description, String imageBase64, String teacherId, Double price) {
        Log.d(TAG, "Querying lessons for courseId: " + id);

        db.collection("lessons")
                .whereEqualTo("courseId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int resultSize = task.getResult().size();

                        // After fetching lesson count, create the CardItem with price
                        CardItem cardItem = new CardItem(id, courseId, title, description, imageBase64, teacherId, resultSize, price); // Pass price
                        cardList.add(cardItem);

                        // Notify adapter after all data is fetched
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreDebug", "Error fetching lessons for courseId: " + courseId, task.getException());
                    }
                });
    }
}
