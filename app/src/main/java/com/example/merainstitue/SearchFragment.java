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

import com.airbnb.lottie.LottieAnimationView;
import com.example.merainstitue.CardItem;
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

    private View initialStateLayout;
    private View noResultsLayout;

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

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        cardList = new ArrayList<>();
        adapter = new CardAdapter(cardList, getContext());
        recyclerView.setAdapter(adapter);

        // Initialize Layouts for animations
        initialStateLayout = view.findViewById(R.id.initialStateLayout);
        noResultsLayout = view.findViewById(R.id.noResultsLayout);

        // Show the initial state animation
        initialStateLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.GONE);

        // Set up SearchView
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    initialStateLayout.setVisibility(View.GONE); // Hide initial state
                    searchCourses(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    cardList.clear();
                    adapter.notifyDataSetChanged();

                    // Show the initial state animation
                    initialStateLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    noResultsLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });

        return view;
    }

    private void searchCourses(String query) {
        final String normalizedQuery = query.toLowerCase();

        db.collection("Courses").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cardList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    List<String> tags = (List<String>) document.get("tags");

                    boolean titleMatches = title != null && title.toLowerCase().contains(normalizedQuery);
                    boolean tagsMatch = tags != null && tags.stream().anyMatch(tag -> tag.toLowerCase().contains(normalizedQuery));

                    if (titleMatches || tagsMatch) {
                        cardList.add(new CardItem(
                                document.getId(),
                                document.getString("courseId"),
                                title,
                                document.getString("description"),
                                document.getString("imageBase64"),
                                document.getString("teacherId"),
                                0 // Assuming lesson count is 0 for now
                        ));
                    }
                }

                if (cardList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noResultsLayout.setVisibility(View.VISIBLE); // Show "No Results" animation
                } else {
                    noResultsLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE); // Show results
                }

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error fetching courses: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
