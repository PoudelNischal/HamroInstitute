package com.example.merainstitue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> cardItems;
    private Context context;

    public CardAdapter(List<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        holder.title.setText(cardItem.getTitle());
        holder.description.setText(cardItem.getDescription());
        holder.lessonCount.setText("Lessons: " + cardItem.getLessonCount());
        holder.price.setText("price: " + cardItem.getPrice());


        // Decode Base64 image and set it
        String base64Image = cardItem.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        } else {
            // Set a placeholder image if no image is available
            holder.image.setImageResource(R.drawable.ic_placeholder); // Replace with your placeholder image resource
        }

        // Set the onClickListener to pass data to DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            // Passing correct courseId to DetailActivity
            intent.putExtra("courseId", cardItem.getId()); // courseId of the course
            intent.putExtra("title", cardItem.getTitle());
            intent.putExtra("subtitle", cardItem.getDescription());
            intent.putExtra("progress", cardItem.getLessonCount());
            intent.putExtra("completionStatus", "In Progress"); // Example logic for completion status
            intent.putExtra("duration", "N/A"); // Add actual duration logic if needed
            intent.putExtra("lessons", String.valueOf(cardItem.getLessonCount()));
            intent.putExtra("price" , cardItem.getPrice());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, lessonCount;
        ImageView image;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            lessonCount = itemView.findViewById(R.id.lessonCount);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image); // Initialize the ImageView here
        }
    }
}
