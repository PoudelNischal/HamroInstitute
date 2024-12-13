package com.example.merainstitue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Set up RecyclerView with GridLayoutManager (2 columns)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Prepare data for the cards
        List<CardItem> cardList = new ArrayList<>();
        cardList.add(new CardItem("Power Bi", "Hi Nischal, Thanks to .", 100, "Completed 12 of 12", "6 hrs 52min", "24 Course"));
        cardList.add(new CardItem("Excel Basics", "Learn Excel Basics Today.", 80, "Completed 8 of 10", "4 hrs", "10 Course"));
        cardList.add(new CardItem("Python", "Master Python in no time!", 90, "Completed 9 of 10", "5 hrs", "20 Course"));
        cardList.add(new CardItem("Java", "Become a Java Pro!", 75, "Completed 6 of 8", "7 hrs", "15 Course"));

        // Set up Adapter
        CardAdapter adapter = new CardAdapter(cardList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
