package com.example.merainstitue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merainstitue.CardItem;
import com.example.merainstitue.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<CardItem> cardList;

    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem card = cardList.get(position);
        holder.title.setText(card.getTitle());
        holder.subtitle.setText(card.getSubtitle());
        holder.progressBar.setProgress(card.getProgress());
        holder.completionStatus.setText(card.getCompletionStatus());
        holder.duration.setText(card.getDuration());
        holder.lessons.setText(card.getLessons());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, completionStatus, duration, lessons;
        ProgressBar progressBar;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            progressBar = itemView.findViewById(R.id.progressBar);
            completionStatus = itemView.findViewById(R.id.completionStatus);
            duration = itemView.findViewById(R.id.duration);
            lessons = itemView.findViewById(R.id.lessons);
        }
    }
}
