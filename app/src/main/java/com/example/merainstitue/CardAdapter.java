package com.example.merainstitue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> cardList;
    private Context context;

    // Constructor
    public CardAdapter(List<CardItem> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each card item
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current card item
        CardItem card = cardList.get(position);

        // Bind data to the views
        holder.title.setText(card.getTitle());
        holder.subtitle.setText(card.getSubtitle());
        holder.progressBar.setProgress(card.getProgress());
        holder.completionStatus.setText(card.getCompletionStatus());
        holder.duration.setText(card.getDuration());
        holder.lessons.setText(card.getLessons());

        // Set click listener for navigation to DetailActivity
        holder.cardView.setOnClickListener(v -> {
            // Log the card click event
            Log.d("CardAdapter", "Card clicked: " + card.getTitle());

            // Create an intent to navigate to DetailActivity
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", card.getId());
            intent.putExtra("title", card.getTitle());
            intent.putExtra("subtitle", card.getSubtitle());
            intent.putExtra("progress", card.getProgress());
            intent.putExtra("completionStatus", card.getCompletionStatus());
            intent.putExtra("duration", card.getDuration());
            intent.putExtra("lessons", card.getLessons());

            // Start the DetailActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size(); // Return the total number of cards
    }

    // ViewHolder class to hold item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, completionStatus, duration, lessons;
        ProgressBar progressBar;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            progressBar = itemView.findViewById(R.id.progressBar);
            completionStatus = itemView.findViewById(R.id.completionStatus);
            duration = itemView.findViewById(R.id.duration);
            lessons = itemView.findViewById(R.id.lessons);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
