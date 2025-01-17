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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageFragment extends Fragment {

    private static final String TAG = "FirestoreDebug";
    private FirebaseFirestore db;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Prepare list and adapter
        cardList = new ArrayList<>();
        adapter = new CardAdapter(cardList, getContext());
        recyclerView.setAdapter(adapter);

        // Fetch user-specific courses
        fetchUserCourses();

        return view;
    }

    private void fetchUserCourses() {
        // Replace "currentUserId" with your logic for retrieving the current user's ID
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection("userCourses")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User-specific courses fetched successfully");
                        for (QueryDocumentSnapshot userCourseDoc : task.getResult()) {
                            String courseId = userCourseDoc.getString("courseId");

                            if (courseId != null) {
                                // Fetch course details for each courseId
                                fetchCourseDetails(courseId);
                            } else {
                                Log.e(TAG, "Course ID is null in userCourses document: " + userCourseDoc.getId());
                            }
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch user-specific courses", task.getException());
                    }
                });
    }

    private void fetchCourseDetails(String courseId) {
        db.collection("Courses")
                .document(courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot courseDoc = task.getResult();  // Change here
                        String id = courseDoc.getId();
                        String title = courseDoc.getString("title");
                        String description = courseDoc.getString("description");
                        String imageBase64 = courseDoc.getString("imageBase64");
                        String teacherId = courseDoc.getString("teacherId");
                        Double price = courseDoc.getDouble("price");

                        // Fetch lessons count for this course
                        fetchLessonsCount(id, courseId, title, description, imageBase64, teacherId, price);
                    } else {
                        Log.e(TAG, "Failed to fetch course details for courseId: " + courseId, task.getException());
                    }
                });
    }


    private void fetchLessonsCount(String id, String courseId, String title, String description, String imageBase64, String teacherId, Double price) {
        Log.d(TAG, "Querying lessons for courseId: " + courseId);

        db.collection("lessons")
                .whereEqualTo("courseId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int resultSize = task.getResult().size();

                        // After fetching lesson count, create the CardItem
                        CardItem cardItem = new CardItem(id, courseId, title, description, imageBase64, teacherId, resultSize, price);
                        cardList.add(cardItem);

                        // Notify adapter to update UI
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreDebug", "Error fetching lessons for courseId: " + courseId, task.getException());
                    }
                });
    }
}
