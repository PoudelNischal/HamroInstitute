package com.example.merainstitue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener;

    // Constructor to initialize data and listener
    public CourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.onCourseClickListener = listener;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Set the course title
        holder.courseTitle.setText(course.getTitle());

        // Set the course description
        holder.courseDescription.setText(course.getDescription());

        // Set the course price
        holder.coursePrice.setText("Price: $" + course.getPrice());

        // Display total purchases
        holder.totalPurchases.setText("Total Purchases: " + course.getTotalPurchases());

        // Decode the base64 image string to Bitmap and set it to ImageView
        String base64Image = course.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            if (base64Image.contains("data:image")) {
                base64Image = base64Image.split(",")[1];
            }
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.courseThumbnail.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);
                Log.e("CourseAdapter", "Error decoding base64 image", e);
            }
        } else {
            holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onCourseClick(course.getCourseId());
            }
        });

        // Delete Button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onDeleteCourseClick(course.getCourseId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ViewHolder for the course item
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTitle;
        public TextView courseDescription;
        public TextView coursePrice;
        public TextView totalPurchases;
        public ImageView courseThumbnail;
        public Button deleteButton;

        public CourseViewHolder(View view) {
            super(view);
            courseTitle = view.findViewById(R.id.courseTitle);
            courseDescription = view.findViewById(R.id.courseDescription);
            coursePrice = view.findViewById(R.id.coursePrice);
            totalPurchases = view.findViewById(R.id.totalPurchases);
            courseThumbnail = view.findViewById(R.id.courseThumbnail);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    // Interface for handling course click events
    public interface OnCourseClickListener {
        void onCourseClick(String courseId);
        void onDeleteCourseClick(String courseId);
    }
}
