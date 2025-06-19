package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class RecipeLibraryFragment extends Fragment {

    private String dietPref;
    private String email;
    private String username;
    private String selectedDate; // Field for selected date
    private DatabaseHelper db;
    private static final String TAG = "RecipeLibraryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(requireContext());
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", "");
        username = prefs.getString("username", "");
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email", email).toLowerCase();
            username = args.getString("username", username);
            dietPref = args.getString("diet_preference");
            selectedDate = args.getString("selected_date"); // Receive selected date
        }
        if (dietPref == null) {
            dietPref = db.getDietPreference(email);
        }
        // If no date provided, try to get from SharedPreferences (set by HomeFragment)
        if (selectedDate == null) {
            selectedDate = prefs.getString("selected_date", null);
        }
        Log.d(TAG, "Initialized with email: " + email + ", username: " + username + ", diet: " + dietPref + ", date: " + selectedDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_library, container, false);

        CardView cardBreakfast = view.findViewById(R.id.card_breakfast);
        CardView cardLunch = view.findViewById(R.id.card_lunch);
        CardView cardDinner = view.findViewById(R.id.card_dinner);
        CardView cardHealthTips = view.findViewById(R.id.card_healthtips);

        cardBreakfast.setOnClickListener(v -> navigateToRecipeContent("Breakfast"));
        cardLunch.setOnClickListener(v -> navigateToRecipeContent("Lunch"));
        cardDinner.setOnClickListener(v -> navigateToRecipeContent("Dinner"));
        cardHealthTips.setOnClickListener(v -> navigateToHealthTips());

        return view;
    }

    private void navigateToRecipeContent(String mealType) {
        Intent intent = new Intent(requireActivity(), RecipeContent.class);
        intent.putExtra("meal_type", mealType);
        intent.putExtra("diet_preference", dietPref);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        if (selectedDate != null) {
            intent.putExtra("selected_date", selectedDate);
        }
        startActivity(intent);
        Log.d(TAG, "Navigated to RecipeContent: " + mealType + ", diet=" + dietPref + ", date=" + selectedDate);
    }

    private void navigateToHealthTips() {
        Intent intent = new Intent(requireActivity(), HealthTipsActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        startActivity(intent);
        Log.d(TAG, "Navigated to HealthTipsActivity, email=" + email);
    }
}