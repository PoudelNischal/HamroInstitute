package com.example.merainstitue;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "FirestoreDebug";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        cardList = new ArrayList<>();
        adapter = new CardAdapter(cardList, getContext());
        recyclerView.setAdapter(adapter);

        // Set up SearchView
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search action when submit button is pressed
                if (!TextUtils.isEmpty(query)) {
                    searchCourses(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Search action when text is changed
                if (!TextUtils.isEmpty(newText)) {
                    searchCourses(newText);
                } else {
                    cardList.clear();  // Clear the list if search is empty
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return view;
    }

    private void searchCourses(String query) {
        // Convert the query to lowercase to ensure case-insensitive search
        final String normalizedQuery = query.toLowerCase();

        // Query the Firestore database for courses matching the normalized query in either the title or tags
        db.collection("Courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cardList.clear();  // Clear the current list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String courseId = document.getString("courseId");
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String imageBase64 = document.getString("imageBase64");
                            String teacherId = document.getString("teacherId");
                            List<String> tags = (List<String>) document.get("tags"); // Assuming 'tags' is a list of strings

                            // Log the retrieved data for debugging
                            Log.d(TAG, "Course: " + title + ", courseId: " + courseId);

                            // Check if title contains the search query (case-insensitive)
                            boolean titleMatches = title != null && title.toLowerCase().contains(normalizedQuery);

                            // Check if any tag contains the search query (case-insensitive)
                            boolean tagsMatch = false;
                            if (tags != null) {
                                for (String tag : tags) {
                                    if (tag.toLowerCase().contains(normalizedQuery)) {
                                        tagsMatch = true;
                                        break;
                                    }
                                }
                            }

                            // If either title or tags match the query, add to the list
                            if (titleMatches || tagsMatch) {
                                CardItem cardItem = new CardItem(id ,courseId, title, description, imageBase64, teacherId, 0); // Assuming 0 for lesson count
                                cardList.add(cardItem);
                            }
                        }
                        // Notify adapter to update RecyclerView
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error fetching courses: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
