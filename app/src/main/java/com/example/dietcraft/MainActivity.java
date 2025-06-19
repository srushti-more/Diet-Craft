package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button startbtn2, skipbtn, alreadyHaveAccountButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Check if user is logged in
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String email = prefs.getString("email", "");

        Log.d(TAG, "onCreate: isLoggedIn=" + isLoggedIn + ", email=" + email);

        if (isLoggedIn && !email.isEmpty()) {
            // Fetch user details from database
            String username = dbHelper.getUsername(email);
            String dietPref = dbHelper.getDietPreference(email);
            int age = dbHelper.getAge(email);
            String gender = dbHelper.getGender(email);

            Log.d(TAG, "Logged in: email=" + email + ", username=" + username +
                    ", dietPref=" + dietPref + ", age=" + age + ", gender=" + gender);

            // Validate user data
            if (username == null || dietPref == null) {
                Log.e(TAG, "User data missing, clearing login state and redirecting to LoginActivity");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.remove("email");
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }

            // Navigate to NavBar
            Intent intent = new Intent(MainActivity.this, NavBar.class);
            intent.putExtra("email", email);
            intent.putExtra("username", username);
            intent.putExtra("diet_preference", dietPref);
            intent.putExtra("age", age);
            intent.putExtra("gender", gender);
            startActivity(intent);
            finish(); // Close MainActivity to prevent back navigation
            return;
        }

        // Show MainActivity layout for new or logged-out users
        setContentView(R.layout.activity_main);

        startbtn2 = findViewById(R.id.signup2);
        skipbtn = findViewById(R.id.skip);
        alreadyHaveAccountButton = findViewById(R.id.login1);

        if (alreadyHaveAccountButton == null) {
            Toast.makeText(this, "Error: login1 button not found", Toast.LENGTH_LONG).show();
        }

        // Create an Account (to SignupActivity)
        startbtn2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Skip (to NavBar without login)
        skipbtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NavBar.class);
            startActivity(intent);
        });

        // I Already Have an Account (to LoginActivity)
        alreadyHaveAccountButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Opening LoginActivity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}