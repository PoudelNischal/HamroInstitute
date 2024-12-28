package com.example.merainstitue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

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

        // Set the title and description
        holder.courseTitle.setText(course.getTitle());
        holder.courseDescription.setText(course.getDescription());

        // Decode the base64 image string to Bitmap and set it to ImageView
        String base64Image = course.getImageBase64();
        Log.d("CourseAdapter", "Base64 String: " + base64Image);

        if (base64Image != null && !base64Image.isEmpty()) {
            // Remove the "data:image" prefix if it exists
            if (base64Image.contains("data:image")) {
                base64Image = base64Image.split(",")[1];  // Get the actual Base64 string
            }
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    holder.courseThumbnail.setImageBitmap(decodedByte);  // Set the decoded Bitmap
                } else {
                    holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);  // Placeholder on error
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CourseAdapter", "Error decoding Base64 image", e);
                holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);  // Fallback to placeholder
            }
        } else {
            holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);  // Use placeholder if no image
        }

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onCourseClick(course.getCourseId());
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
        public ImageView courseThumbnail;

        public CourseViewHolder(View view) {
            super(view);
            courseTitle = view.findViewById(R.id.courseTitle);
            courseDescription = view.findViewById(R.id.courseDescription);
            courseThumbnail = view.findViewById(R.id.courseThumbnail);
        }
    }

    // Interface to handle course click events
    public interface OnCourseClickListener {
        void onCourseClick(String courseId);
    }
}
