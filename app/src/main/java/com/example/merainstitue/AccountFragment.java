package com.example.merainstitue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class AccountFragment extends Fragment {

    private ImageView profileImageView;
    private TextView fullNameTextView, emailTextView, usernameTextView, changePasswordButton, userProfile;
    private Switch notificationsSwitch, themeSwitch;
    private Button logOutButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase and SharedPreferences
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        // Initialize UI components
        profileImageView = rootView.findViewById(R.id.profileImage);
        fullNameTextView = rootView.findViewById(R.id.name);
        emailTextView = rootView.findViewById(R.id.userEmail);
        usernameTextView = rootView.findViewById(R.id.username);
        notificationsSwitch = rootView.findViewById(R.id.notificationsSwitch);
        themeSwitch = rootView.findViewById(R.id.themeSwitch);
        logOutButton = rootView.findViewById(R.id.logOut);
        changePasswordButton = rootView.findViewById(R.id.changePasswordButton);
        userProfile = rootView.findViewById(R.id.userProfile);

        // Set up log out button
        logOutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            firebaseAuth.signOut();

            // Clear any shared preferences related to login
            SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isValidLogin", false);  // Mark login status as false
            editor.apply();

            // Redirect to the LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

            // Optionally, call finish() if you want to close the current activity and prevent going back to the logged-in screen
            getActivity().finish();
        });

        // Set up change password button
        changePasswordButton.setOnClickListener(v -> {
            ChangePasswordBottomSheetFragment changePasswordBottomSheet = new ChangePasswordBottomSheetFragment();
            changePasswordBottomSheet.show(getChildFragmentManager(), changePasswordBottomSheet.getTag());
        });

        userProfile.setOnClickListener(v -> {
            UpdateProfileBottomSheetFragment updateProfileBottomSheetFragment = new UpdateProfileBottomSheetFragment();
            updateProfileBottomSheetFragment.show(getChildFragmentManager(), updateProfileBottomSheetFragment.getTag());
        });

        // Load settings from SharedPreferences
        loadSettings();

        // Set up switch listeners
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_theme_enabled", isChecked);
            editor.apply();
            if (isChecked) {
                // Force dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Force light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Fetch and display user data
        fetchUserData();

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void fetchUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            listenerRegistration = userRef.addSnapshotListener((documentSnapshot, e) -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    String email = documentSnapshot.getString("email");
                    String base64Image = documentSnapshot.getString("profileImage");

                    if (fullName != null) {
                        fullNameTextView.setText(fullName);
                        usernameTextView.setText("@" + fullName.toLowerCase().replace(" ", "_"));
                    }

                    if (email != null) {
                        emailTextView.setText(email);
                        emailTextView.setOnClickListener(v -> {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                            startActivity(Intent.createChooser(emailIntent, "Send email"));
                        });
                    }

                    if (base64Image != null) {
                        try {
                            byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                            profileImageView.setImageBitmap(decodedBitmap);
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void loadSettings() {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationsSwitch.setChecked(notificationsEnabled);

        boolean darkThemeEnabled = sharedPreferences.getBoolean("dark_theme_enabled", false);
        themeSwitch.setChecked(darkThemeEnabled);

        // Apply system default night mode
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
