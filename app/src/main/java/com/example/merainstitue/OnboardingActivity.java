package com.example.merainstitue;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merainstitue.databinding.OnboardingActivityBinding;

public class OnboardingActivity extends AppCompatActivity {

    OnboardingActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding
        binding = OnboardingActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the continue button click listener
        binding.continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish OnboardingActivity so the user can't navigate back to it
        });
    }
}