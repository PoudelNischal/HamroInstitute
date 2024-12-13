// MainActivity.java
package com.example.merainstitue;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        boolean isValidLogin = false;

        // Your MainActivity logic here
        if(isValidLogin == true){
        setContentView(R.layout.fragment_login_activity);
        }else{
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setupBottomNavigation();
        }

    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView2.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.course) {
                replaceFragment(new CourseFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.message) {
                replaceFragment(new MessageFragment());
            } else {
                replaceFragment(new AccountFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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