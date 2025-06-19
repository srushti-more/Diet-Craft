package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class FirstPage extends AppCompatActivity {

    private static final String TAG = "FirstPage";
    private RadioGroup radioGroup;
    private Button nextButton1;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_page);

        try {
            db = new DatabaseHelper(this);
            String username = getIntent().getStringExtra("username");
            String email = getIntent().getStringExtra("email");
            String normalizedEmail = email != null ? email.toLowerCase() : null;
            Log.d(TAG, "Received username: " + username + ", email: " + normalizedEmail);

            if (normalizedEmail == null || normalizedEmail.isEmpty()) {
//                Toast.makeText(this, "Error: User email not found.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                return;
            }

            if (!db.checkEmailExists(normalizedEmail)) {
//                Toast.makeText(this, "Error: Email not registered.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                return;
            }

            // Save to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.putString("email", normalizedEmail);
            editor.putString("username", username);
            editor.apply();
            Log.d(TAG, "Saved to SharedPreferences: email=" + normalizedEmail + ", username=" + username);

            radioGroup = findViewById(R.id.radioGroup);
            nextButton1 = findViewById(R.id.nextButton);

            nextButton1.setOnClickListener(v -> {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedDiet = selectedRadioButton.getText().toString();
                    Toast.makeText(FirstPage.this, "Selected Diet: " + selectedDiet, Toast.LENGTH_SHORT).show();

                    // Save diet preference
                    boolean success = db.updateDietPreference(normalizedEmail, selectedDiet);
                    if (!success) {
                        Toast.makeText(this, "Failed to save diet preference.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Failed to save diet preference for email: " + normalizedEmail);
                        return;
                    }

                    Intent intent = new Intent(FirstPage.this, SecondPage.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", normalizedEmail);
                    intent.putExtra("diet_preference", selectedDiet);
                    Log.d(TAG, "Sending to SecondPage - username: " + username + ", email: " + normalizedEmail + ", diet: " + selectedDiet);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(FirstPage.this, "Please select a diet option.", Toast.LENGTH_SHORT).show();
                }
            });

            ImageButton backButton = findViewById(R.id.firstpagebackButton);
            backButton.setOnClickListener(v -> finish());

        } catch (Exception e) {
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error in onCreate: ", e);
            finish();
        }
    }
}



