package com.example.dietcraft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private String email;
    private String username;
    private String dietPref;
    private int age;
    private DatabaseHelper db;
    private ImageView profileImage;
    private TextView profileName;
    private TextView heightTextView;
    private TextView weightTextView;
    private TextView trackerDate;
    private TextView trackerCalories;
    private TextView trackerCarbs;
    private TextView trackerProteins;
    private TextView trackerWeightChange;
    private TextView trackerMonthlyTrend;
    private MaterialButton btnViewAnalysis;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(requireContext());

        // Load initial data from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        username = sharedPreferences.getString("username", "User");
        age = sharedPreferences.getInt("age", 25);

        // Override with arguments if provided
        Bundle args = getArguments();
        if (args != null) {
            String argEmail = args.getString("email");
            String argUsername = args.getString("username");
            dietPref = args.getString("diet_preference");
            age = args.getInt("age", 25);
            if (argEmail != null && !argEmail.isEmpty()) {
                email = argEmail.toLowerCase();
            }
            if (argUsername != null && !argUsername.isEmpty()) {
                username = argUsername;
            }
        }

        Log.d(TAG, "onCreate: Email: " + email + ", Username: " + username + ", DietPref: " + dietPref + ", Age: " + age);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileName.setText(username);

        heightTextView = view.findViewById(R.id.height_text_view);
        weightTextView = view.findViewById(R.id.weight_text_view);
        profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> openImagePicker());

        // Initialize View Analysis Button with updated navigation
        btnViewAnalysis = view.findViewById(R.id.btn_view_analysis);
        btnViewAnalysis.setOnClickListener(v -> {
            if (email != null && !email.isEmpty()) {
                Intent intent = new Intent(requireContext(), AnalysisActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("diet_preference", db.getDietPreference(email));
                intent.putExtra("age", db.getAge(email));
                intent.putExtra("gender", db.getGender(email));
                startActivity(intent);
                Log.d(TAG, "Navigated to AnalysisActivity for email: " + email);
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "View Analysis: Email is null or empty");
            }
        });

        MaterialButton btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        MaterialButton btnFeedback = view.findViewById(R.id.btn_feedback);
        btnFeedback.setOnClickListener(v -> showFeedbackDialog());

        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());

        MaterialButton btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh username from SharedPreferences or database
        refreshUsername();
        // Load profile image
        loadProfileImage();
        // Refresh height and weight
        refreshHeightAndWeight();
        // Update meal tracker
        updateMealTracker();
    }

    private void refreshUsername() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String updatedUsername = sharedPreferences.getString("username", null);
        if (updatedUsername == null || updatedUsername.isEmpty()) {
            // Fallback to database if SharedPreferences is empty
            updatedUsername = db.getUsername(email);
            if (updatedUsername == null) {
                updatedUsername = "User"; // Default if not found
            }
            // Update SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", updatedUsername);
            editor.apply();
        }
        username = updatedUsername;
        profileName.setText(username);
        Log.d(TAG, "refreshUsername: Updated username to: " + username + " for email: " + email);
    }

    private void loadProfileImage() {
        try {
            byte[] imageBytes = db.getProfileImage(email);
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);
                    Log.d(TAG, "loadProfileImage: Loaded image for email: " + email);
                } else {
                    profileImage.setImageResource(R.drawable.profile);
                    Log.e(TAG, "loadProfileImage: Failed to decode bitmap for email: " + email);
                }
            } else {
                profileImage.setImageResource(R.drawable.profile);
                Log.d(TAG, "loadProfileImage: No image found for email: " + email + ", using default image");
            }
        } catch (Exception e) {
            profileImage.setImageResource(R.drawable.defaultprofile);
            Log.e(TAG, "loadProfileImage: Error loading image for email: " + email, e);
        }
    }

    private void refreshHeightAndWeight() {
        if (email != null && !email.isEmpty()) {
            float[] heightAndWeight = db.getHeightAndWeight(email);
            float height = heightAndWeight[0];
            float weight = heightAndWeight[1];
            if (height > 0) {
                heightTextView.setText("Height: " + height + " cm");
            } else {
                heightTextView.setText("Height: Not set");
            }
            if (weight > 0) {
                weightTextView.setText("Weight: " + weight + " kg");
            } else {
                weightTextView.setText("Weight: Not set");
            }
            Log.d(TAG, "refreshHeightAndWeight: Email: " + email + ", Height: " + height + ", Weight: " + weight);
        } else {
            heightTextView.setText("Height: Not set");
            weightTextView.setText("Weight: Not set");
            Toast.makeText(requireContext(), "Error: No email found.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "refreshHeightAndWeight: Email is empty");
        }
    }

    public void updateMealTracker() {
        Log.d(TAG, "updateMealTracker: Tracker TextViews not initialized in provided layout.");
    }

    private float calculateBMR(float weight, float height, int age, String gender) {
        float bmr = 10 * weight + 6.25f * height - 5 * age + (gender.equalsIgnoreCase("male") ? 5 : -161);
        return bmr;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageBytes = stream.toByteArray();

                if (email != null && !email.isEmpty()) {
                    boolean saved = db.saveProfileImage(requireContext(), email, imageBytes);
                    if (saved) {
                        loadProfileImage();
                        Toast.makeText(requireContext(), "Profile image saved.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: Saved image for email: " + email);
                    } else {
                        Toast.makeText(requireContext(), "Failed to save profile image.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onActivityResult: Failed to save image for email: " + email);
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: User email not found.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onActivityResult: Email is null or empty");
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error loading image.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onActivityResult: Error processing image", e);
            }
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Username");

        final EditText input = new EditText(requireContext());
        input.setText(username); // Use current username
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                if (db.updateUsername(email, newUsername)) {
                    // Update SharedPreferences
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
                    editor.putString("username", newUsername);
                    editor.apply();
                    // Update local field and UI
                    username = newUsername;
                    profileName.setText(newUsername);
                    Toast.makeText(requireContext(), "Username updated.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "showEditProfileDialog: Username updated to: " + newUsername + " for email: " + email);
                } else {
                    Toast.makeText(requireContext(), "Failed to update username.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "showEditProfileDialog: Failed to update username for email: " + email);
                }
            } else {
                Toast.makeText(requireContext(), "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "showEditProfileDialog: Empty username entered for email: " + email);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Feedback");

        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String feedback = input.getText().toString().trim();
            if (!feedback.isEmpty()) {
                if (db.insertFeedback(email, feedback)) {
                    Toast.makeText(requireContext(), "Feedback submitted successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Feedback cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Account");
        builder.setMessage("Please enter your password to confirm account deletion.");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();
            if (email == null || email.isEmpty()) {
                Toast.makeText(requireContext(), "Error: Email not found.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "confirmDeleteAccount: Email is null or empty");
                return;
            }
            if (enteredPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Error: Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "confirmDeleteAccount: Attempting deletion for email: " + email);
            boolean deleted = db.deleteAccount(email, enteredPassword);
            if (deleted) {
                Toast.makeText(requireContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(requireContext(), SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Error deleting account. Incorrect password or try again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "confirmDeleteAccount: Deletion failed for email: " + email);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}


//package com.example.dietcraft;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.InputType;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.google.android.material.button.MaterialButton;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//public class ProfileFragment extends Fragment {
//
//    private static final int PICK_IMAGE = 1;
//    private String email;
//    private String username;
//    private String dietPref;
//    private int age;
//    private DatabaseHelper db;
//    private ImageView profileImage;
//    private TextView profileName;
//    private TextView heightTextView;
//    private TextView weightTextView;
//    private TextView trackerDate;
//    private TextView trackerCalories;
//    private TextView trackerCarbs;
//    private TextView trackerProteins;
//    private TextView trackerWeightChange;
//    private TextView trackerMonthlyTrend;
//    private MaterialButton btnViewAnalysis;
//    private static final String TAG = "ProfileFragment";
//
//    public ProfileFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        db = new DatabaseHelper(requireContext());
//
//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        email = sharedPreferences.getString("email", "");
//        username = sharedPreferences.getString("username", "User");
//        age = sharedPreferences.getInt("age", 25);
//
//        Bundle args = getArguments();
//        if (args != null) {
//            String argEmail = args.getString("email");
//            String argUsername = args.getString("username");
//            dietPref = args.getString("diet_preference");
//            age = args.getInt("age", 25);
//            if (argEmail != null && !argEmail.isEmpty()) {
//                email = argEmail.toLowerCase();
//            }
//            if (argUsername != null && !argUsername.isEmpty()) {
//                username = argUsername;
//            }
//        }
//
//        Log.d(TAG, "onCreate: Email: " + email + ", Username: " + username + ", DietPref: " + dietPref + ", Age: " + age);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        profileName = view.findViewById(R.id.profile_name);
//        profileName.setText(username);
//
//        heightTextView = view.findViewById(R.id.height_text_view);
//        weightTextView = view.findViewById(R.id.weight_text_view);
//        profileImage = view.findViewById(R.id.profile_image);
//        profileImage.setOnClickListener(v -> openImagePicker());
//
//        // Load profile image from database
//        loadProfileImage();
//
//        // Initialize View Analysis Button with updated navigation
//        btnViewAnalysis = view.findViewById(R.id.btn_view_analysis);
//        btnViewAnalysis.setOnClickListener(v -> {
//            if (email != null && !email.isEmpty()) {
//                Intent intent = new Intent(requireContext(), AnalysisActivity.class);
//                intent.putExtra("email", email);
//                intent.putExtra("diet_preference", db.getDietPreference(email));
//                intent.putExtra("age", db.getAge(email));
//                intent.putExtra("gender", db.getGender(email));
//                startActivity(intent);
//                Log.d(TAG, "Navigated to AnalysisActivity for email: " + email);
//            } else {
//                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "View Analysis: Email is null or empty");
//            }
//        });
//
//        MaterialButton btnEditProfile = view.findViewById(R.id.btn_edit_profile);
//        btnEditProfile.setOnClickListener(v -> showEditUsernameDialog());
//
//        MaterialButton btnFeedback = view.findViewById(R.id.btn_feedback);
//        btnFeedback.setOnClickListener(v -> showFeedbackDialog());
//
//        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
//        btnLogout.setOnClickListener(v -> logoutUser());
//
//        MaterialButton btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
//        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
//
//        refreshHeightAndWeight();
//        updateMealTracker();
//
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        refreshHeightAndWeight();
//        updateMealTracker();
//        loadProfileImage();
//    }
//
//    private void loadProfileImage() {
//        if (email != null && !email.isEmpty()) {
//            byte[] imageBytes = db.getProfileImage(email);
//            if (imageBytes != null && imageBytes.length > 0) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                profileImage.setImageBitmap(bitmap);
//                Log.d(TAG, "loadProfileImage: Loaded image for email: " + email);
//            } else {
//                profileImage.setImageResource(android.R.drawable.ic_menu_gallery);
//                Log.d(TAG, "loadProfileImage: No image found for email: " + email);
//            }
//        } else {
//            profileImage.setImageResource(android.R.drawable.ic_menu_gallery);
//            Log.e(TAG, "loadProfileImage: Email is null or empty");
//        }
//    }
//
//    private void refreshHeightAndWeight() {
//        if (!email.isEmpty()) {
//            float[] heightAndWeight = db.getHeightAndWeight(email);
//            float height = heightAndWeight[0];
//            float weight = heightAndWeight[1];
//            if (height > 0) {
//                heightTextView.setText("Height: " + height + " cm");
//            } else {
//                heightTextView.setText("Height: Not set");
//            }
//            if (weight > 0) {
//                weightTextView.setText("Weight: " + weight + " kg");
//            } else {
//                weightTextView.setText("Weight: Not set");
//            }
//            Log.d(TAG, "refreshHeightAndWeight: Email: " + email + ", Height: " + height + ", Weight: " + weight);
//        } else {
//            heightTextView.setText("Height: Not set");
//            weightTextView.setText("Weight: Not set");
//            Toast.makeText(getActivity(), "Error: No email found.", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "refreshHeightAndWeight: Email is empty");
//        }
//    }
//
//    public void updateMealTracker() {
//        Log.d(TAG, "updateMealTracker: Tracker TextViews not initialized in provided layout.");
//    }
//
//    private float calculateBMR(float weight, float height, int age, String gender) {
//        float bmr = 10 * weight + 6.25f * height - 5 * age + (gender.equals("male") ? 5 : -161);
//        return bmr;
//    }
//
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
//            Uri selectedImage = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
//                profileImage.setImageBitmap(bitmap);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] imageBytes = stream.toByteArray();
//
//                if (email != null && !email.isEmpty()) {
//                    boolean saved = db.saveProfileImage(email, imageBytes);
//                    if (saved) {
//                        Toast.makeText(requireActivity(), "Profile image saved.", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "onActivityResult: Saved image for email: " + email);
//                    } else {
//                        Toast.makeText(requireActivity(), "Failed to save profile image.", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onActivityResult: Failed to save image for email: " + email);
//                    }
//                  } else {
//                    Toast.makeText(requireActivity(), "Error: User email not found.", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "onActivityResult: Email is null or empty");
//                }
//            } catch (IOException e) {
//                Toast.makeText(requireActivity(), "Error loading image.", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "onActivityResult: Error loading image: " + e.getMessage());
//            }
//        }
//    }
//
//    private void showEditUsernameDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setTitle("Edit Username");
//
//        final EditText input = new EditText(requireActivity());
//        input.setText(profileName.getText().toString());
//        builder.setView(input);
//
//        builder.setPositiveButton("OK", (dialog, which) -> {
//            String newUsername = input.getText().toString().trim();
//            if (!newUsername.isEmpty()) {
//                if (db.updateUsername(email, newUsername)) {
//                    profileName.setText(newUsername);
//                    username = newUsername;
//                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
//                    editor.putString("username", newUsername);
//                    editor.apply();
//                    Toast.makeText(requireActivity(), "Username updated.", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "Username updated to: " + newUsername + " for email: " + email);
//                } else {
//                    Toast.makeText(requireActivity(), "Failed to update username.", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Failed to update username for email: " + email);
//                }
//            } else {
//                Toast.makeText(requireActivity(), "Username cannot be empty.", Toast.LENGTH_SHORT).show();
//                Log.w(TAG, "Empty username entered for email: " + email);
//            }
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }
//
//    private void showFeedbackDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setTitle("Feedback");
//
//        final EditText input = new EditText(requireActivity());
//        builder.setView(input);
//
//        builder.setPositiveButton("Submit", (dialog, which) -> {
//            String feedback = input.getText().toString().trim();
//            if (!feedback.isEmpty()) {
//                if (db.insertFeedback(email, feedback)) {
//                    Toast.makeText(requireActivity(), "Feedback submitted successfully.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(requireActivity(), "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(requireActivity(), "Feedback cannot be empty.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }
//
//    private void logoutUser() {
//        SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
//        editor.clear();
//        editor.apply();
//
//        Toast.makeText(requireActivity(), "Logged out", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(requireActivity(), LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        requireActivity().finish();
//    }
//
//    private void confirmDeleteAccount() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setTitle("Delete Account");
//        builder.setMessage("Please enter your password to confirm account deletion.");
//
//        final EditText input = new EditText(requireActivity());
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(input);
//
//        builder.setPositiveButton("Delete", (dialog, which) -> {
//            String enteredPassword = input.getText().toString().trim();
//            if (email == null || email.isEmpty()) {
//                Toast.makeText(requireActivity(), "Error: Email not found.", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "confirmDeleteAccount: Email is null or empty");
//                return;
//            }
//            if (enteredPassword.isEmpty()) {
//                Toast.makeText(requireActivity(), "Error: Password cannot be empty.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Log.d(TAG, "confirmDeleteAccount: Attempting deletion for email: " + email);
//            boolean deleted = db.deleteAccount(email, enteredPassword);
//            if (deleted) {
//                Toast.makeText(requireActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
//                SharedPreferences.Editor editor = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
//                editor.clear();
//                editor.apply();
//                Intent intent = new Intent(requireActivity(), SignupActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                requireActivity().finish();
//            } else {
//                Toast.makeText(requireActivity(), "Error deleting account. Incorrect password or try again.", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "confirmDeleteAccount: Deletion failed for email: " + email);
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (db != null) {
//            db.close();
//        }
//    }
//}