package com.example.dietcraft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class AnalysisActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisActivity";
    private DatabaseHelper db;
    private String email;
    private TextView bmrTextView, totalCaloriesTextView, totalCarbsTextView, totalProteinsTextView;
    private TextView weightChangeTextView, monthlyTrendTextView, calorieBudgetTextView;
    private TextView breakfastCaloriesTextView, lunchCaloriesTextView, dinnerCaloriesTextView;
    private Button dailyButton, monthlyButton;
    private LineChart lineChart;
    private PieChart remainingCaloriesPieChart;
    private float height, weight;
    private int age;
    private String gender, dietPref;
    private Calendar calendar;
    private float bmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        db = new DatabaseHelper(this);
        email = getIntent().getStringExtra("email");
        dietPref = getIntent().getStringExtra("diet_preference");
        age = getIntent().getIntExtra("age", 25);
        gender = getIntent().getStringExtra("gender");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        email = prefs.getString("email", email).toLowerCase();
        float[] heightWeight = db.getHeightAndWeight(email);
        height = heightWeight[0];
        weight = heightWeight[1];

        initializeViews();
        updateAnalysis("daily");

        dailyButton.setOnClickListener(v -> updateAnalysis("daily"));
        monthlyButton.setOnClickListener(v -> updateAnalysis("monthly"));
    }

    private void initializeViews() {
        bmrTextView = findViewById(R.id.bmr_text_view);
        totalCaloriesTextView = findViewById(R.id.total_calories_text_view);
        totalCarbsTextView = findViewById(R.id.total_carbs_text_view);
        totalProteinsTextView = findViewById(R.id.total_proteins_text_view);
        weightChangeTextView = findViewById(R.id.weight_change_text_view);
        monthlyTrendTextView = findViewById(R.id.monthly_trend_text_view);
        dailyButton = findViewById(R.id.daily_button);
        monthlyButton = findViewById(R.id.monthly_button);
        lineChart = findViewById(R.id.line_chart);
        calorieBudgetTextView = findViewById(R.id.calorie_budget_text_view);
        breakfastCaloriesTextView = findViewById(R.id.breakfast_calories_text_view);
        lunchCaloriesTextView = findViewById(R.id.lunch_calories_text_view);
        dinnerCaloriesTextView = findViewById(R.id.dinner_calories_text_view);
        remainingCaloriesPieChart = findViewById(R.id.remaining_calories_pie_chart);
    }

    private void updateAnalysis(String mode) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Error: User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        bmr = calculateBMR(weight, height, age, gender);
        bmrTextView.setText(String.format("BMR: %.0f kcal", bmr));

        if (mode.equals("daily")) {
            float[] dailyNutrients = calculateDailyNutrients(currentDate);
            updateNutrientDisplay(dailyNutrients);
            String[] meals = {"Breakfast", "Lunch", "Dinner"};
            float[] mealCalories = new float[3];
            for (int i = 0; i < meals.length; i++) {
                String meal = db.getMeal(email, currentDate, meals[i]);
                if (meal != null && !meal.equals("Not set")) {
                    Map<String, String> recipeDetails = db.getRecipeDetails(meal);
                    mealCalories[i] = parseNutrientValue(recipeDetails.getOrDefault("calories", "0"));
                }
            }
            breakfastCaloriesTextView.setText(String.format("Breakfast: %.0f kcal", mealCalories[0]));
            lunchCaloriesTextView.setText(String.format("Lunch: %.0f kcal", mealCalories[1]));
            dinnerCaloriesTextView.setText(String.format("Dinner: %.0f kcal", mealCalories[2]));
            updateWeightChange(bmr, dailyNutrients[0], 1);
            setupLineChart(currentDate, dailyNutrients[0], 1);
            setupPieChart(dailyNutrients[0]);
        } else if (mode.equals("monthly")) {
            float[] monthlyNutrients = calculateMonthlyNutrients();
            updateNutrientDisplay(monthlyNutrients);
            updateWeightChange(bmr, monthlyNutrients[0], 30);
            setupLineChart(currentDate, monthlyNutrients[0], 30);
            setupPieChart(monthlyNutrients[0]);
            breakfastCaloriesTextView.setText("");
            lunchCaloriesTextView.setText("");
            dinnerCaloriesTextView.setText("");
        }

        calorieBudgetTextView.setText("Calorie Budget: 1900 kcal");
    }

    private void setupPieChart(float totalCalories) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        float calorieBudget = 1900f;
        float remainingCalories = calorieBudget - totalCalories;

        // Ensure non-negative values
        if (remainingCalories < 0) remainingCalories = 0;
        if (totalCalories < 0) totalCalories = 0;

        entries.add(new PieEntry(totalCalories, "Consumed"));
        entries.add(new PieEntry(remainingCalories, "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{ColorTemplate.rgb("#FF5722"), ColorTemplate.rgb("#4CAF50")});
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(ContextCompat.getColor(this, android.R.color.white));
        dataSet.setValueFormatter(new PercentFormatter(remainingCaloriesPieChart));

        PieData data = new PieData(dataSet);
        remainingCaloriesPieChart.setData(data);

        // Customize chart appearance
        remainingCaloriesPieChart.setUsePercentValues(true);
        remainingCaloriesPieChart.getDescription().setEnabled(false);
        remainingCaloriesPieChart.setCenterText(String.format("%.0f kcal\nLeft", remainingCalories));
        remainingCaloriesPieChart.setCenterTextSize(14f);
        remainingCaloriesPieChart.setCenterTextColor(ContextCompat.getColor(this, android.R.color.black));
        remainingCaloriesPieChart.setDrawHoleEnabled(true);
        remainingCaloriesPieChart.setHoleColor(ContextCompat.getColor(this, android.R.color.white));
        remainingCaloriesPieChart.setTransparentCircleColor(ContextCompat.getColor(this, android.R.color.white));
        remainingCaloriesPieChart.setTransparentCircleAlpha(110);
        remainingCaloriesPieChart.setHoleRadius(40f);
        remainingCaloriesPieChart.setTransparentCircleRadius(45f);
        remainingCaloriesPieChart.setDrawCenterText(true);
        remainingCaloriesPieChart.getLegend().setEnabled(true);
        remainingCaloriesPieChart.getLegend().setTextColor(ContextCompat.getColor(this, android.R.color.black));
        remainingCaloriesPieChart.getLegend().setTextSize(10f);
        remainingCaloriesPieChart.invalidate();
    }

    private float parseNutrientValue(String value) {
        try {
            String numericValue = value.replaceAll("[^0-9.]", "");
            return Float.parseFloat(numericValue);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing nutrient value: " + value, e);
            return 0f;
        }
    }

    private float[] calculateDailyNutrients(String date) {
        float[] nutrients = new float[]{0f, 0f, 0f}; // [calories, carbs, proteins]
        String[] meals = {"Breakfast", "Lunch", "Dinner"};

        for (String mealType : meals) {
            String meal = db.getMeal(email, date, mealType);
            if (meal != null && !meal.equals("Not set")) {
                Map<String, String> recipeDetails = db.getRecipeDetails(meal);
                if (recipeDetails != null) {
                    nutrients[0] += parseNutrientValue(recipeDetails.getOrDefault("calories", "0"));
                    nutrients[1] += parseNutrientValue(recipeDetails.getOrDefault("carbs", "0"));
                    nutrients[2] += parseNutrientValue(recipeDetails.getOrDefault("proteins", "0"));
                }
            }
        }
        Log.d(TAG, String.format("Daily nutrients for %s: Calories=%.0f, Carbs=%.0f, Proteins=%.0f", date, nutrients[0], nutrients[1], nutrients[2]));
        return nutrients;
    }

    private float[] calculateMonthlyNutrients() {
        float[] nutrients = new float[]{0f, 0f, 0f}; // [calories, carbs, proteins]
        calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            String date = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(calendar.getTime());
            float[] dailyNutrients = calculateDailyNutrients(date);
            nutrients[0] += dailyNutrients[0];
            nutrients[1] += dailyNutrients[1];
            nutrients[2] += dailyNutrients[2];
            calendar.add(Calendar.DAY_OF_MONTH, i);
        }
        Log.d(TAG, String.format("Monthly nutrients: Calories=%.0f, Carbs=%.0f, Proteins=%.0f", nutrients[0], nutrients[1], nutrients[2]));
        return nutrients;
    }

    private float calculateBMR(float weight, float height, int age, String gender) {
        return 10 * weight + 6.25f * height - 5 * age + (gender.equals("male") ? 5 : -161);
    }

    private void updateNutrientDisplay(float[] nutrients) {
        totalCaloriesTextView.setText(String.format("Total Calories: %.0f kcal", nutrients[0]));
        totalCarbsTextView.setText(String.format("Total Carbs: %.0f g", nutrients[1]));
        totalProteinsTextView.setText(String.format("Total Proteins: %.0f g", nutrients[2]));
    }

    private void updateWeightChange(float bmr, float totalCalories, int days) {
        float dailyChange = totalCalories - bmr;
        float monthlyChange = (dailyChange * days) / 7700; // 7700 kcal ≈ 1 kg fat
        weightChangeTextView.setText(String.format("Weight Change (%d days): %.1f kg", days, monthlyChange));
        String trend = getTrend(monthlyChange, totalCalories, weight);
        monthlyTrendTextView.setText("Monthly Trend: " + trend);
    }

    private String getTrend(float monthlyChange, float totalCalories, float weight) {
        if (monthlyChange > 0.1) return "Weight Gain";
        else if (monthlyChange < -0.1) return "Weight Loss";
        else if (totalCalories > bmr && totalCalories > 1.6 * weight) return "Muscle Gain";
        return "Stable";
    }

    private void setupLineChart(String date, float calories, int days) {
        ArrayList<Entry> entries = new ArrayList<>();
        calendar = Calendar.getInstance();
        entries.add(new Entry(0, calories));
        for (int i = 1; i < days; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String pastDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(calendar.getTime());
            float[] pastNutrients = calculateDailyNutrients(pastDate);
            entries.add(new Entry(i, pastNutrients[0]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Calories Over Time");
        dataSet.setColor(ContextCompat.getColor(this, R.color.teal_700));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.teal_700));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize chart appearance
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.black));
        xAxis.setTextSize(10f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.gray));
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.black));
        leftAxis.setTextSize(10f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.black));
        lineChart.getLegend().setTextSize(12f);

        lineChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}