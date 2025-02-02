package com.example.merainstitue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.merainstitue.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore.setLoggingEnabled(true);
        // Check if it's the first launch and show onboarding if needed
        if (isFirstLaunch()) {
            setFirstLaunchFalse(); // Mark as not first launch
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check if the user is logged in
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Log.e("checking", prefs.toString());
        Log.e("checking", "SharedPreferences contents:");


        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.e("checking", entry.getKey() + ": " + entry.getValue().toString());
        }
        boolean isValidLogin = prefs.getBoolean("isValidLogin", false);
        Log.d("LoginCheck", "isValidLogin: " + isValidLogin); // Debugging
        if (!isValidLogin) {
            // Redirect to LoginActivity if the user is not logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Load MainActivity layout if user is logged in
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup bottom navigation
        setupBottomNavigation();

        // Load HomeFragment as the default fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment()); // Load the home fragment by default
        }
    }

    private void setupBottomNavigation() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("role", "student"); // Default to "student" if not set

        // Check if the user is a teacher and only then show the "Courses" tab
        binding.bottomNavigationView2.getMenu().findItem(R.id.course).setVisible(userRole.equals("teacher"));
        binding.bottomNavigationView2.getMenu().findItem(R.id.courses).setVisible(userRole.equals("student"));

        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.course) {
                replaceFragment(new CourseFragment());
                return true;
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
                return true;
            } else if (item.getItemId() == R.id.courses) {
                replaceFragment(new MessageFragment());
                return true;
            } else if (item.getItemId() == R.id.account) {
                replaceFragment(new AccountFragment());
                return true;
            } else {
                return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private boolean isFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isFirstLaunch", true);
    }

    private void setFirstLaunchFalse() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstLaunch", false).apply();
    }

    // Method to hide the bottom navigation
    public void hideBottomNavigation() {
        binding.bottomNavigationView2.setVisibility(View.GONE);
    }

    // Method to show the bottom navigation
    public void showBottomNavigation() {
        binding.bottomNavigationView2.setVisibility(View.VISIBLE);
    }
}
