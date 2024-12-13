package com.example.merainstitue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button loginButton; // Your login button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login_activity);

        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            // Assuming login is successful, save the logged-in state
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true); // Set the login flag to true
            editor.apply();

            // Redirect to MainActivity after login
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the login screen
        });
    }
}
