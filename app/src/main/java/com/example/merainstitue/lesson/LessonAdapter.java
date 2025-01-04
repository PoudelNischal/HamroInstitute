package com.example.merainstitue.lesson;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.merainstitue.R;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final List<Lesson> lessonList;

    public LessonAdapter(List<Lesson> lessonList) {
        this.lessonList = lessonList;
    }

    @Override
    public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.lessonTitle.setText(lesson.getTitle());
        holder.lessonDescription.setText(lesson.getDescription());

        // Decode base64 thumbnail if necessary
        byte[] decodedString = Base64.decode(lesson.getThumbnailBase64(), Base64.DEFAULT);
        holder.lessonThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }


    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {

        TextView lessonTitle, lessonDescription;
        ImageView lessonThumbnail;

        public LessonViewHolder(View itemView) {
            super(itemView);
            lessonTitle = itemView.findViewById(R.id.lessonTitle);
            lessonDescription = itemView.findViewById(R.id.lessonDescription);
            lessonThumbnail = itemView.findViewById(R.id.lessonThumbnail);
        }
    }
}
