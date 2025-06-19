package com.example.dietcraft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeContent extends AppCompatActivity {

    private String mealType;
    private String dietPref;
    private String email;
    private String username;
    private String selectedDate; // Field for selected date
    private DatabaseHelper db;
    private LinearLayout recipeList;
    private static final String TAG = "RecipeContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_content);

        try {
            db = new DatabaseHelper(this);
            mealType = getIntent().getStringExtra("meal_type");
            dietPref = getIntent().getStringExtra("diet_preference");
            email = getIntent().getStringExtra("email");
            username = getIntent().getStringExtra("username");
            selectedDate = getIntent().getStringExtra("selected_date"); // Receive selected date
            email = email != null ? email.toLowerCase() : "";

            Log.d(TAG, "Received: meal_type=" + mealType + ", diet=" + dietPref + ", email=" + email + ", username=" + username + ", date=" + selectedDate);

            if (email.isEmpty() || dietPref == null || mealType == null) {
//                Toast.makeText(this, "Error: Missing user data.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Missing data: email=" + email + ", dietPref=" + dietPref + ", mealType=" + mealType);
                finish();
                return;
            }

            // If no selected date, use current date as fallback
            if (selectedDate == null) {
                selectedDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
                Log.w(TAG, "No selected date provided, using current date: " + selectedDate);
            }

            ImageButton backButton = findViewById(R.id.recipecontentbackButton);
            backButton.setOnClickListener(v -> finish());

            TextView title = findViewById(R.id.titleTextView);
            title.setText(mealType + " Recipes");

            recipeList = findViewById(R.id.recipe_list);
            loadRecipes();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }



    private void loadRecipes() {
        List<Map<String, String>> recipes;
        try {
            recipes = db.getRecipes(dietPref, mealType);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching recipes: ", e);
//            Toast.makeText(this, "Error loading recipes.", Toast.LENGTH_SHORT).show();
            return;
        }

        recipeList.removeAllViews();
        Log.d(TAG, "Found " + recipes.size() + " recipes for diet=" + dietPref + ", meal=" + mealType);

        if (recipes.isEmpty()) {
//            Toast.makeText(this, "No " + mealType + " recipes for " + dietPref, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No recipes found for diet=" + dietPref + ", meal=" + mealType);
            TextView noRecipes = new TextView(this);
            noRecipes.setText("No recipes available for " + mealType);
            noRecipes.setPadding(16, 16, 16, 16);
            recipeList.addView(noRecipes);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map<String, String> recipe : recipes) {
            CardView card = (CardView) inflater.inflate(R.layout.recipe_card, recipeList, false);
            ImageView image = card.findViewById(R.id.recipe_image);
            TextView name = card.findViewById(R.id.recipe_name);
            TextView desc = card.findViewById(R.id.recipe_desc);
            Button addButton = card.findViewById(R.id.add_button);

            // Extract recipe name using column name
            final String recipeName = recipe.get("recipe_name") != null ? recipe.get("recipe_name") : "Unknown Recipe";
            if (recipe.get("recipe_name") == null) {
                Log.w(TAG, "Recipe name is null for recipe: " + recipe);
            }

            // Set image (assuming image contains a drawable resource name)
            final String imageName = recipe.get("image");
            if (imageName != null && !imageName.isEmpty()) {
                int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                if (imageResId != 0) {
                    image.setImageResource(imageResId);
                } else {
                    image.setImageResource(R.drawable.recipe1); // Fallback image
                    Log.w(TAG, "Invalid image resource: " + imageName);
                }
            } else {
                image.setImageResource(R.drawable.recipe1); // Fallback image
                Log.w(TAG, "No image provided for recipe: " + recipeName);
            }

            // Set name and description
            name.setText(recipeName);
            final String ingredients = recipe.get("ingredients");
            final String description = ingredients != null && !ingredients.isEmpty()
                    ? "Ingredients: " + ingredients
                    : "A delicious " + mealType.toLowerCase() + " option for your " + dietPref.toLowerCase() + " plan.";
            desc.setText(description);

            // Copy variables to final versions for lambda expressions
            final String finalDietPref = dietPref;
            final String finalMealType = mealType;
            final String finalSelectedDate = selectedDate;

            addButton.setOnClickListener(v -> {
                long result = db.insertMeal(email, finalSelectedDate, finalMealType, recipeName);
                boolean success = result != -1;
//                Toast.makeText(this, success ? "Added to your " + finalMealType + " plan for " + finalSelectedDate + "!" : "Failed to add meal.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Add recipe: " + recipeName + " to " + finalMealType + ", date=" + finalSelectedDate + ", success=" + success);
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                }
            });

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, RecipeDetailActivity.class);
                intent.putExtra("RECIPE_NAME", recipeName);
                intent.putExtra("diet_preference", finalDietPref);
                intent.putExtra("meal_type", finalMealType);
                // Pass additional recipe details to RecipeDetailActivity
                intent.putExtra("INGREDIENTS", recipe.get("ingredients"));
                intent.putExtra("DIRECTIONS", recipe.get("directions"));
                intent.putExtra("CALORIES", recipe.get("calories"));
                intent.putExtra("PROTEINS", recipe.get("proteins"));
                intent.putExtra("CARBS", recipe.get("carbs"));
                intent.putExtra("IMAGE", recipe.get("image"));
                startActivity(intent);
                Log.d(TAG, "Navigated to RecipeDetailActivity for recipe: " + recipeName);
            });

            recipeList.addView(card);
        }
        Log.d(TAG, "Loaded " + recipes.size() + " recipes for " + mealType);
    }



}


