package com.example.merainstitue;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MediaActivity extends Fragment {

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageButton backButton;
    private TextView videoTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Make the activity full screen (hide status bar and navigation bar)
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Set the requested orientation to landscape (if needed)
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        View view = inflater.inflate(R.layout.fragment_media_activity, container, false);
        playerView = view.findViewById(R.id.player_view);
        backButton = view.findViewById(R.id.back_button);
        videoTitle = view.findViewById(R.id.video_title);

        // Set video title dynamically (you can replace this with the actual video title)
        String title = getActivity().getIntent().getStringExtra("video_title");
        videoTitle.setText(title);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        // Load video
        MediaItem mediaItem = MediaItem.fromUri("https://samplelib.com/lib/preview/mp4/sample-5s.mp4");
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        // Set up back button to handle click
        backButton.setOnClickListener(v -> {
            // Exit full-screen mode and show the system UI (status bar and navigation bar)
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            // Show bottom navigation again
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showBottomNavigation();
            }

            // Navigate back to the previous fragment or activity
            getActivity().onBackPressed();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
        }
    }

    // Override onStart to hide bottom navigation when media fragment starts
    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
    }
}
