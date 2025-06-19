package com.example.dietcraft;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String TAG = "RecipeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        try {
            ImageView recipeImage = findViewById(R.id.recipe_detail_image);
            TextView recipeTitle = findViewById(R.id.recipe_title);
            TextView ingredients = findViewById(R.id.recipe_ingredients);
            TextView directions = findViewById(R.id.recipe_directions);
            TextView calories = findViewById(R.id.recipe_calories);
            TextView proteins = findViewById(R.id.recipe_proteins);
            TextView carbs = findViewById(R.id.recipe_carbs);
            ImageButton backButton = findViewById(R.id.recipe_detail_back_button);

            // Retrieve data from Intent
            String recipeName = getIntent().getStringExtra("RECIPE_NAME");
            String dietPref = getIntent().getStringExtra("diet_preference");
            String mealType = getIntent().getStringExtra("meal_type");
            String imageName = getIntent().getStringExtra("IMAGE");
            String ingredientsText = getIntent().getStringExtra("INGREDIENTS");
            String directionsText = getIntent().getStringExtra("DIRECTIONS");
            String caloriesText = getIntent().getStringExtra("CALORIES");
            String proteinsText = getIntent().getStringExtra("PROTEINS");
            String carbsText = getIntent().getStringExtra("CARBS");

            if (recipeName == null || dietPref == null || mealType == null) {
                Log.e(TAG, "Missing intent extras: recipeName=" + recipeName + ", dietPref=" + dietPref + ", mealType=" + mealType);
                finish();
                return;
            }

            // Set recipe image
            if (imageName != null && !imageName.isEmpty()) {
                int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                if (resId != 0) {
                    recipeImage.setImageResource(resId);
                } else {
                    recipeImage.setImageResource(R.drawable.recipe1); // Fallback image
                    Log.w(TAG, "Image resource not found for: " + imageName);
                }
            } else {
                recipeImage.setImageResource(R.drawable.recipe1); // Fallback image
                Log.w(TAG, "No image name found for recipe: " + recipeName);
            }

            // Set recipe title
            recipeTitle.setText(recipeName);

            // Format and set ingredients
            ingredients.setText("Ingredients:\n" + formatText(ingredientsText != null ? ingredientsText : "Not available"));

            // Format and set directions
            directions.setText("Directions:\n" + formatText(directionsText != null ? directionsText : "Not available"));

            // Set nutritional information
            calories.setText("Calories: " + (caloriesText != null ? caloriesText : "N/A"));
            proteins.setText("Proteins: " + (proteinsText != null ? proteinsText : "N/A"));
            carbs.setText("Carbohydrates: " + (carbsText != null ? carbsText : "N/A"));

            // Back button functionality
            backButton.setOnClickListener(v -> finish());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            finish();
        }
    }

    // Helper method to format text for better readability
    private String formatText(String text) {
        // Split by commas and add bullet points for clarity
        String[] items = text.split(",");
        StringBuilder formatted = new StringBuilder();
        for (String item : items) {
            formatted.append("• ").append(item.trim()).append("\n");
        }
        return formatted.toString();
    }
}