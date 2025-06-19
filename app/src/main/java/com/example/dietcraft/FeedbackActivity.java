package com.example.dietcraft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    EditText editTextFeedback;
    Button buttonSubmitFeedback;
    DatabaseHelper db; // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize views
        editTextFeedback = findViewById(R.id.editTextFeedback);
        buttonSubmitFeedback = findViewById(R.id.buttonSubmitFeedback);

        // Initialize the database helper
        db = new DatabaseHelper(this);

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = editTextFeedback.getText().toString().trim();
                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
                } else {
                    // Retrieve the user's email from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    String email = sharedPreferences.getString("email", null);

                    if (email != null) {
                        // Save feedback to the database with the user's email
                        boolean success = db.insertFeedback(email, feedback); // Fixed: Replaced saveFeedback with insertFeedback
                        if (success) {
                            Toast.makeText(FeedbackActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                            editTextFeedback.setText(""); // Clear input field
                            finish(); // Close the activity after submission
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FeedbackActivity.this, "User not logged in. Please log in to submit feedback.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close(); // Close the database to prevent leaks
    }
}