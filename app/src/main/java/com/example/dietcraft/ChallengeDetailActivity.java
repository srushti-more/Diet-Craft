package com.example.dietcraft;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengeDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private static final String TAG = "ChallengeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        db = new DatabaseHelper(this);

        String challengeName = getIntent().getStringExtra("challenge_name");
        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");

        Log.d(TAG, "Received: challenge_name=" + challengeName + ", email=" + email + ", username=" + username);

        TextView challengeNameText = findViewById(R.id.challengeNameTextView);
        TextView challengeDescText = findViewById(R.id.challenge_description);

        if (challengeName == null || challengeName.isEmpty()) {
            Log.e(TAG, "Challenge name is null or empty");
//            Toast.makeText(this, "Error loading challenge", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        challengeNameText.setText(challengeName);

        String description = db.getChallengeDescription(challengeName);
        if (description.isEmpty()) {
            Log.w(TAG, "No description found for challenge: " + challengeName);
            challengeDescText.setText("No description available");
        } else {
            challengeDescText.setText(description);
            Log.d(TAG, "Loaded description: " + description);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Log.d(TAG, "Database closed");
    }
}


