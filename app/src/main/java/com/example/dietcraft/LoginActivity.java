package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton;
    private TextView signupLink, forgotPasswordLink;
    private DatabaseHelper db;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            db = new DatabaseHelper(this);
            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            loginButton = findViewById(R.id.logbut);
            signupLink = findViewById(R.id.tosignup);
            forgotPasswordLink = findViewById(R.id.pass);

            // Check if user is already logged in
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String savedEmail = sharedPreferences.getString("email", null);
            if (sharedPreferences.getBoolean("isLoggedIn", false) && savedEmail != null && db.checkEmailExists(savedEmail)) {
                Toast.makeText(this, "Already logged in, redirecting to NavBar", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, NavBar.class);
                intent.putExtra("email", savedEmail);
                intent.putExtra("username", db.getUsername(savedEmail));
                intent.putExtra("diet_preference", db.getDietPreference(savedEmail));
                intent.putExtra("age", db.getAge(savedEmail));
                intent.putExtra("gender", db.getGender(savedEmail));
                try {
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
//                    Toast.makeText(this, "Error launching NavBar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error starting NavBar activity: ", e);
                }
                return;
            }

            // Enable login button only if fields are filled
            loginButton.setEnabled(false);

            // TextWatcher to enable login button when both fields are filled
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loginButton.setEnabled(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };

            email.addTextChangedListener(textWatcher);
            password.addTextChangedListener(textWatcher);

            // Login button click
            loginButton.setOnClickListener(v -> {
                try {
                    String emailInput = email.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();

                    if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Normalize email
                    String normalizedEmail = emailInput.toLowerCase();
                    Log.d(TAG, "Attempting login with email: " + normalizedEmail);

                    if (db.checkUser(normalizedEmail, passwordInput)) {
                        String username = db.getUsername(normalizedEmail);
                        String dietPref = db.getDietPreference(normalizedEmail);
                        int age = db.getAge(normalizedEmail);
                        String gender = db.getGender(normalizedEmail);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("email", normalizedEmail);
                        editor.putString("diet_preference", dietPref);
                        editor.putInt("age", age);
                        editor.putString("gender", gender);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

//                        Toast.makeText(LoginActivity.this, "Logged in as " + username, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, NavBar.class);
                        intent.putExtra("username", username);
                        intent.putExtra("email", normalizedEmail);
                        intent.putExtra("diet_preference", dietPref);
                        intent.putExtra("age", age);
                        intent.putExtra("gender", gender);
                        try {
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Error launching NavBar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error starting NavBar activity: ", e);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Login failed for email: " + normalizedEmail);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error during login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error in login: ", e);
                }
            });

            // Redirect to SignupActivity
            signupLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

            // Redirect to ForgotPasswordActivity
            forgotPasswordLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        } catch (Exception e) {
            Toast.makeText(this, "Error in LoginActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Initialization error: ", e);
        }
    }
}


