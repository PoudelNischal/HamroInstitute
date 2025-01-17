package com.example.merainstitue;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    private boolean isMediaPlaying = false; // Track media state

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Make the activity full screen (hide status bar and navigation bar)
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            // Set the background color to black when media is playing
            getActivity().findViewById(R.id.fragment_container).setBackgroundColor(getResources().getColor(android.R.color.black));
        }

        // Set the requested orientation to landscape (if needed)
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        View view = inflater.inflate(R.layout.fragment_media_activity, container, false);
        playerView = view.findViewById(R.id.player_view);
        backButton = view.findViewById(R.id.back_button);
        videoTitle = view.findViewById(R.id.video_title);

        // Set video title dynamically (replace with the actual video title)
        if (getArguments() != null) {
            String title = getArguments().getString("LESSON_TITLE");
            videoTitle.setText(title);
        }

        // Initialize ExoPlayer using Media3 package
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        // Retrieve video URL passed from previous activity (or fragment)
        if (getArguments() != null) {
            String videoUrl = getArguments().getString("VIDEO_URL");
            if (videoUrl != null) {
                // Load video using Media3
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
                isMediaPlaying = true;  // Set media as playing
            } else {
                // Handle case where video URL is missing
                Toast.makeText(requireContext(), "Error: No video URL found.", Toast.LENGTH_SHORT).show();
            }
        }

        // Set up back button to handle click
        backButton.setOnClickListener(v -> {
            // Exit full-screen mode and show the system UI (status bar and navigation bar)
            if (getActivity() != null) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }

            // Return to normal orientation (portrait mode, if needed)
            if (getActivity() != null) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            // Navigate back to the previous fragment or activity
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
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

        // Reset background color to default when leaving the media screen
        if (getActivity() != null && !isMediaPlaying) {
            getActivity().findViewById(R.id.fragment_container).setBackgroundColor(getResources().getColor(android.R.color.white));
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
        if (player != null && isMediaPlaying) {
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
