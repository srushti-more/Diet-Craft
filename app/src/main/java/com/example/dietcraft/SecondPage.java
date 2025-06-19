package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SecondPage extends AppCompatActivity {

    private static final String TAG = "SecondPage";
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText ageEditText;
    private RadioGroup genderRadioGroup;
    private Button nextButton2;
    private DatabaseHelper db;
    private String email;
    private String username;
    private String dietPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_page);

        try {
            db = new DatabaseHelper(this);
            heightEditText = findViewById(R.id.heightEditText);
            weightEditText = findViewById(R.id.weightEditText);
            ageEditText = findViewById(R.id.ageEditText);
            genderRadioGroup = findViewById(R.id.genderRadioGroup);
            nextButton2 = findViewById(R.id.nextButton2);

            // Retrieve Intent extras
            email = getIntent().getStringExtra("email");
            username = getIntent().getStringExtra("username");
            dietPref = getIntent().getStringExtra("diet_preference");
            email = email != null ? email.toLowerCase() : "";
            Log.d(TAG, "Received email: " + email + ", username: " + username + ", diet: " + dietPref);

            // Validate email
            if (email.isEmpty()) {
                Toast.makeText(this, "Error: User email not found.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Verify email exists in database
            if (!db.checkEmailExists(email)) {
                Toast.makeText(this, "Error: Email not registered in database.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Email not found in users table: " + email);
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                return;
            }

            // Save to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.putString("email", email);
            editor.putString("username", username);
            editor.putString("diet_preference", dietPref);
            editor.apply();
            Log.d(TAG, "Saved to SharedPreferences: email=" + email + ", username=" + username);

            nextButton2.setOnClickListener(v -> {
                try {
                    String heightStr = heightEditText.getText().toString().trim();
                    String weightStr = weightEditText.getText().toString().trim();
                    String ageStr = ageEditText.getText().toString().trim();
                    int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                    RadioButton selectedGenderRadioButton = selectedGenderId != -1 ? findViewById(selectedGenderId) : null;
                    String genderStr = selectedGenderRadioButton != null ? selectedGenderRadioButton.getText().toString().toLowerCase() : "male";

                    if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
                        Toast.makeText(SecondPage.this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float height = Float.parseFloat(heightStr);
                    float weight = Float.parseFloat(weightStr);
                    int age = Integer.parseInt(ageStr);

                    if (height <= 0 || weight <= 0 || age <= 0 || age > 120) {
                        Toast.makeText(SecondPage.this, "Invalid input: Height, weight must be positive, age (1-120).", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean heightWeightSuccess = db.updateHeightAndWeight(email, height, weight);
                    boolean ageGenderSuccess = db.updateAgeAndGender(email, age, genderStr);

                    if (!heightWeightSuccess || !ageGenderSuccess) {
                        Log.w(TAG, "Database update failed - heightWeightSuccess: " + heightWeightSuccess + ", ageGenderSuccess: " + ageGenderSuccess);
                    }

//                    Toast.makeText(SecondPage.this, "Height, weight, age, and gender saved.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Saved height: " + height + ", weight: " + weight + ", age: " + age + ", gender: " + genderStr + " for email: " + email);

                    // Save to SharedPreferences
                    editor.putInt("age", age);
                    editor.putString("gender", genderStr);
                    editor.apply();

                    // Start NavBar activity
                    Intent intent = new Intent(SecondPage.this, NavBar.class);
                    intent.putExtra("email", email);
                    intent.putExtra("username", username);
                    intent.putExtra("diet_preference", dietPref);
                    intent.putExtra("age", age);
                    intent.putExtra("gender", genderStr);
                    try {
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(SecondPage.this, "Error launching NavBar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error starting NavBar activity: ", e);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(SecondPage.this, "Enter valid numbers for height, weight, and age.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invalid number format: ", e);
                } catch (Exception e) {
                    Toast.makeText(SecondPage.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Unexpected error in nextButton2: ", e);
                }
            });

            ImageButton backButton = findViewById(R.id.perosonalbackButton);
            backButton.setOnClickListener(v -> finish());

        } catch (Exception e) {
            Toast.makeText(this, "Error initializing SecondPage: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error in onCreate: ", e);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}



