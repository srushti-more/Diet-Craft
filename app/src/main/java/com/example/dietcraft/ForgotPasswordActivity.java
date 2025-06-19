package com.example.dietcraft;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailInput;
    Button sendEmailButton;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Create this layout

        db = new DatabaseHelper(this);
        emailInput = findViewById(R.id.emailInput); // Make sure to create this EditText in your layout
        sendEmailButton = findViewById(R.id.sendEmailButton); // Create this button in your layout

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();

                // Check if the email exists in the database
                if (db.checkEmailExists(email)) {
                    // Here you would implement the logic to send an email with an OTP
                    // For demonstration, we'll just show a Toast
                    Toast.makeText(ForgotPasswordActivity.this, "An email has been sent to " + email + " with instructions to reset your password.", Toast.LENGTH_LONG).show();

                    // Redirect to LoginActivity after sending the email
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "This email is not registered.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}