package com.example.merainstitue.utils;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.FirebaseInstallationsException;

public class FCMTokenManager {

    private static final String TAG = "FCMTokenManager";

    // Method to get the FCM token
    public static void getFCMToken() {
        FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Token retrieval failed", task.getException());
                        // Retry if Firebase Installations service is unavailable
                        handleFcmRetry(task.getException());
                        return;
                    }

                    // Get the token and save it to your server
                    String token = task.getResult().getToken();
                    Log.d(TAG, "FCM Token: " + token);
                    // TODO: Send this token to your server
                });
    }

    // Handle retrying FCM token retrieval if there are errors
    private static void handleFcmRetry(Exception exception) {
        if (exception instanceof FirebaseInstallationsException) {
            FirebaseInstallationsException installationsException = (FirebaseInstallationsException) exception;
            if (installationsException.getStatus() == FirebaseInstallationsException.Status.UNAVAILABLE) {
                Log.e(TAG, "Firebase Installations Service is unavailable. Retrying...");
                // Optionally, you can retry after a short delay
                new Handler().postDelayed(FCMTokenManager::getFCMToken, 5000);  // Retry after 5 seconds
            }
        }
    }
}
