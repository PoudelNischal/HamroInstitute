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

        // Check if the views are not null
        if (holder.courseTitle != null) {
            holder.courseTitle.setText(course.getTitle());
        } else {
            Log.e("CourseAdapter", "courseTitle is null");
        }

        if (holder.courseDescription != null) {
            holder.courseDescription.setText(course.getDescription());
        } else {
            Log.e("CourseAdapter", "courseDescription is null");
        }

        if (holder.coursePrice != null) {
            holder.coursePrice.setText("Price: $" + course.getPrice());
        } else {
            Log.e("CourseAdapter", "coursePrice is null");
        }

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
            }
        } else {
            holder.courseThumbnail.setImageResource(R.drawable.ic_placeholder);
        }

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
        public TextView coursePrice;
        public ImageView courseThumbnail;

        public CourseViewHolder(View view) {
            super(view);
            courseTitle = view.findViewById(R.id.courseTitle);
            courseDescription = view.findViewById(R.id.courseDescription);
            coursePrice = view.findViewById(R.id.coursePrice);
            courseThumbnail = view.findViewById(R.id.courseThumbnail);
        }
    }

    // Interface to handle course click events
    public interface OnCourseClickListener {
        void onCourseClick(String courseId);
    }
}
