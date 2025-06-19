package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText email, username, password;
    private Button signupButton;
    private TextView loginLink;
    private DatabaseHelper db;
    private static final String TAG = "SignupActivity";

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        try {
            db = new DatabaseHelper(this);
            email = findViewById(R.id.email2);
            username = findViewById(R.id.username);
            password = findViewById(R.id.password2);
            signupButton = findViewById(R.id.signbut);
            loginLink = findViewById(R.id.tologin);

            signupButton.setEnabled(false);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    signupButton.setEnabled(!email.getText().toString().isEmpty() &&
                            !username.getText().toString().isEmpty() &&
                            !password.getText().toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };

            email.addTextChangedListener(textWatcher);
            username.addTextChangedListener(textWatcher);
            password.addTextChangedListener(textWatcher);

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailInput = email.getText().toString().trim();
                    String usernameInput = username.getText().toString().trim();
                    String passwordInput = password.getText().toString();

                    if (!isValidEmail(emailInput)) {
                        Toast.makeText(SignupActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isValidUsername(usernameInput)) {
                        Toast.makeText(SignupActivity.this, "Username can only contain letters, numbers, and underscores.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isValidPassword(passwordInput)) {
                        Toast.makeText(SignupActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String normalizedEmail = emailInput.toLowerCase();
                    Log.d(TAG, "Attempting to register email: " + normalizedEmail + ", username: " + usernameInput);

                    if (db.checkEmailExists(normalizedEmail)) {
                        Toast.makeText(SignupActivity.this, "This account is already registered. Please login.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // Pass a default value for dietPreference
                        boolean isInserted = db.insertData(normalizedEmail, usernameInput, passwordInput, "none");
                        if (isInserted) {
                            Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                            editor.putString("username", usernameInput);
                            editor.putString("email", normalizedEmail);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();
                            Log.d(TAG, "Saved to SharedPreferences: email=" + normalizedEmail + ", username=" + usernameInput);

                            Intent intent = new Intent(SignupActivity.this, FirstPage.class);
                            intent.putExtra("username", usernameInput);
                            intent.putExtra("email", normalizedEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to insert email: " + normalizedEmail);
                        }
                    }
                }
            });

            loginLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(SignupActivity.this, "Opening LoginActivity", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
//            Toast.makeText(this, "Error in SignupActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Initialization error: ", e);
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    private boolean isValidUsername(String username) {
        return Pattern.matches(USERNAME_PATTERN, username);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}