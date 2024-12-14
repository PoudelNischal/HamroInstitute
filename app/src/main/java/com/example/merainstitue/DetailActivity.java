package com.example.merainstitue;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Retrieve intent extras
        int id = getIntent().getIntExtra("id", -1);
        String title = getIntent().getStringExtra("title");
        Log.d("DetailActivity", "Title received: " + title);
        String subtitle = getIntent().getStringExtra("subtitle");
        int progress = getIntent().getIntExtra("progress", 0);
        String completionStatus = getIntent().getStringExtra("completionStatus");
        String duration = getIntent().getStringExtra("duration");
        String lessons = getIntent().getStringExtra("lessons");

        // Bind data to views (replace with actual IDs)
        ((TextView) findViewById(R.id.detailTitle)).setText(title);
        ((TextView) findViewById(R.id.detailSubtitle)).setText(subtitle);
        ((ProgressBar) findViewById(R.id.detailProgress)).setProgress(progress);
        ((TextView) findViewById(R.id.detailCompletionStatus)).setText(completionStatus);
        ((TextView) findViewById(R.id.detailDuration)).setText(duration);
        ((TextView) findViewById(R.id.detailLessons)).setText(lessons);
    }
}
