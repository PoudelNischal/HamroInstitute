package com.example.merainstitue.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.merainstitue.R;
import com.example.merainstitue.MessageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    private static final String CHANNEL_ID = "course_purchase_channel";
    private static final String CHANNEL_NAME = "Course Purchases";
    private static final String TAG = "NotificationHelper";

    private final Context context;
    private final NotificationManager notificationManager;
    private final FirebaseFirestore db;

    public NotificationHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for course purchases and updates");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showCoursePurchaseNotification(String courseId, String courseTitle, double price) {
        // Create intent for notification click
        Intent intent = new Intent(context, MessageFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Create and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Purchase Successful!")
                .setContentText("You now have access to " + courseTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        // Store in Firebase
        storeNotification(courseId, courseTitle, price);
    }

    private void storeNotification(String courseId, String courseTitle, double price) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("courseId", courseId);
        notification.put("title", "Purchase Successful!");
        notification.put("message", "You have successfully purchased " + courseTitle + " for $" + price);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("isRead", false);
        notification.put("type", "purchase");

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification stored with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error storing notification: ", e);
                });
    }
}
