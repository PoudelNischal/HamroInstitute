package com.example.merainstitue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.merainstitue.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        boolean isValidLogin = prefs.getBoolean("isValidLogin", false);  // Check the stored login state

        if (isValidLogin) {
            // If user is logged in, display the main content
            setContentView(R.layout.activity_main); // Ensure the main activity layout is set

            // Initialize the binding for bottom navigation
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setupBottomNavigation();  // Set up bottom navigation

        } else {
            // If the user is not logged in, navigate to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupBottomNavigation() {
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
            } else if (item.getItemId() == R.id.message) {
                replaceFragment(new MessageFragment());
                return true;
            } else if (item.getItemId() == R.id.account) {
                replaceFragment(new AccountFragment());
                return true;
            }
            return false;
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
}
