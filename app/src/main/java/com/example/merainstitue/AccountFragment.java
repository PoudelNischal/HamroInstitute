package com.example.merainstitue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;  // Use android.util.Base64 here
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class AccountFragment extends Fragment {

    private ImageView profileImageView;
    private TextView fullNameTextView, emailTextView, usernameTextView;
    //    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase and UI components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileImageView = rootView.findViewById(R.id.profileImage);
        fullNameTextView = rootView.findViewById(R.id.name);
        emailTextView = rootView.findViewById(R.id.userEmail);
        usernameTextView = rootView.findViewById(R.id.username);
        Button logOutButton = rootView.findViewById(R.id.logOut);
//        progressBar = rootView.findViewById(R.id.progressBar);

        // Set log out button listener
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

        // Fetch user data when the fragment is created
        fetchUserData();

        return rootView;
    }

    private void fetchUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            listenerRegistration = userRef.addSnapshotListener((documentSnapshot, e) -> {
//                if (e != null) {
////                    progressBar.setVisibility(View.GONE);
//                    return;
//                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Display user data
                    String fullName = documentSnapshot.getString("fullName");
                    String email = documentSnapshot.getString("email");
                    String base64Image = documentSnapshot.getString("profileImage");

                    // Debugging: Check if the data is being fetched
                    if (fullName != null) {
                        fullNameTextView.setText(fullName);
                        usernameTextView.setText("@" + fullName.toLowerCase().replace(" ", "_"));
                    }

                    if (email != null) {
                        emailTextView.setText(email);
                    }

                    // Decode Base64 image and set it to ImageView
                    if (base64Image != null) {
                        try {
                            // Use android.util.Base64 for backward compatibility
                            byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                            profileImageView.setImageBitmap(decodedBitmap);
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                            // Handle error with decoding image
                        }
                    }

//                    progressBar.setVisibility(View.GONE);
                } else {
//                    progressBar.setVisibility(View.GONE);
                    // Handle case where the document doesn't exist
                    return;
                }
            });
        } else {
            // Handle case where the user is not authenticated (redirect to login screen)
//            progressBar.setVisibility(View.GONE);
            // Navigate to login activity or show login prompt
            return;
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
