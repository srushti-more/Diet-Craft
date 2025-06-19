package com.example.dietcraft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView breakfastTextView, lunchTextView, dinnerTextView;
    private TextView calendarTextView, usernameTextView;
    private ImageView iconLeft, iconRight;
    private Button reminderButton;
    private CardView breakfastCardView, lunchCardView, dinnerCardView;
    private LinearLayout followedChallengesList;
    private Calendar calendar;
    private DatabaseHelper db;
    private String email;
    private String dietPref;
    private String username;
    private SharedPreferences prefs;
    private static final String TAG = "HomeFragment";
    private ActivityResultLauncher<Intent> recipeContentLauncher;
    private BroadcastReceiver challengeUpdateReceiver;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        try {
            db = new DatabaseHelper(requireContext());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing DatabaseHelper: ", e);
            Toast.makeText(requireContext(), "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", "").toLowerCase();
        username = prefs.getString("username", "");
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email", email).toLowerCase();
            username = args.getString("username", username);
            dietPref = args.getString("diet_preference", "");
        }
        if (dietPref == null || dietPref.isEmpty()) {
            dietPref = db != null ? db.getDietPreference(email) : "";
        }
        if (dietPref == null || dietPref.isEmpty()) {
            dietPref = "Weight Loss";
            Log.w(TAG, "No diet preference found for email: " + email + ", using default: Weight Loss");
        }
        Log.d(TAG, "Initialized with email: " + email + ", username: " + username + ", diet: " + dietPref);

        recipeContentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        Log.d(TAG, "Recipe added successfully, refreshing meal plans");
                        loadMealPlans();
                    }
                }
        );

        // Register BroadcastReceiver for challenge updates
        challengeUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received challenge update broadcast");
                loadFollowedChallenges();
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                challengeUpdateReceiver, new IntentFilter("CHALLENGE_UPDATED"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            calendarTextView = view.findViewById(R.id.calendarTextView);
            iconLeft = view.findViewById(R.id.icon_left);
            iconRight = view.findViewById(R.id.icon_right);
            breakfastTextView = view.findViewById(R.id.breakfast_meal_text);
            lunchTextView = view.findViewById(R.id.lunch_meal_text);
            dinnerTextView = view.findViewById(R.id.dinner_meal_text);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            reminderButton = view.findViewById(R.id.setReminderButton);
            breakfastCardView = view.findViewById(R.id.breakfastCardView);
            lunchCardView = view.findViewById(R.id.lunchCardView);
            dinnerCardView = view.findViewById(R.id.dinnerCardView);
            followedChallengesList = view.findViewById(R.id.followed_challenges_list);

            if (calendarTextView == null || iconLeft == null || iconRight == null ||
                    breakfastTextView == null || lunchTextView == null || dinnerTextView == null ||
                    usernameTextView == null || reminderButton == null ||
                    breakfastCardView == null || lunchCardView == null || dinnerCardView == null ||
                    followedChallengesList == null) {
                Log.e(TAG, "One or more views not found in fragment_home.xml");
                Toast.makeText(requireContext(), "Error: UI components missing", Toast.LENGTH_LONG).show();
                return view;
            }

            if (username == null && !email.isEmpty() && db != null) {
                username = db.getUsername(email);
                if (username != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.apply();
                }
            }
            usernameTextView.setText(username != null && !username.isEmpty() ? "Welcome, " + username + "!" : "Welcome, User!");

            reminderButton.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ReminderActivity.class);
                intent.putExtra("email", email);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting ReminderActivity: ", e);
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            breakfastCardView.setOnClickListener(v -> navigateToRecipeDetail("Breakfast", breakfastTextView.getText().toString()));
            lunchCardView.setOnClickListener(v -> navigateToRecipeDetail("Lunch", lunchTextView.getText().toString()));
            dinnerCardView.setOnClickListener(v -> navigateToRecipeDetail("Dinner", dinnerTextView.getText().toString()));

            updateDate();
            loadMealPlans();
            loadFollowedChallenges();

            iconLeft.setOnClickListener(v -> {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                updateDate();
                loadMealPlans();
            });

            iconRight.setOnClickListener(v -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                updateDate();
                loadMealPlans();
            });

            calendarTextView.setOnClickListener(v -> {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new android.app.DatePickerDialog(requireContext(),
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            calendar.set(Calendar.YEAR, selectedYear);
                            calendar.set(Calendar.MONTH, selectedMonth);
                            calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                            updateDate();
                            loadMealPlans();
                        }, year, month, day).show();
            });

            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: ", e);
            Toast.makeText(requireContext(), "Error loading HomeFragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void loadFollowedChallenges() {
        if (email.isEmpty() || db == null) {
            Log.e(TAG, "loadFollowedChallenges: Email or db is null");
            return;
        }

        followedChallengesList.removeAllViews();
        ArrayList<Map<String, String>> followedChallenges = db.getFollowedChallenges(email);
        if (followedChallenges.isEmpty()) {
            Log.d(TAG, "No followed challenges for email: " + email);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (Map<String, String> challengeData : followedChallenges) {
            String challengeName = challengeData.get("challenge_name");
            if (challengeName == null) {
                Log.w(TAG, "Null challenge name in followed challenges");
                continue;
            }

            View challengeCard = inflater.inflate(R.layout.challenge_card_no_image, followedChallengesList, false);
            TextView name = challengeCard.findViewById(R.id.challenge_name);
            TextView desc = challengeCard.findViewById(R.id.challenge_desc);
            Button followButton = challengeCard.findViewById(R.id.follow_button);

            if (name == null || desc == null || followButton == null) {
                Log.e(TAG, "Challenge card view components missing for challenge: " + challengeName);
                continue;
            }

            name.setText(challengeName);
            desc.setText(db.getChallengeDescription(challengeName));

            followButton.setOnClickListener(v -> {
                boolean success = db.removeFollowedChallenge(email, challengeName);
                if (success) {
                    Toast.makeText(requireContext(), "Unfollowed " + challengeName, Toast.LENGTH_SHORT).show();
                    loadFollowedChallenges();
                    // Notify other fragments
                    Intent intent = new Intent("CHALLENGE_UPDATED");
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                } else {
                    Toast.makeText(requireContext(), "Failed to unfollow " + challengeName, Toast.LENGTH_SHORT).show();
                }
            });

            followedChallengesList.addView(challengeCard);
        }
        Log.d(TAG, "Loaded " + followedChallenges.size() + " followed challenges");
    }

    private void updateDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
            String date = sdf.format(calendar.getTime()).trim();
            calendarTextView.setText(date);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selected_date", date);
            editor.apply();
            Log.d(TAG, "Updated date to: " + date);
        } catch (Exception e) {
            Log.e(TAG, "Error updating date: ", e);
            Toast.makeText(requireContext(), "Error updating date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMealPlans() {
        if (email.isEmpty() || dietPref.isEmpty() || db == null) {
            Toast.makeText(requireContext(), "User data not available.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "loadMealPlans: Email=" + email + ", dietPref=" + dietPref + ", db=" + (db == null));
            return;
        }

        String displayDate = calendarTextView.getText().toString();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.submit(() -> {
            Map<String, Object> result = new HashMap<>();
            try {
                result.put("displayDate", displayDate);
                result.put("breakfastMeal", db.getMeal(email, displayDate, "Breakfast"));
                result.put("lunchMeal", db.getMeal(email, displayDate, "Lunch"));
                result.put("dinnerMeal", db.getMeal(email, displayDate, "Dinner"));
            } catch (Exception e) {
                result.put("error", e);
            }

            handler.post(() -> {
                if (result.containsKey("error")) {
                    Exception e = (Exception) result.get("error");
                    Log.e(TAG, "Error loading meal plans: ", e);
                    Toast.makeText(requireContext(), "Error loading meals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String displayDateResult = (String) result.get("displayDate");
                String breakfastMeal = (String) result.get("breakfastMeal");
                String lunchMeal = (String) result.get("lunchMeal");
                String dinnerMeal = (String) result.get("dinnerMeal");

                Log.d(TAG, "loadMealPlans: Breakfast=" + breakfastMeal + ", Lunch=" + lunchMeal + ", Dinner=" + dinnerMeal);

                if (breakfastMeal == null || breakfastMeal.equals("Not set")) {
                    breakfastMeal = getDefaultMeal("Breakfast", displayDateResult, new HashSet<>());
                    if (!breakfastMeal.equals("Not set")) {
                        long insertResult = db.insertMeal(email, displayDateResult, "Breakfast", breakfastMeal);
                        if (insertResult != -1) {
                            Log.d(TAG, "Successfully inserted Breakfast meal: " + breakfastMeal + " for date: " + displayDateResult);
                        } else {
                            Log.e(TAG, "Failed to insert Breakfast meal: " + breakfastMeal + " for email: " + email + ", date: " + displayDateResult);
                        }
                    }
                }
                final String finalBreakfast = breakfastMeal != null ? breakfastMeal : "Not set";
                if (lunchMeal == null || lunchMeal.equals("Not set")) {
                    lunchMeal = getDefaultMeal("Lunch", displayDateResult, new HashSet<String>() {{ add(finalBreakfast); }});
                    if (!lunchMeal.equals("Not set")) {
                        long insertResult = db.insertMeal(email, displayDateResult, "Lunch", lunchMeal);
                        if (insertResult != -1) {
                            Log.d(TAG, "Successfully inserted Lunch meal: " + lunchMeal + " for date: " + displayDateResult);
                        } else {
                            Log.e(TAG, "Failed to insert Lunch meal: " + lunchMeal + " for email: " + email + ", date: " + displayDateResult);
                        }
                    }
                }
                final String finalLunch = lunchMeal != null ? lunchMeal : "Not set";
                if (dinnerMeal == null || dinnerMeal.equals("Not set")) {
                    dinnerMeal = getDefaultMeal("Dinner", displayDateResult, new HashSet<String>() {{ add(finalBreakfast); add(finalLunch); }});
                    if (!dinnerMeal.equals("Not set")) {
                        long insertResult = db.insertMeal(email, displayDateResult, "Dinner", dinnerMeal);
                        if (insertResult != -1) {
                            Log.d(TAG, "Successfully inserted Dinner meal: " + dinnerMeal + " for date: " + displayDateResult);
                        } else {
                            Log.e(TAG, "Failed to insert Dinner meal: " + dinnerMeal + " for email: " + email + ", date: " + displayDateResult);
                        }
                    }
                }

                if (breakfastTextView != null) {
                    breakfastTextView.setText("Breakfast: " + (breakfastMeal != null ? breakfastMeal : "Not set"));
                    Log.d(TAG, "Updated breakfastTextView with: " + (breakfastMeal != null ? breakfastMeal : "Not set"));
                } else {
                    Log.e(TAG, "breakfastTextView is null");
                }
                if (lunchTextView != null) {
                    lunchTextView.setText("Lunch: " + (lunchMeal != null ? lunchMeal : "Not set"));
                    Log.d(TAG, "Updated lunchTextView with: " + (lunchMeal != null ? lunchMeal : "Not set"));
                } else {
                    Log.e(TAG, "lunchTextView is null");
                }
                if (dinnerTextView != null) {
                    dinnerTextView.setText("Dinner: " + (dinnerMeal != null ? dinnerMeal : "Not set"));
                    Log.d(TAG, "Updated dinnerTextView with: " + (dinnerMeal != null ? dinnerMeal : "Not set"));
                } else {
                    Log.e(TAG, "dinnerTextView is null");
                }

                Log.d(TAG, "Loaded meals for " + displayDateResult + ": Breakfast=" + (breakfastMeal != null ? breakfastMeal : "Not set") +
                        ", Lunch=" + (lunchMeal != null ? lunchMeal : "Not set") +
                        ", Dinner=" + (dinnerMeal != null ? dinnerMeal : "Not set"));

                db.logAllMealsForDate(email, displayDateResult);
            });
        });

        executor.shutdown();
    }

    private String getDefaultMeal(String mealType, String date, Set<String> usedMeals) {
        try {
            ArrayList<Map<String, String>> recipeList = db != null ? db.getRecipes(dietPref, mealType) : null;
            if (recipeList == null || recipeList.isEmpty()) {
                Log.w(TAG, "getDefaultMeal: No recipes for diet=" + dietPref + ", mealType=" + mealType);
                return "Not set";
            }

            String[] meals = new String[recipeList.size()];
            for (int i = 0; i < recipeList.size(); i++) {
                Map<String, String> recipe = recipeList.get(i);
                meals[i] = recipe.get("recipe_name") != null ? recipe.get("recipe_name") : "Unknown Recipe";
            }

            int hash = (date + mealType).hashCode();
            int index = Math.abs(hash) % meals.length;
            int originalIndex = index;
            do {
                String meal = meals[index];
                if (!usedMeals.contains(meal)) {
                    Log.d(TAG, "getDefaultMeal: Type=" + mealType + ", Date=" + date + ", Selected=" + meal + ", Index=" + index);
                    return meal;
                }
                index = (index + 1) % meals.length;
            } while (index != originalIndex);

            String meal = meals[Math.abs(hash) % meals.length];
            Log.d(TAG, "getDefaultMeal: Type=" + mealType + ", Date=" + date + ", Fallback Selected=" + meal + ", Index=" + index);
            return meal;
        } catch (Exception e) {
            Log.e(TAG, "Error getting default meal: ", e);
            return "Not set";
        }
    }

    private void navigateToRecipeDetail(String mealType, String textViewContent) {
        try {
            String recipeName = textViewContent.replaceFirst("^(Breakfast|Lunch|Dinner):\\s*", "");
            if (recipeName.equals("Not set")) {
                Intent intent = new Intent(requireContext(), RecipeContent.class);
                intent.putExtra("meal_type", mealType);
                intent.putExtra("diet_preference", dietPref);
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                intent.putExtra("selected_date", calendarTextView.getText().toString());
                recipeContentLauncher.launch(intent);
                Log.d(TAG, "Navigating to RecipeContent to select recipe for " + mealType);
            } else {
                Map<String, String> recipeDetails = db.getRecipeDetails(recipeName);

                Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
                intent.putExtra("RECIPE_NAME", recipeName);
                intent.putExtra("diet_preference", dietPref);
                intent.putExtra("meal_type", mealType);
                intent.putExtra("IMAGE", recipeDetails.get("image"));
                intent.putExtra("INGREDIENTS", recipeDetails.get("ingredients"));
                intent.putExtra("DIRECTIONS", recipeDetails.get("directions"));
                intent.putExtra("CALORIES", recipeDetails.get("calories"));
                intent.putExtra("PROTEINS", recipeDetails.get("proteins"));
                intent.putExtra("CARBS", recipeDetails.get("carbs"));
                startActivity(intent);
                Log.d(TAG, "Navigated to RecipeDetailActivity for recipe: " + recipeName + ", mealType=" + mealType);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting RecipeDetailActivity or RecipeContent: ", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            username = prefs.getString("username", "");
            if (username == null || username.isEmpty()) {
                username = db != null ? db.getUsername(email) : "User";
                if (username != null && !username.isEmpty()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.apply();
                    Log.d(TAG, "onResume: Reloaded username from database: " + username);
                } else {
                    username = "User";
                    Log.w(TAG, "onResume: No username found for email: " + email);
                }
            }
            usernameTextView.setText("Welcome, " + username + "!");

            dietPref = db != null ? db.getDietPreference(email) : dietPref;
            if (dietPref == null || dietPref.isEmpty()) {
                dietPref = "Weight Loss";
                Log.w(TAG, "onResume: No diet preference found, using default: Weight Loss");
            }
            loadMealPlans();
            loadFollowedChallenges();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: ", e);
            Toast.makeText(requireContext(), "Error refreshing meals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (db != null) {
                db.close();
                Log.d(TAG, "Database closed");
            }
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(challengeUpdateReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Error closing database or unregistering receiver: ", e);
        }
    }
}