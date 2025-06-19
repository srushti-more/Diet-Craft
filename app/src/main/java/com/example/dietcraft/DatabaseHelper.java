package com.example.dietcraft;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context dbContext;
    private static final String DATABASE_NAME = "DietCraft.db";
    private static final int DATABASE_VERSION = 13;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String TABLE_REMINDERS = "reminders";
    private static final String TABLE_MEALS = "meals";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_CHALLENGES = "challenges";
    private static final String TAG = "DatabaseHelper";

    // Users table columns
    private static final String COL_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_HEIGHT = "height";
    private static final String COL_WEIGHT = "weight";
    private static final String COL_DIET_PREF = "diet_preference";
    private static final String COL_AGE = "age";
    private static final String COL_GENDER = "gender";
    private static final String COL_PROFILE_IMAGE = "profile_image";
    public static final String COL_IMAGE = "image";

    // Feedback table columns
    private static final String COL_FEEDBACK_ID = "feedback_id";
    private static final String COL_FEEDBACK = "feedback";

    // Reminders table columns
    private static final String COL_REMINDER_ID = "reminder_id";
    private static final String COL_REMINDER_TIME = "reminder_time";
    private static final String COL_REMINDER_TYPE = "reminder_type";

    // Meals table columns
    private static final String COL_MEAL_ID = "meal_id";
    private static final String COL_MEAL_DATE = "meal_date";
    private static final String COL_MEAL_TYPE = "meal_type";
    private static final String COL_MEAL_DETAILS = "meal_details";

    // Recipes table columns
    private static final String COL_RECIPE_ID = "recipe_id";
    private static final String COL_RECIPE_NAME = "recipe_name";
    private static final String COL_DIET_TYPE = "diet_type";
    private static final String COL_RECIPE_TYPE = "recipe_type";
    private static final String COL_INGREDIENTS = "ingredients";
    private static final String COL_DIRECTIONS = "directions";
    private static final String COL_CALORIES = "calories";
    private static final String COL_PROTEINS = "proteins";
    private static final String COL_CARBS = "carbs";

    private static final String TABLE_USER_CHALLENGES = "user_challenges";



    // Challenges table columns
    private static final String COL_CHALLENGE_ID = "challenge_id";
    private static final String COL_CHALLENGE_NAME = "challenge_name";
    private static final String COL_CHALLENGE_DESC = "challenge_description";
    private static final String COL_CHALLENGE_DIET_TYPE = "challenge_diet_type";


    // Health Tips table columns
    private static final String TABLE_HEALTH_TIPS = "health_tips";
    private static final String COL_TIP_ID = "tip_id";
    private static final String COL_CATEGORY = "category";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";



    private final String[] healthTips = {
            "FOOD , STAY HYDRATED , Drink at least 8 glasses of water daily to keep your body hydrated and support digestion.",
            "FOOD , EAT WHOLE FOODS , Incorporate whole grains, fruits, and vegetables into your meals for better nutrition.",
            "NUTRITION , BALANCE MACRONUTRIENTS , Ensure your diet includes a balance of carbohydrates, proteins, and healthy fats.",
            "NUTRITION , LIMIT PROCESSED FOODS , Reduce consumption of processed foods high in sugar and unhealthy fats.",
            "HEALTH , EXERCISE REGULARLY , Aim for at least 30 minutes of moderate exercise 5 days a week to boost overall health.",
            "HEALTH , GET ENOUGH SLEEP , Sleep 7-9 hours per night to support physical and mental well-being.",
            "GENERAL WELLNESS , PRACTICE MINDFULNESS , Spend 10 minutes daily meditating to reduce stress and improve focus.",
            "GENERAL WELLNESS , STAY CONSISTENT , Maintain a consistent routine for meals, exercise, and sleep to support long-term health.",
            "FOOD , PORTION CONTROL , Use smaller plates to help manage portion sizes and avoid overeating.",
            "NUTRITION , INCREASE FIBER INTAKE , Consume 25-30g of fiber daily to support digestion and feel fuller longer.",
            "HEALTH , LIMIT SCREEN TIME , Reduce screen time before bed to improve sleep quality.",
            "GENERAL WELLNESS , STAY SOCIAL , Engage in social activities to boost mental health and reduce stress."
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbContext = context;
        Log.d(TAG, "DatabaseHelper initialized with version: " + DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_HEIGHT + " REAL DEFAULT 0, " +
                COL_WEIGHT + " REAL DEFAULT 0, " +
                COL_DIET_PREF + " TEXT, " +
                COL_AGE + " INTEGER, " +
                COL_GENDER + " TEXT, " +
                COL_PROFILE_IMAGE + " BLOB)";
        db.execSQL(createUsersTable);
        Log.d(TAG, "Created users table");

        String createFeedbackTable = "CREATE TABLE " + TABLE_FEEDBACK + " (" +
                COL_FEEDBACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_FEEDBACK + " TEXT)";
        db.execSQL(createFeedbackTable);
        Log.d(TAG, "Created feedback table");

        String createRemindersTable = "CREATE TABLE " + TABLE_REMINDERS + " (" +
                COL_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_REMINDER_TIME + " TEXT, " +
                COL_REMINDER_TYPE + " TEXT)";
        db.execSQL(createRemindersTable);
        Log.d(TAG, "Created reminders table");

        String createMealsTable = "CREATE TABLE " + TABLE_MEALS + " (" +
                COL_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_MEAL_DATE + " TEXT, " +
                COL_MEAL_TYPE + " TEXT, " +
                COL_MEAL_DETAILS + " TEXT)";
        db.execSQL(createMealsTable);
        Log.d(TAG, "Created meals table");

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_DIET_TYPE + " TEXT, " +
                COL_RECIPE_TYPE + " TEXT, " +
                COL_INGREDIENTS + " TEXT, " +
                COL_DIRECTIONS + " TEXT, " +
                COL_CALORIES + " TEXT, " +
                COL_PROTEINS + " TEXT, " +
                COL_CARBS + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createRecipesTable);
        Log.d(TAG, "Created recipes table");

        String createChallengesTable = "CREATE TABLE " + TABLE_CHALLENGES + " (" +
                COL_CHALLENGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CHALLENGE_NAME + " TEXT, " +
                COL_CHALLENGE_DESC + " TEXT, " +
                COL_IMAGE + " TEXT, " +
                COL_CHALLENGE_DIET_TYPE + " TEXT)";
        db.execSQL(createChallengesTable);
        Log.d(TAG, "Created challenges table");

        // Health Tips table
        String createHealthTipsTable = "CREATE TABLE " + TABLE_HEALTH_TIPS + " (" +
                COL_TIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT NOT NULL)";
        db.execSQL(createHealthTipsTable);
        Log.d(TAG, "Created health_tips table");

        // Create user_challenges table
        String CREATE_USER_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_USER_CHALLENGES + "("
                + COL_EMAIL + " TEXT,"
                + COL_CHALLENGE_NAME + " TEXT,"
                + COL_DIET_PREF + " TEXT,"
                + "PRIMARY KEY (" + COL_EMAIL + ", " + COL_CHALLENGE_NAME + ")"
                + ")";
        db.execSQL(CREATE_USER_CHALLENGES_TABLE);
        Log.d(TAG, "Database created with all tables");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Weight Loss Recipes
        Map<String, String[]> wlRecipes = new HashMap<>();
        wlRecipes.put("Breakfast", new String[]{
                "Poha with Vegetables|Flattened rice, onions, peas, turmeric|Soak poha, sauté vegetables with spices, mix|150 kcal|4g|25g|poha_vegetables",
                "Moong Dal Chilla|Moong dal, onions, green chilies|Soak dal, blend to batter, cook with veggies|120 kcal|6g|15g|moong_dal_chilla",
                "Upma|Rava, mustard seeds, mixed vegetables|Roast rava, cook with sautéed veggies|140 kcal|3g|20g|upma",
                "Oats Idli|Oats, curd, carrots, mustard seeds|Grind oats, mix with curd, steam with veggies|130 kcal|4g|18g|oats_idli",
                "Besan Cheela|Gram flour, tomatoes, onions|Mix besan with water, add veggies, cook as pancake|150 kcal|5g|20g|besan_cheela",
                "Ragi Dosa|Ragi flour, curd, green chilies|Mix ragi with curd, ferment, cook as dosa|140 kcal|4g|22g|ragi_dosa",
                "Vegetable Dalia|Broken wheat, mixed vegetables, cumin|Cook dalia with veggies and spices|130 kcal|4g|20g|vegetable_dalia",
                "Sprouts Salad|Moong sprouts, cucumber, lemon juice|Mix sprouts with veggies, add lemon|100 kcal|5g|15g|sprouts_salad",
                "Methi Thepla|Whole wheat flour, fenugreek leaves|Knead dough with methi, cook as flatbread|120 kcal|3g|18g|methi_thepla",
                "Suji Pancake|Semolina, curd, mixed vegetables|Mix suji with curd, add veggies, cook|140 kcal|4g|20g|suji_pancake",
                "Greek Yogurt with Berries|Fresh berries, Greek yogurt, honey|Mix berries with yogurt, drizzle with honey|150 kcal|5g|20g|greek_yogurt_berries",
                "Avocado Toast|Whole grain bread, avocado, lemon juice|Toast bread, mash avocado, add lemon juice|200 kcal|4g|25g|avocado_toast",
                "Oatmeal with Apple|Oats, apple, cinnamon|Cook oats, top with apple and cinnamon|160 kcal|4g|28g|oatmeal_apple",
                "Egg White Scramble|Egg whites, spinach, tomatoes|Scramble egg whites with veggies|120 kcal|10g|5g|egg_white_scramble",
                "Chia Seed Pudding|Chia seeds, almond milk, berries|Soak chia in milk, top with berries|140 kcal|5g|15g|chia_seed_pudding",
                "Smoothie Bowl|Banana, spinach, almond milk|Blend ingredients, top with seeds|150 kcal|4g|20g|smoothie_bowl",
                "Whole Grain Muffin|Whole wheat flour, blueberries|Bake muffins with berries|130 kcal|3g|22g|whole_grain_muffin",
                "Cottage Cheese with Pineapple|Cottage cheese, pineapple chunks|Mix cheese with pineapple|120 kcal|8g|10g|cottage_cheese_pineapple",
                "Almond Butter Toast|Whole grain bread, almond butter|Spread almond butter on toast|180 kcal|5g|20g|almond_butter_toast",
                "Quinoa Porridge|Quinoa, almond milk, nuts|Cook quinoa with milk, top with nuts|160 kcal|6g|22g|quinoa_porridge",
                "Veggie Omelette|Egg whites, mushrooms, peppers|Cook omelette with veggies|130 kcal|9g|6g|veggie_omelette",
                "Green Smoothie|Kale, apple, yogurt|Blend ingredients until smooth|140 kcal|5g|20g|green_smoothie",
                "Buckwheat Pancakes|Buckwheat flour, banana|Mix batter, cook pancakes|150 kcal|4g|25g|buckwheat_pancakes",
                "Fruit Salad|Mixed fruits, lemon juice|Chop fruits, toss with lemon|100 kcal|2g|20g|fruit_salad",
                "Barley Porridge|Barley, almond milk, berries|Cook barley with milk, top with berries|140 kcal|4g|24g|barley_porridge",
                "Tofu Scramble|Tofu, turmeric, spinach|Crumble tofu, cook with spices and veggies|120 kcal|8g|10g|tofu_scramble",
                "Cornmeal Porridge|Cornmeal, almond milk, cinnamon|Cook cornmeal with milk, add cinnamon|130 kcal|3g|22g|cornmeal_porridge",
                "Zucchini Bread|Zucchini, whole wheat flour|Bake bread with grated zucchini|140 kcal|4g|20g|zucchini_bread",
                "Millet Porridge|Millet, almond milk, nuts|Cook millet with milk, top with nuts|150 kcal|5g|22g|millet_porridge",
                "Flaxseed Smoothie|Flaxseeds, banana, yogurt|Blend ingredients until smooth|140 kcal|5g|20g|flaxseed_smoothie"
        });
        wlRecipes.put("Lunch", new String[]{
                "Vegetable Khichdi|Rice, moong dal, mixed vegetables|Cook rice and dal with veggies|200 kcal|8g|30g|vegetable_khichdi",
                "Palak Dal|Spinach, toor dal, spices|Cook dal with spinach and spices|180 kcal|9g|25g|palak_dal",
                "Rajma Salad|Kidney beans, cucumber, lemon|Mix boiled rajma with veggies|170 kcal|8g|20g|rajma_salad",
                "Vegetable Pulao|Brown rice, mixed vegetables|Cook rice with veggies and spices|190 kcal|5g|30g|vegetable_pulao",
                "Lauki Sabzi|Bottle gourd, tomatoes, spices|Cook lauki with spices|120 kcal|3g|15g|lauki_sabzi",
                "Chickpea Salad|Chickpeas, onions, lemon|Mix boiled chickpeas with veggies|180 kcal|7g|25g|chickpea_salad",
                "Tofu Bhurji|Tofu, onions, tomatoes|Crumble tofu, cook with spices|150 kcal|10g|10g|tofu_bhurji",
                "Bhindi Masala|Okra, tomatoes, spices|Cook bhindi with spices|140 kcal|4g|15g|bhindi_masala",
                "Methi Matar|Fenugreek leaves, peas|Cook methi with peas and spices|130 kcal|5g|15g|methi_matar",
                "Cabbage Sabzi|Cabbage, mustard seeds|Cook cabbage with spices|120 kcal|3g|14g|cabbage_sabzi",
                "Grilled Chicken Salad|Chicken breast, mixed greens, vinaigrette|Grill chicken, toss with greens and dressing|250 kcal|30g|10g|grilled_chicken_salad",
                "Quinoa Veggie Bowl|Quinoa, roasted vegetables, olive oil|Cook quinoa, roast veggies, combine|220 kcal|8g|35g|quinoa_veggie_bowl",
                "Turkey Wrap|Whole wheat tortilla, turkey, lettuce|Wrap turkey and veggies in tortilla|200 kcal|15g|20g|turkey_wrap",
                "Lentil Soup|Lentils, carrots, celery|Simmer lentils with veggies|180 kcal|10g|25g|lentil_soup",
                "Tuna Salad|Tuna, celery, Greek yogurt|Mix tuna with yogurt and celery|190 kcal|20g|5g|tuna_salad",
                "Veggie Stir-Fry|Broccoli, peppers, soy sauce|Stir-fry veggies with soy sauce|150 kcal|5g|20g|veggie_stir_fry",
                "Black Bean Salad|Black beans, corn, lime|Mix beans with corn and lime|170 kcal|7g|25g|black_bean_salad",
                "Grilled Shrimp Salad|Shrimp, mixed greens, lemon|Grill shrimp, toss with greens|200 kcal|18g|10g|grilled_shrimp_salad",
                "Hummus Veggie Wrap|Hummus, carrots, cucumber|Spread hummus, add veggies, wrap|180 kcal|6g|25g|hummus_veggie_wrap",
                "Cauliflower Rice Bowl|Cauliflower rice, avocado|Cook cauliflower rice, top with avocado|160 kcal|5g|15g|cauliflower_rice_bowl",
                "Zucchini Noodles|Zucchini, tomato sauce|Spiralize zucchini, top with sauce|120 kcal|3g|15g|zucchini_noodles",
                "Baked Cod|Cod fillet, lemon, herbs|Bake cod with lemon and herbs|180 kcal|20g|5g|baked_cod",
                "Spaghetti Squash|Spaghetti squash, marinara|Bake squash, top with sauce|140 kcal|3g|20g|spaghetti_squash",
                "Kale Salad|Kale, lemon, olive oil|Massage kale with lemon and oil|150 kcal|4g|15g|kale_salad",
                "Eggplant Curry|Eggplant, tomatoes, spices|Cook eggplant with spices|130 kcal|3g|18g|eggplant_curry",
                "Mushroom Soup|Mushrooms, vegetable broth|Simmer mushrooms in broth|120 kcal|4g|15g|mushroom_soup",
                "Arugula Salad|Arugula, walnuts, balsamic|Mix arugula with nuts and dressing|140 kcal|4g|10g|arugula_salad",
                "Grilled Tofu|Tofu, soy sauce, ginger|Marinate tofu, grill|150 kcal|10g|8g|grilled_tofu",
                "Barley Salad|Barley, cucumber, mint|Mix cooked barley with veggies|160 kcal|5g|25g|barley_salad",
                "Stuffed Peppers|Bell peppers, quinoa, tomatoes|Stuff peppers, bake|170 kcal|6g|25g|stuffed_peppers"
        });
        wlRecipes.put("Dinner", new String[]{
                "Moong Dal Soup|Moong dal, ginger, spices|Cook dal with spices|150 kcal|8g|20g|moong_dal_soup",
                "Vegetable Curry|Mixed vegetables, coconut milk|Cook veggies in coconut milk|170 kcal|5g|20g|vegetable_curry",
                "Tandoori Chicken|Chicken breast, yogurt, spices|Marinate chicken, grill|200 kcal|25g|5g|tandoori_chicken",
                "Palak Paneer|Spinach, paneer, spices|Cook spinach with paneer|180 kcal|10g|10g|palak_paneer",
                "Baingan Bharta|Eggplant, tomatoes, spices|Roast eggplant, cook with spices|140 kcal|4g|15g|baingan_bharta",
                "Chana Masala|Chickpeas, tomatoes, spices|Cook chickpeas with spices|190 kcal|8g|25g|chana_masala",
                "Gobi Matar|Cauliflower, peas, spices|Cook cauliflower with peas|130 kcal|5g|15g|gobi_matar",
                "Dal Tadka|Toor dal, tomatoes, spices|Cook dal with tempering|170 kcal|9g|20g|dal_tadka",
                "Mixed Vegetable Sabzi|Carrots, beans, spices|Cook veggies with spices|120 kcal|4g|15g|mixed_vegetable_sabzi",
                "Fish Curry|Fish, coconut milk, spices|Cook fish in coconut curry|200 kcal|20g|10g|fish_curry",
                "Baked Salmon|Salmon fillet, lemon, herbs|Bake salmon with lemon and herbs|300 kcal|25g|5g|baked_salmon",
                "Grilled Veggie Plate|Zucchini, peppers, eggplant, olive oil|Grill vegetables, drizzle with oil|180 kcal|4g|20g|grilled_veggie_plate",
                "Turkey Meatballs|Ground turkey, herbs, tomato sauce|Bake meatballs, simmer in sauce|220 kcal|20g|10g|turkey_meatballs",
                "Stuffed Zucchini|Zucchini, ground turkey, tomatoes|Stuff zucchini, bake|200 kcal|15g|15g|stuffed_zucchini",
                "Grilled Chicken Breast|Chicken breast, herbs, lemon|Grill chicken with herbs|250 kcal|30g|5g|grilled_chicken_breast",
                "Roasted Brussels Sprouts|Brussels sprouts, olive oil|Roast sprouts with oil|140 kcal|4g|15g|roasted_brussels_sprouts",
                "Baked Cod|Cod fillet, lemon, herbs|Bake cod with lemon|180 kcal|20g|5g|baked_cod",
                "Vegetable Soup|Carrots, celery, broth|Simmer veggies in broth|120 kcal|3g|15g|vegetable_soup",
                "Shrimp Stir-Fry|Shrimp, broccoli, soy sauce|Stir-fry shrimp with veggies|200 kcal|18g|10g|shrimp_stir_fry",
                "Quinoa Stuffed Peppers|Bell peppers, quinoa, herbs|Stuff peppers, bake|190 kcal|6g|25g|quinoa_stuffed_peppers",
                "Grilled Tofu|Tofu, soy sauce, ginger|Marinate tofu, grill|150 kcal|10g|8g|grilled_tofu",
                "Lentil Curry|Lentils, coconut milk, spices|Cook lentils in curry|180 kcal|9g|25g|lentil_curry",
                "Baked Tilapia|Tilapia, lemon, herbs|Bake tilapia with lemon|190 kcal|20g|5g|baked_tilapia",
                "Cauliflower Mash|Cauliflower, garlic, olive oil|Mash cauliflower with garlic|120 kcal|3g|10g|cauliflower_mash",
                "Spinach Soup|Spinach, vegetable broth|Blend spinach with broth|110 kcal|4g|12g|spinach_soup",
                "Grilled Eggplant|Eggplant, olive oil, herbs|Grill eggplant slices|130 kcal|3g|15g|grilled_eggplant",
                "Mushroom Risotto|Arborio rice, mushrooms, broth|Cook rice with mushrooms|200 kcal|5g|30g|mushroom_risotto",
                "Kale Stir-Fry|Kale, garlic, soy sauce|Stir-fry kale with garlic|140 kcal|4g|15g|kale_stir_fry",
                "Baked Chicken|Chicken breast, herbs, lemon|Bake chicken with herbs|220 kcal|25g|5g|baked_chicken",
                "Zucchini Lasagna|Zucchini, tomato sauce, cheese|Layer zucchini with sauce|180 kcal|10g|15g|zucchini_lasagna"
        });

        // Weight Gain Recipes
        Map<String, String[]> wgRecipes = new HashMap<>();
        wgRecipes.put("Breakfast", new String[]{
                "Paratha with Curd|Whole wheat flour, ghee, curd|Cook paratha with ghee, serve with curd|400 kcal|10g|50g|paratha_with_curd",
                "Puri Bhaji|Wheat flour, potatoes, spices|Deep fry puri, cook potato bhaji|450 kcal|8g|60g|puri_bhaji",
                "Aloo Paratha|Wheat flour, potatoes, ghee|Stuff paratha with potatoes, cook with ghee|420 kcal|9g|55g|aloo_paratha",
                "Halwa|Semolina, ghee, sugar|Cook suji with ghee and sugar|400 kcal|6g|50g|halwa",
                "Chole Bhature|Chickpeas, flour, spices|Cook chole, fry bhature|500 kcal|12g|65g|chole_bhature",
                "Paneer Paratha|Wheat flour, paneer, ghee|Stuff paratha with paneer, cook|450 kcal|15g|50g|paneer_paratha",
                "Suji Halwa|Semolina, ghee, nuts|Cook suji with ghee, add nuts|420 kcal|7g|55g|suji_halwa",
                "Lassi|Curd, sugar, cardamom|Blend curd with sugar|350 kcal|8g|45g|lassi",
                "Stuffed Kulcha|Flour, potatoes, ghee|Stuff kulcha, cook with ghee|400 kcal|9g|50g|stuffed_kulcha",
                "Banana Dosa|Rice flour, banana, ghee|Blend banana with batter, cook dosa|380 kcal|6g|55g|banana_dosa",
                "Peanut Butter Banana Smoothie|Banana, peanut butter, milk|Blend ingredients until smooth|400 kcal|12g|50g|peanut_butter_banana_smoothie",
                "Oatmeal with Nuts|Oats, almonds, honey|Cook oats, top with nuts and honey|350 kcal|10g|55g|oatmeal_with_nuts",
                "Pancakes with Syrup|Flour, eggs, maple syrup|Cook pancakes, drizzle with syrup|450 kcal|8g|65g|pancakes_with_syrup",
                "Waffles with Cream|Waffle mix, whipped cream|Cook waffles, top with cream|420 kcal|7g|60g|waffles_with_cream",
                "Bagel with Cream Cheese|Bagel, cream cheese, jam|Spread cream cheese on bagel|400 kcal|10g|55g|bagel_with_cream_cheese",
                "French Toast|Bread, eggs, syrup|Dip bread in egg, cook, add syrup|380 kcal|9g|50g|french_toast",
                "Granola with Milk|Granola, whole milk, nuts|Mix granola with milk and nuts|400 kcal|12g|50g|granola_with_milk",
                "Breakfast Burrito|Tortilla, eggs, cheese|Fill tortilla with eggs and cheese|450 kcal|15g|45g|breakfast_burrito",
                "Muffin with Butter|Muffin, butter, jam|Spread butter and jam on muffin|420 kcal|7g|60g|muffin_with_butter",
                "Smoothie with Oats|Oats, banana, whole milk|Blend ingredients|380 kcal|10g|55g|smoothie_with_oats",
                "Cheese Omelette|Eggs, cheese, butter|Cook omelette with cheese|400 kcal|15g|10g|cheese_omelette",
                "Nut Butter Toast|Bread, almond butter, honey|Spread nut butter, drizzle honey|380 kcal|10g|50g|nut_butter_toast",
                "Milkshake|Whole milk, ice cream, banana|Blend ingredients|450 kcal|12g|60g|milkshake",
                "Croissant with Jam|Croissant, butter, jam|Spread butter and jam on croissant|400 kcal|8g|55g|croissant_with_jam",
                "Rice Pudding|Rice, whole milk, sugar|Cook rice with milk and sugar|380 kcal|8g|50g|rice_pudding",
                "Fruit Smoothie|Banana, mango, whole milk|Blend fruits with milk|400 kcal|10g|55g|fruit_smoothie",
                "Granola Bar|Granola, honey, nuts|Mix and bake granola bars|350 kcal|8g|50g|granola_bar",
                "Egg Muffin|Eggs, cheese, muffin|Cook eggs in muffin|400 kcal|15g|45g|egg_muffin",
                "Sweet Potato Hash|Sweet potato, eggs, butter|Cook hash with eggs|380 kcal|10g|50g|sweet_potato_hash",
                "Chocolate Smoothie|Banana, cocoa, whole milk|Blend ingredients|400 kcal|10g|55g|chocolate_smoothie"
        });
        wgRecipes.put("Lunch", new String[]{
                "Butter Chicken|Chicken, butter, cream|Cook chicken in creamy sauce|600 kcal|30g|20g|butter_chicken",
                "Paneer Butter Masala|Paneer, butter, cream|Cook paneer in creamy sauce|550 kcal|20g|25g|paneer_butter_masala",
                "Dal Makhani|Black lentils, cream, butter|Cook lentils with cream|500 kcal|15g|40g|dal_makhani",
                "Mutton Curry|Mutton, spices, ghee|Cook mutton with spices|650 kcal|35g|20g|mutton_curry",
                "Chicken Biryani|Basmati rice, chicken, ghee|Cook rice with chicken and spices|600 kcal|30g|60g|chicken_biryani",
                "Prawn Masala|Prawns, tomatoes, ghee|Cook prawns with spices|550 kcal|25g|15g|prawn_masala",
                "Rajma with Rice|Kidney beans, rice, ghee|Cook rajma, serve with rice|500 kcal|15g|65g|rajma_with_rice",
                "Palak Paneer with Naan|Spinach, paneer, naan|Cook palak paneer, serve with naan|600 kcal|20g|60g|palak_paneer_with_naan",
                "Chole with Kulcha|Chickpeas, kulcha, ghee|Cook chole, serve with kulcha|550 kcal|15g|65g|chole_with_kulcha",
                "Egg Curry with Rice|Eggs, spices, rice|Cook egg curry, serve with rice|500 kcal|20g|60g|egg_curry_with_rice",
                "Paneer Burger|Paneer patty, bun, cheese|Grill paneer patty, assemble burger|550 kcal|20g|55g|paneer_burger",
                "Mushroom Pasta|Pasta, mushrooms, cream sauce|Cook pasta, mix with mushroom sauce|550 kcal|15g|65g|mushroom_pasta",
                "Mac and Cheese|Macaroni, cheese, cream|Cook pasta, mix with cheese sauce|600 kcal|20g|65g|mac_and_cheese",
                "Chickpea Stuffed Paratha|Chickpeas, wheat flour, ghee|Stuff paratha with chickpeas, cook|550 kcal|15g|60g|chickpea_stuffed_paratha",
                "Cheese Pizza|Pizza dough, cheese, sauce|Bake pizza with cheese|550 kcal|20g|60g|cheese_pizza",
                "Paneer Alfredo|Pasta, paneer, cream sauce|Cook pasta, mix with paneer and sauce|600 kcal|20g|65g|paneer_alfredo",
                "Tofu Cheesesteak|Tofu, cheese, bun|Cook tofu with cheese, serve in bun|550 kcal|20g|50g|tofu_cheesesteak",
                "Chickpea Tacos|Corn tortillas, chickpeas, cheese|Fill tortillas with chickpeas and cheese|500 kcal|15g|50g|chickpea_tacos",
                "Lentil Lasagna|Lasagna noodles, lentils, cheese|Layer noodles with lentils and cheese|550 kcal|20g|55g|lentil_lasagna",
                "Paneer Sub|Paneer, cheese, bun|Fill bun with paneer and cheese|550 kcal|20g|50g|paneer_sub",
                "Fried Paneer|Paneer, flour, oil|Fry paneer with coating|550 kcal|20g|30g|fried_paneer",
                "Creamy Mushroom Pasta|Pasta, mushrooms, cream|Cook pasta, mix with mushroom sauce|550 kcal|15g|65g|creamy_mushroom_pasta",
                "Tofu Stir-Fry|Tofu, rice, soy sauce|Stir-fry tofu, serve with rice|550 kcal|20g|55g|tofu_stir_fry",
                "Cheese Quesadilla|Tortilla, cheese, salsa|Cook quesadilla with cheese|500 kcal|20g|50g|cheese_quesadilla",
                "Paneer Steak|Paneer, butter, herbs|Grill paneer with butter|550 kcal|20g|10g|paneer_steak",
                "Creamy Soup|Potatoes, cream, cheese|Cook soup with cream|500 kcal|15g|50g|creamy_soup",
                "Avocado Sandwich|Avocado, bread, mayo|Assemble sandwich with avocado|550 kcal|10g|50g|avocado_sandwich",
                "Mushroom Pizza|Pizza dough, mushrooms, cheese|Bake pizza with mushrooms|550 kcal|20g|60g|mushroom_pizza",
                "Paneer Parmesan|Paneer, cheese, sauce|Bake paneer with cheese|550 kcal|20g|40g|paneer_parmesan",
                "Lentil Tacos|Corn tortillas, lentils, cheese|Fill tortillas with lentils|500 kcal|15g|50g|lentil_tacos"
        });
        wgRecipes.put("Dinner", new String[]{
                "Korma Chicken|Chicken, cream, nuts|Cook chicken in creamy sauce|600 kcal|30g|20g|korma_chicken",
                "Rogan Josh|Mutton, yogurt, spices|Cook mutton with spices|650 kcal|35g|15g|rogan_josh",
                "Paneer Tikka Masala|Paneer, cream, spices|Cook paneer in creamy sauce|550 kcal|20g|25g|paneer_tikka_masala",
                "Malai Kofta|Paneer balls, cream, spices|Cook kofta in creamy sauce|600 kcal|15g|40g|malai_kofta",
                "Butter Naan with Curry|Naan, butter, chicken curry|Cook curry, serve with naan|650 kcal|25g|70g|butter_naan_with_curry",
                "Fish Fry|Fish, spices, oil|Fry fish with spices|550 kcal|25g|15g|fish_fry",
                "Prawn Biryani|Basmati rice, prawns, ghee|Cook rice with prawns|600 kcal|25g|65g|prawn_biryani",
                "Mutton Biryani|Basmati rice, mutton, ghee|Cook rice with mutton|650 kcal|35g|60g|mutton_biryani",
                "Palak Kofta|Spinach, paneer, cream|Cook kofta in spinach sauce|550 kcal|15g|35g|palak_kofta",
                "Chicken Tikka|Chicken, yogurt, spices|Marinate chicken, grill|600 kcal|30g|10g|chicken_tikka",
                "Paneer with Potatoes|Paneer, potatoes, butter|Cook paneer with potatoes|600 kcal|20g|60g|paneer_with_potatoes",
                "Creamy Mushroom Pasta|Pasta, mushrooms, cream sauce|Cook pasta, combine with sauce|600 kcal|15g|65g|creamy_mushroom_pasta",
                "Lentil Loaf|Lentils, breadcrumbs, sauce|Bake lentil loaf with sauce|550 kcal|20g|50g|lentil_loaf",
                "Chickpea Curry|Chickpeas, cream, bun|Cook chickpeas in curry|550 kcal|15g|55g|chickpea_curry",
                "Baked Ziti|Pasta, cheese, tomato sauce|Bake pasta with cheese|600 kcal|25g|65g|baked_ziti",
                "Fried Tofu|Tofu, flour, oil|Fry tofu with coating|550 kcal|20g|30g|fried_tofu",
                "Lentil Stew|Lentils, potatoes, carrots|Simmer lentils with veggies|550 kcal|20g|50g|lentil_stew",
                "Paneer Burger|Paneer patty, cheese, bun|Grill paneer, assemble burger|600 kcal|20g|50g|paneer_burger",
                "Paneer Pot Pie|Paneer, pastry, cream|Bake pie with paneer|600 kcal|20g|55g|paneer_pot_pie",
                "Chickpea Curry|Chickpeas, cream, spices|Cook chickpeas with cream|550 kcal|15g|50g|chickpea_curry",
                "Paneer Chops|Paneer, butter, herbs|Grill paneer with butter|550 kcal|20g|10g|paneer_chops",
                "Creamy Risotto|Arborio rice, cream, cheese|Cook risotto with cream|550 kcal|15g|65g|creamy_risotto",
                "Lentil Lasagna|Lasagna noodles, lentils, cheese|Layer noodles with lentils|600 kcal|20g|55g|lentil_lasagna",
                "Mushroom Pasta|Pasta, mushrooms, cream|Cook pasta with mushrooms|550 kcal|15g|65g|mushroom_pasta",
                "Avocado Pasta|Pasta, avocado, cream|Cook pasta with avocado|550 kcal|10g|60g|avocado_pasta",
                "Paneer Curry|Paneer, cream, rice|Cook paneer curry, serve with rice|600 kcal|20g|60g|paneer_curry",
                "Tofu Stir-Fry|Tofu, rice, soy sauce|Stir-fry tofu, serve with rice|550 kcal|20g|55g|tofu_stir_fry",
                "Cheese Ravioli|Ravioli, cheese, sauce|Cook ravioli with sauce|600 kcal|20g|65g|cheese_ravioli",
                "Lentil Casserole|Lentils, potatoes, cheese|Bake casserole with lentils|600 kcal|20g|50g|lentil_casserole",
                "Creamy Paneer|Paneer, cream, rice|Cook paneer in cream sauce|600 kcal|20g|50g|creamy_paneer"
        });

        // Muscle Gain Recipes
        Map<String, String[]> mgRecipes = new HashMap<>();
        mgRecipes.put("Breakfast", new String[]{
                "Paneer Bhurji|Paneer, onions, spices|Crumble paneer, cook with spices|300 kcal|20g|10g|paneer_bhurji",
                "Moong Dal Paratha|Moong dal, wheat flour|Stuff paratha with dal, cook|350 kcal|15g|45g|moong_dal_paratha",
                "Egg Bhurji|Eggs, onions, spices|Scramble eggs with spices|250 kcal|20g|10g|egg_bhurji",
                "Chicken Stuffed Paratha|Chicken, wheat flour|Stuff paratha with chicken, cook|400 kcal|25g|40g|chicken_stuffed_paratha",
                "Sprouts Chilla|Moong sprouts, gram flour|Blend sprouts, cook as chilla|250 kcal|15g|20g|sprouts_chilla",
                "Soya Upma|Soya granules, rava, veggies|Cook upma with soya|300 kcal|18g|30g|soya_upma",
                "Paneer Dosa|Paneer, rice flour|Stuff dosa with paneer|350 kcal|20g|40g|paneer_dosa",
                "Oats with Paneer|Oats, paneer, spices|Cook oats with paneer|300 kcal|18g|30g|oats_with_paneer",
                "Chicken Poha|Chicken, flattened rice|Cook poha with chicken|350 kcal|25g|35g|chicken_poha",
                "Soya Chilla|Soya flour, veggies|Cook chilla with soya flour|250 kcal|15g|20g|soya_chilla",
                "Protein Shake|Whey protein, milk, banana|Blend ingredients|300 kcal|25g|35g|protein_shake",
                "Egg White Omelette|Egg whites, spinach, cheese|Cook omelette with fillings|200 kcal|20g|5g|egg_white_omelette",
                "Turkey Bacon Wrap|Turkey bacon, eggs, tortilla|Wrap bacon and eggs in tortilla|350 kcal|25g|30g|turkey_bacon_wrap",
                "Greek Yogurt with Protein|Greek yogurt, protein powder|Mix yogurt with protein|250 kcal|20g|15g|greek_yogurt_with_protein",
                "Chicken Sausage Muffin|Chicken sausage, muffin|Cook sausage, serve in muffin|300 kcal|20g|25g|chicken_sausage_muffin",
                "Peanut Butter Oatmeal|Oats, peanut butter, milk|Cook oats with peanut butter|350 kcal|15g|40g|peanut_butter_oatmeal",
                "Tuna Toast|Tuna, whole grain bread|Spread tuna on toast|250 kcal|20g|20g|tuna_toast",
                "Egg and Avocado Toast|Eggs, avocado, bread|Cook eggs, spread avocado|300 kcal|15g|25g|egg_and_avocado_toast",
                "Protein Pancakes|Protein powder, eggs, banana|Cook pancakes with protein|350 kcal|25g|35g|protein_pancakes",
                "Salmon Bagel|Salmon, bagel, cream cheese|Spread cream cheese, add salmon|400 kcal|25g|40g|salmon_bagel",
                "Quinoa Porridge|Quinoa, milk, protein powder|Cook quinoa with milk|300 kcal|20g|30g|quinoa_porridge",
                "Tofu Scramble|Tofu, spinach, spices|Crumble tofu, cook with veggies|250 kcal|20g|10g|tofu_scramble",
                "Chicken Omelette|Chicken, eggs, cheese|Cook omelette with chicken|350 kcal|30g|10g|chicken_omelette",
                "Sardine Toast|Sardines, whole grain bread|Spread sardines on toast|250 kcal|20g|20g|sardine_toast",
                "Protein Smoothie|Protein powder, berries, milk|Blend ingredients|300 kcal|25g|30g|protein_smoothie",
                "Egg Muffins|Eggs, turkey, cheese|Bake muffins with eggs|300 kcal|25g|10g|egg_muffins",
                "Cottage Cheese Bowl|Cottage cheese, nuts, honey|Mix cheese with nuts|350 kcal|25g|20g|cottage_cheese_bowl",
                "Paneer Wrap|Paneer, tortilla|Wrap paneer in tortilla|350 kcal|20g|30g|paneer_wrap",
                "Oatmeal with Eggs|Oats, eggs, milk|Cook oats, top with eggs|350 kcal|20g|35g|oatmeal_with_eggs",
                "Shrimp Omelette|Shrimp, eggs, cheese|Cook omelette with shrimp|300 kcal|25g|10g|shrimp_omelette"
        });
        mgRecipes.put("Lunch", new String[]{
                "Chicken Curry|Chicken, spices, rice|Cook chicken curry, serve with rice|450 kcal|35g|40g|chicken_curry",
                "Soya Chunk Pulao|Soya chunks, rice, veggies|Cook pulao with soya|400 kcal|25g|45g|soya_chunk_pulao",
                "Fish Curry|Fish, coconut milk, rice|Cook fish curry, serve with rice|450 kcal|30g|40g|fish_curry",
                "Paneer Pulao|Paneer, rice, spices|Cook pulao with paneer|400 kcal|25g|45g|paneer_pulao",
                "Egg Curry|Eggs, spices, rice|Cook egg curry, serve with rice|400 kcal|25g|40g|egg_curry",
                "Chicken Tikka|Chicken, yogurt, spices|Marinate chicken, grill|450 kcal|35g|10g|chicken_tikka",
                "Soya Matar|Soya chunks, peas, spices|Cook soya with peas|350 kcal|25g|20g|soya_matar",
                "Mutton Curry|Mutton, spices, rice|Cook mutton curry, serve with rice|500 kcal|40g|40g|mutton_curry",
                "Chickpea Curry|Chickpeas, spices, rice|Cook chickpeas, serve with rice|400 kcal|20g|50g|chickpea_curry",
                "Tofu Curry|Tofu, spices, rice|Cook tofu curry, serve with rice|350 kcal|20g|40g|tofu_curry",
                "Grilled Chicken Breast|Chicken breast, rice, broccoli|Grill chicken, steam rice and broccoli|400 kcal|35g|45g|grilled_chicken_breast",
                "Salmon Salad|Salmon, mixed greens, avocado|Grill salmon, toss with greens and avocado|350 kcal|30g|20g|salmon_salad",
                "Turkey Burger|Turkey patty, bun, lettuce|Grill patty, assemble burger|400 kcal|30g|35g|turkey_burger",
                "Tofu Stir-Fry|Tofu, rice, broccoli|Stir-fry tofu, serve with rice|400 kcal|25g|40g|tofu_stir_fry",
                "Tuna Wrap|Tuna, tortilla, lettuce|Wrap tuna with veggies|350 kcal|30g|30g|tuna_wrap",
                "Chicken Quinoa Bowl|Chicken, quinoa, veggies|Grill chicken, mix with quinoa|400 kcal|35g|40g|chicken_quinoa_bowl",
                "Shrimp Salad|Shrimp, greens, avocado|Grill shrimp, toss with greens|350 kcal|30g|20g|shrimp_salad",
                "Egg Salad Wrap|Eggs, tortilla, mayo|Mix eggs with mayo, wrap|400 kcal|25g|30g|egg_salad_wrap",
                "Paneer Steak|Paneer, sweet potato|Grill paneer, roast potato|400 kcal|25g|40g|paneer_steak",
                "Turkey Meatballs|Turkey, quinoa, sauce|Bake meatballs, serve with quinoa|400 kcal|30g|35g|turkey_meatballs",
                "Lentil Bowl|Lentils, rice, avocado|Cook lentils, serve with rice|400 kcal|20g|50g|lentil_bowl",
                "Grilled Tofu|Tofu, rice, veggies|Grill tofu, serve with rice|350 kcal|20g|40g|grilled_tofu",
                "Sardine Salad|Sardines, greens, lemon|Mix sardines with greens|350 kcal|30g|15g|sardine_salad",
                "Chicken Stir-Fry|Chicken, rice, soy sauce|Stir-fry chicken, serve with rice|400 kcal|35g|40g|chicken_stir_fry",
                "Paneer Wrap|Paneer, tortilla, cheese|Wrap paneer with cheese|400 kcal|25g|35g|paneer_wrap",
                "Salmon Quinoa|Salmon, quinoa, kale|Grill salmon, serve with quinoa|400 kcal|35g|35g|salmon_quinoa",
                "Tuna Rice Bowl|Tuna, rice, avocado|Mix tuna with rice and avocado|400 kcal|30g|40g|tuna_rice_bowl",
                "Egg Fried Rice|Eggs, rice, veggies|Cook fried rice with eggs|400 kcal|20g|50g|egg_fried_rice",
                "Chicken Salad|Chicken, greens, nuts|Grill chicken, toss with greens|350 kcal|35g|15g|chicken_salad",
                "Tofu Tenderloin|Tofu, quinoa, veggies|Grill tofu, serve with quinoa|400 kcal|25g|40g|tofu_tenderloin"
        });
        mgRecipes.put("Dinner", new String[]{
                "Chicken Biryani|Chicken, basmati rice, spices|Cook biryani with chicken|500 kcal|35g|50g|chicken_biryani",
                "Fish Tikka|Fish, yogurt, spices|Marinate fish, grill|400 kcal|35g|10g|fish_tikka",
                "Paneer Tikka|Paneer, yogurt, spices|Marinate paneer, grill|450 kcal|25g|15g|paneer_tikka",
                "Soya Curry|Soya chunks, spices, rice|Cook soya curry, serve with rice|400 kcal|25g|40g|soya_curry",
                "Egg Bhurji with Roti|Eggs, spices, roti|Cook egg bhurji, serve with roti|400 kcal|25g|35g|egg_bhurji_with_roti",
                "Chicken Korma|Chicken, cream, rice|Cook chicken in creamy sauce|500 kcal|35g|40g|chicken_korma",
                "Mutton Keema|Mutton, spices, roti|Cook keema, serve with roti|450 kcal|35g|30g|mutton_keema",
                "Prawn Curry|Prawns, spices, rice|Cook prawn curry, serve with rice|400 kcal|30g|40g|prawn_curry",
                "Chickpea Pulao|Chickpeas, rice, spices|Cook pulao with chickpeas|400 kcal|20g|50g|chickpea_pulao",
                "Tofu Tikka|Tofu, yogurt, spices|Marinate tofu, grill|350 kcal|20g|15g|tofu_tikka",
                "Baked Chicken Thighs|Chicken thighs, quinoa, kale|Bake thighs, cook quinoa, sauté kale|450 kcal|35g|40g|baked_chicken_thighs",
                "Paneer Fillet|Paneer, rice, broccoli|Bake paneer, serve with rice|400 kcal|25g|40g|paneer_fillet",
                "Salmon Fillet|Salmon, rice, broccoli|Bake salmon, serve with rice|450 kcal|35g|40g|salmon_fillet",
                "Turkey Chili|Turkey, beans, tomatoes|Cook chili with turkey|400 kcal|35g|35g|turkey_chili",
                "Tofu Tenderloin|Tofu, quinoa, veggies|Grill tofu, serve with quinoa|400 kcal|25g|40g|tofu_tenderloin",
                "Shrimp Stir-Fry|Shrimp, rice, veggies|Stir-fry shrimp, serve with rice|400 kcal|30g|40g|shrimp_stir_fry",
                "Chicken Parmesan|Chicken, quinoa, sauce|Bake chicken with sauce|450 kcal|35g|35g|chicken_parmesan",
                "Paneer Chop|Paneer, sweet potato, kale|Grill paneer, roast potato|400 kcal|25g|40g|paneer_chop",
                "Tuna Steak|Tuna, rice, asparagus|Grill tuna, serve with rice|400 kcal|35g|35g|tuna_steak",
                "Chickpea Burger|Chickpea patty, bun, lettuce|Grill patty, assemble burger|400 kcal|20g|50g|chickpea_burger",
                "Grilled Chicken|Chicken, rice, avocado|Grill chicken, serve with rice|450 kcal|35g|40g|grilled_chicken",
                "Lentil Curry|Lentils, rice, spices|Cook lentil curry, serve with rice|400 kcal|20g|50g|lentil_curry",
                "Salmon Curry|Salmon, coconut milk, rice|Cook salmon curry, serve with rice|450 kcal|35g|40g|salmon_curry",
                "Tofu Stir-Fry|Tofu, rice, veggies|Stir-fry tofu, serve with rice|350 kcal|20g|40g|tofu_stir_fry",
                "Lentil Kebabs|Lentils, veggies, rice|Grill kebabs, serve with rice|400 kcal|20g|50g|lentil_kebabs",
                "Chicken Tacos|Chicken, tortillas, avocado|Fill tortillas with chicken|400 kcal|35g|35g|chicken_tacos",
                "Sardine Rice|Sardines, rice, veggies|Cook rice with sardines|400 kcal|30g|40g|sardine_rice",
                "Egg Curry|Eggs, spices, rice|Cook egg curry, serve with rice|400 kcal|25g|40g|egg_curry",
                "Turkey Wrap|Turkey, tortilla, cheese|Wrap turkey with cheese|400 kcal|35g|30g|turkey_wrap",
                "Shrimp Quinoa|Shrimp, quinoa, kale|Grill shrimp, serve with quinoa|400 kcal|35g|35g|shrimp_quinoa"
        });

        // Challenges
        String[][] challenges = {
                // Weight Loss Challenges (17)
                {"Weight Loss 7-Day Sprint", "A week-long plan to jumpstart weight loss.", "Weight Loss", "weight_loss_7_day_sprint"},
                {"Low-Carb Challenge", "30 days of low-carb meals for fat loss.", "Weight Loss", "low_carb_challenge"},
                {"Intermittent Fasting 16:8", "Fast for 16 hours, eat within an 8-hour window for 30 days.", "Weight Loss", "intermittent_fasting_16_8"},
                {"No Sugar Challenge", "Eliminate added sugars for 21 days to reduce cravings.", "Weight Loss", "no_sugar_challenge"},
                {"Veggie Boost Challenge", "Eat 5 servings of vegetables daily for 30 days.", "Weight Loss", "veggie_boost_challenge"},
                {"Calorie Deficit Dash", "Maintain a 500-calorie deficit daily for 14 days.", "Weight Loss", "calorie_deficit_dash"},
                {"Keto Kickstart", "Follow a ketogenic diet for 28 days to burn fat.", "Weight Loss", "keto_kickstart"},
                {"Portion Control Plan", "Use smaller plates and track portions for 21 days.", "Weight Loss", "portion_control_plan"},
                {"Hydration Challenge", "Drink 3 liters of water daily for 30 days.", "Weight Loss", "hydration_challenge"},
                {"Fiber Feast", "Consume 30g of fiber daily for 21 days to stay full.", "Weight Loss", "fiber_feast"},
                {"Mindful Eating Month", "Eat without distractions for 30 days to control intake.", "Weight Loss", "mindful_eating_month"},
                {"Plant-Based Week", "Follow a vegetarian diet for 7 days to cut calories.", "Weight Loss", "plant_based_week"},
                {"No Fried Foods", "Avoid fried foods for 30 days to reduce fat intake.", "Weight Loss", "no_fried_foods"},
                {"Meal Prep Mastery", "Prepare all meals in advance for 14 days.", "Weight Loss", "meal_prep_mastery"},
                {"Whole Foods Focus", "Eat only unprocessed foods for 21 days.", "Weight Loss", "whole_foods_focus"},
                {"Low-Cal Snack Swap", "Replace high-calorie snacks with low-cal options for 30 days.", "Weight Loss", "low_cal_snack_swap"},
                {"Daily Salad Challenge", "Eat a large salad daily for 30 days to boost nutrients.", "Weight Loss", "daily_salad_challenge"},

                // Weight Gain Challenges (17)
                {"Weight Gain Bulk", "High-calorie diet for 30 days.", "Weight Gain", "weight_gain_bulk"},
                {"Protein-Packed Month", "High-protein meals for 30 days.", "Weight Gain", "protein_packed_month"},
                {"Calorie Surplus Challenge", "Consume 500 extra calories daily for 30 days.", "Weight Gain", "calorie_surplus_challenge"},
                {"Nut Butter Boost", "Add 2 tbsp of nut butter to meals daily for 21 days.", "Weight Gain", "nut_butter_boost"},
                {"Carb Load Cycle", "Eat 400g of carbs daily for 30 days.", "Weight Gain", "carb_load_cycle"},
                {"Shake It Up", "Drink a 1000-calorie shake daily for 14 days.", "Weight Gain", "shake_it_up"},
                {"Healthy Fats Feast", "Include avocado or olive oil in every meal for 30 days.", "Weight Gain", "healthy_fats_feast"},
                {"Frequent Feeding", "Eat 6 meals a day for 21 days to increase intake.", "Weight Gain", "frequent_feeding"},
                {"Dairy Delight", "Add full-fat dairy to meals for 30 days.", "Weight Gain", "dairy_delight"},
                {"Snack Attack", "Eat a high-calorie snack every 3 hours for 14 days.", "Weight Gain", "snack_attack"},
                {"Rice and Ghee Plan", "Include rice with ghee in 2 meals daily for 30 days.", "Weight Gain", "rice_and_ghee_plan"},
                {"Banana Bonanza", "Eat 4 bananas daily for 21 days for extra calories.", "Weight Gain", "banana_bonanza"},
                {"Nuts and Seeds Surge", "Add 50g of nuts/seeds daily for 30 days.", "Weight Gain", "nuts_and_seeds_surge"},
                {"Creamy Cuisine", "Use cream-based sauces in meals for 21 days.", "Weight Gain", "creamy_cuisine"},
                {"Bread and Butter", "Eat whole-grain bread with butter daily for 30 days.", "Weight Gain", "bread_and_butter"},
                {"Pasta Power", "Include pasta in one meal daily for 30 days.", "Weight Gain", "pasta_power"},
                {"Sweet Potato Stack", "Eat sweet potatoes daily for 30 days for dense carbs.", "Weight Gain", "sweet_potato_stack"},

                // Muscle Gain Challenges (15)
                {"Muscle Gain Strength", "Protein-rich plan for muscle growth.", "Muscle Gain", "muscle_gain_strength"},
                {"High-Protein Cycle", "90g+ protein daily for 30 days.", "Muscle Gain", "high_protein_cycle"},
                {"Protein Timing Trial", "Consume 30g protein every 3 hours for 21 days.", "Muscle Gain", "protein_timing_trial"},
                {"Lean Bulk Blast", "Eat 1.5g protein per kg body weight for 30 days.", "Muscle Gain", "lean_bulk_blast"},
                {"Egg Explosion", "Eat 6 eggs daily for 30 days for protein.", "Muscle Gain", "egg_explosion"},
                {"Chicken Challenge", "Include chicken in 2 meals daily for 21 days.", "Muscle Gain", "chicken_challenge"},
                {"Fish Frenzy", "Eat fish 5 times a week for 30 days for lean protein.", "Muscle Gain", "fish_frenzy"},
                {"Greek Yogurt Goal", "Eat 200g Greek yogurt daily for 30 days.", "Muscle Gain", "greek_yogurt_goal"},
                {"Quinoa Quest", "Include quinoa in one meal daily for 21 days.", "Muscle Gain", "quinoa_quest"},
                {"Tofu Takeover", "Eat tofu 4 times a week for 30 days for plant protein.", "Muscle Gain", "tofu_takeover"},
                {"Protein Shake Plan", "Drink a protein shake post-workout for 30 days.", "Muscle Gain", "protein_shake_plan"},
                {"Lentil Lift", "Eat lentils daily for 21 days for protein and carbs.", "Muscle Gain", "lentil_lift"},
                {"Turkey Trek", "Include turkey in meals 3 times a week for 30 days.", "Muscle Gain", "turkey_trek"},
                {"Cottage Cheese Craze", "Eat cottage cheese daily for 30 days.", "Muscle Gain", "cottage_cheese_craze"},
                {"Bean Boost", "Include beans in meals 5 times a week for 30 days.", "Muscle Gain", "bean_boost"}
        };

        int wlCount = 0, wgCount = 0, mgCount = 0;
        for (String[] challenge : challenges) {
            ContentValues values = new ContentValues();
            values.put(COL_CHALLENGE_NAME, challenge[0]);
            values.put(COL_CHALLENGE_DESC, challenge[1]);
            values.put(COL_CHALLENGE_DIET_TYPE, challenge[2]);
            values.put(COL_IMAGE, challenge[3]);
            long result = db.insertWithOnConflict(TABLE_CHALLENGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (result != -1) {
                if (challenge[2].equals("Weight Loss")) wlCount++;
                else if (challenge[2].equals("Weight Gain")) wgCount++;
                else if (challenge[2].equals("Muscle Gain")) mgCount++;
            } else {
                Log.w(TAG, "Failed to insert challenge: " + challenge[0]);
            }
        }
        Log.d(TAG, "Inserted challenges: Weight Loss=" + wlCount + ", Weight Gain=" + wgCount + ", Muscle Gain=" + mgCount);

        // Verify challenge counts
        String[] dietTypes = {"Weight Loss", "Weight Gain", "Muscle Gain"};
        for (String diet : dietTypes) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CHALLENGES +
                    " WHERE " + COL_CHALLENGE_DIET_TYPE + " = ?", new String[]{diet});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Inserted challenge count for " + diet + ": " + count);
            }
            cursor.close();
        }

        // Verify recipe counts
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner"};
        for (String diet : dietTypes) {
            for (String meal : mealTypes) {
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES +
                                " WHERE " + COL_DIET_TYPE + " = ? AND " + COL_RECIPE_TYPE + " = ?",
                        new String[]{diet, meal});
                if (cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    Log.d(TAG, "Inserted recipe count for " + diet + " - " + meal + ": " + count);
                }
                cursor.close();
            }
        }

        // Insert recipes
        insertRecipes(db, wlRecipes, "Weight Loss");
        insertRecipes(db, wgRecipes, "Weight Gain");
        insertRecipes(db, mgRecipes, "Muscle Gain");

        // Insert health tips using the class-level healthTips array
        int healthTipsCount = 0;
        ContentValues healthTipValues = new ContentValues();
        for (String tip : healthTips) {
            String[] parts = tip.split(" , ", 3); // Split into category, title, description
            if (parts.length == 3) {
                healthTipValues.put(COL_CATEGORY, parts[0].trim());
                healthTipValues.put(COL_TITLE, parts[1].trim());
                healthTipValues.put(COL_DESCRIPTION, parts[2].trim());
                long result = db.insertWithOnConflict(TABLE_HEALTH_TIPS, null, healthTipValues, SQLiteDatabase.CONFLICT_REPLACE);
                if (result != -1) {
                    healthTipsCount++;
                } else {
                    Log.w(TAG, "Failed to insert health tip: " + parts[1]);
                }
                healthTipValues.clear();
            } else {
                Log.e(TAG, "Invalid health tip format: " + tip);
            }
        }
        Log.d(TAG, "Inserted " + healthTipsCount + " health tips");



        // Verify health tips count
        Cursor healthTipsCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HEALTH_TIPS, null);
        if (healthTipsCursor.moveToFirst()) {
            int count = healthTipsCursor.getInt(0);
            Log.d(TAG, "Total health tips in table: " + count);
        }
        healthTipsCursor.close();

        Log.d(TAG, "Completed insertSampleData");
    }


    // Method to retrieve all health tips
    public List<HealthTip> getHealthTips() {
        List<HealthTip> tips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HEALTH_TIPS,
                new String[]{COL_CATEGORY, COL_TITLE, COL_DESCRIPTION},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
            tips.add(new HealthTip(category, title, description));
        }

        cursor.close();
        db.close();
        Log.d(TAG, "getHealthTips: Retrieved " + tips.size() + " health tips");
        return tips;
    }

    public void validateRecipeImages() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, new String[]{COL_RECIPE_NAME, COL_IMAGE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String recipeName = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));
            int resId = dbContext.getResources().getIdentifier(imageName, "drawable", dbContext.getPackageName());
            if (resId == 0) {
                Log.w(TAG, "Invalid image for recipe: " + recipeName + ", image: " + imageName);
            }
        }
        cursor.close();
        db.close();
    }

    private void insertRecipes(SQLiteDatabase db, Map<String, String[]> recipes, String dietType) {
        for (Map.Entry<String, String[]> entry : recipes.entrySet()) {
            String recipeType = entry.getKey();
            for (String recipeData : entry.getValue()) {
                String[] parts = recipeData.split("\\|");
                if (parts.length < 7) {
                    Log.e(TAG, "Invalid recipe data for " + dietType + " - " + recipeType + ": " + recipeData);
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put(COL_RECIPE_NAME, parts[0]);
                values.put(COL_DIET_TYPE, dietType);
                values.put(COL_RECIPE_TYPE, recipeType);
                values.put(COL_INGREDIENTS, parts[1]);
                values.put(COL_DIRECTIONS, parts[2]);
                values.put(COL_CALORIES, parts[3]);
                values.put(COL_PROTEINS, parts[4]);
                values.put(COL_CARBS, parts[5]);
                values.put(COL_IMAGE, parts[6]);

                // Verify image resource exists
                int resId = dbContext.getResources().getIdentifier(parts[6], "drawable", dbContext.getPackageName());
                if (resId == 0) {
                    Log.w(TAG, "Image resource not found for recipe: " + parts[0] + ", image: " + parts[6]);
                }

                long result = db.insertWithOnConflict(TABLE_RECIPES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (result == -1) {
                    Log.w(TAG, "Failed to insert recipe: " + parts[0] + " for " + dietType + " - " + recipeType);
                }
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Check if user_challenges table exists
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_USER_CHALLENGES});
        boolean tableExists = cursor.moveToFirst();
        cursor.close();

        if (!tableExists) {
            // Create user_challenges table
            String CREATE_USER_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_USER_CHALLENGES + "("
                    + COL_EMAIL + " TEXT,"
                    + COL_CHALLENGE_NAME + " TEXT,"
                    + COL_DIET_PREF + " TEXT,"
                    + "PRIMARY KEY (" + COL_EMAIL + ", " + COL_CHALLENGE_NAME + ")"
                    + ")";
            db.execSQL(CREATE_USER_CHALLENGES_TABLE);
            Log.d(TAG, "Created user_challenges table during upgrade to version " + newVersion);
        } else {
            Log.d(TAG, "user_challenges table already exists, no action needed");
        }

        // Add future upgrade steps here for higher versions
        Log.d(TAG, "Database upgrade completed");
    }

    public boolean insertData(String email, String username, String password, String dietPreference) {
        if (email == null || username == null || password == null || dietPreference == null) {
            Log.e(TAG, "insertData: Null input detected - email: " + email + ", username: " + username + ", password: " + password + ", dietPreference: " + dietPreference);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_DIET_PREF, dietPreference);
        contentValues.put(COL_HEIGHT, 0);
        contentValues.put(COL_WEIGHT, 0);
        contentValues.put(COL_AGE, 0);
        contentValues.put(COL_GENDER, "");
        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        if (result == -1) {
            Log.e(TAG, "insertData: Failed to insert data for email: " + email);
            return false;
        } else {
            Log.d(TAG, "insertData: Successfully inserted data for email: " + email);
            return true;
        }
    }

    public boolean checkUser(String email, String password) {
        if (email == null || password == null) {
            Log.e(TAG, "checkUser: Null input detected - email: " + email + ", password: " + password);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ID};
        String selection = COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.d(TAG, "checkUser: Email: " + email + ", Found: " + count + " user(s)");
        return count > 0;
    }

    public boolean updateHeightAndWeight(String email, float height, float weight) {
        if (email == null) {
            Log.e(TAG, "updateHeightAndWeight: Email is null");
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HEIGHT, height);
        values.put(COL_WEIGHT, weight);
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();
        Log.d(TAG, "updateHeightAndWeight: Email: " + email + ", Height: " + height + ", Weight: " + weight + ", Rows updated: " + rows);
        if (rows == 0) {
            Log.w(TAG, "updateHeightAndWeight: No rows updated for email: " + email + ". Email may not exist in database.");
        }
        return rows > 0;
    }

    public boolean updateAgeAndGender(String email, int age, String gender) {
        if (email == null || gender == null) {
            Log.e(TAG, "updateAgeAndGender: Invalid input - email: " + email + ", gender: " + gender);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();
        Log.d(TAG, "updateAgeAndGender: Email: " + email + ", Age: " + age + ", Gender: " + gender + ", Rows updated: " + rows);
        if (rows == 0) {
            Log.w(TAG, "updateAgeAndGender: No rows updated for email: " + email + ". Email may not exist in database.");
        }
        return rows > 0;
    }

    public boolean checkEmailExists(String email) {
        if (email == null) {
            Log.e(TAG, "checkEmailExists: Email is null");
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ID};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.d(TAG, "checkEmailExists: Email: " + email + ", Found: " + count + " user(s)");
        return count > 0;
    }

    public String getUsername(String email) {
        if (email == null) {
            Log.e(TAG, "getUsername: Email is null");
            return null;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USERNAME};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getUsername: Email: " + email + ", Username: " + username);
        return username;
    }

    public boolean updateUsername(String email, String newUsername) {
        if (email == null || newUsername == null) {
            Log.e(TAG, "updateUsername: Invalid input - email: " + email + ", newUsername: " + newUsername);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, newUsername);
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();
        Log.d(TAG, "updateUsername: Email: " + email + ", New Username: " + newUsername + ", Rows updated: " + rows);
        return rows > 0;
    }

    public boolean deleteUser(String email, String password) {
        if (email == null || password == null) {
            Log.e(TAG, "deleteUser: Null input detected - email: " + email + ", password: " + password);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            if (!checkUser(email, password)) {
                Log.e(TAG, "deleteUser: Invalid credentials for email: " + email);
                return false;
            }
            int rowsDeleted = db.delete(TABLE_USERS, COL_EMAIL + " = ?", new String[]{email});
            db.delete(TABLE_FEEDBACK, COL_EMAIL + " = ?", new String[]{email});
            db.delete(TABLE_REMINDERS, COL_EMAIL + " = ?", new String[]{email});
            db.delete(TABLE_MEALS, COL_EMAIL + " = ?", new String[]{email});
            db.setTransactionSuccessful();
            Log.d(TAG, "deleteUser: Email: " + email + ", Rows deleted from users: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteUser: Error deleting user for email: " + email, e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public float[] getHeightAndWeight(String email) {
        if (email == null) {
            Log.e(TAG, "getHeightAndWeight: Email is null");
            return new float[]{0, 0};
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        float[] result = new float[]{0, 0};
        String[] columns = {COL_HEIGHT, COL_WEIGHT};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            result[0] = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_HEIGHT));
            result[1] = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getHeightAndWeight: Email: " + email + ", Height: " + result[0] + ", Weight: " + result[1]);
        return result;
    }

    public String getDietPreference(String email) {
        if (email == null) {
            Log.e(TAG, "getDietPreference: Email is null");
            return null;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_DIET_PREF};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        String dietPref = null;
        if (cursor.moveToFirst()) {
            dietPref = cursor.getString(cursor.getColumnIndexOrThrow(COL_DIET_PREF));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getDietPreference: Email: " + email + ", Diet Preference: " + dietPref);
        return dietPref;
    }

    public boolean updateDietPreference(String email, String dietPref) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "updateDietPreference: Email is null or empty");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DIET_PREF, dietPref);
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
        db.close();
        Log.d(TAG, "updateDietPreference: Email: " + email + ", Diet: " + dietPref + ", Rows updated: " + rows);
        return rows > 0;
    }

    public int getAge(String email) {
        if (email == null) {
            Log.e(TAG, "getAge: Email is null");
            return 0;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_AGE};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int age = 0;
        if (cursor.moveToFirst()) {
            age = cursor.getInt(cursor.getColumnIndexOrThrow(COL_AGE));
        } else {
            Log.w(TAG, "getAge: No user found for email: " + email);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getAge: Email: " + email + ", Age: " + age);
        return age;
    }

    public String getGender(String email) {
        if (email == null) {
            Log.e(TAG, "getGender: Email is null");
            return null;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_GENDER};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        String gender = null;
        if (cursor.moveToFirst()) {
            gender = cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER));
        } else {
            Log.w(TAG, "getGender: No user found for email: " + email);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getGender: Email: " + email + ", Gender: " + gender);
        return gender;
    }

    public boolean insertFeedback(String email, String feedback) {
        if (email == null || feedback == null) {
            Log.e(TAG, "insertFeedback: Invalid input - email: " + email + ", feedback: " + feedback);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_FEEDBACK, feedback);
        long result = db.insert(TABLE_FEEDBACK, null, values);
        db.close();
        if (result == -1) {
            Log.e(TAG, "insertFeedback: Failed to insert feedback for email: " + email);
            return false;
        } else {
            Log.d(TAG, "insertFeedback: Successfully inserted feedback for email: " + email);
            return true;
        }
    }

    public ArrayList<String> getFeedback(String email) {
        if (email == null) {
            Log.e(TAG, "getFeedback: Email is null");
            return new ArrayList<>();
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> feedbackList = new ArrayList<>();
        String[] columns = {COL_FEEDBACK};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_FEEDBACK, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            String feedback = cursor.getString(cursor.getColumnIndexOrThrow(COL_FEEDBACK));
            feedbackList.add(feedback);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getFeedback: Email: " + email + ", Feedback count: " + feedbackList.size());
        return feedbackList;
    }

    public boolean insertReminder(String email, String reminderTime, String reminderType) {
        if (email == null || reminderTime == null || reminderType == null) {
            Log.e(TAG, "insertReminder: Invalid input - email: " + email + ", reminderTime: " + reminderTime + ", reminderType: " + reminderType);
            return false;
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_REMINDER_TIME, reminderTime);
        values.put(COL_REMINDER_TYPE, reminderType);
        long result = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        if (result == -1) {
            Log.e(TAG, "insertReminder: Failed to insert reminder for email: " + email);
            return false;
        } else {
            Log.d(TAG, "insertReminder: Successfully inserted reminder for email: " + email);
            return true;
        }
    }

    public ArrayList<Map<String, String>> getReminders(String email) {
        if (email == null) {
            Log.e(TAG, "getReminders: Email is null");
            return new ArrayList<>();
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Map<String, String>> remindersList = new ArrayList<>();
        String[] columns = {COL_REMINDER_TIME, COL_REMINDER_TYPE};
        String selection = COL_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_REMINDERS, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> reminder = new HashMap<>();
            reminder.put(COL_REMINDER_TIME, cursor.getString(cursor.getColumnIndexOrThrow(COL_REMINDER_TIME)));
            reminder.put(COL_REMINDER_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COL_REMINDER_TYPE)));
            remindersList.add(reminder);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getReminders: Email: " + email + ", Reminders count: " + remindersList.size());
        return remindersList;
    }

    public void logAllMealsForDate(String email, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS,
                new String[]{COL_MEAL_ID, COL_EMAIL, COL_MEAL_DATE, COL_MEAL_TYPE, COL_MEAL_DETAILS}, // Changed COL_ID to COL_MEAL_ID
                COL_EMAIL + "=? AND " + COL_MEAL_DATE + "=?",
                new String[]{email, date},
                null, null, null);

        Log.d(TAG, "logAllMealsForDate: Meals for email=" + email + ", date=" + date);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEAL_ID)); // Changed COL_ID to COL_MEAL_ID
                String mealType = cursor.getString(cursor.getColumnIndexOrThrow(COL_MEAL_TYPE));
                String mealDetails = cursor.getString(cursor.getColumnIndexOrThrow(COL_MEAL_DETAILS));
                Log.d(TAG, "Meal ID=" + id + ", Type=" + mealType + ", Details=" + mealDetails);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No meals found for email=" + email + ", date=" + date);
        }
        cursor.close();
    }
    public long insertMeal(String email, String mealDate, String mealType, String mealDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_MEAL_DATE, mealDate);
        values.put(COL_MEAL_TYPE, mealType);
        values.put(COL_MEAL_DETAILS, mealDetails);

        Cursor cursor = db.query(TABLE_MEALS,
                new String[]{COL_MEAL_ID}, // Changed from COL_ID to COL_MEAL_ID
                COL_EMAIL + "=? AND " + COL_MEAL_DATE + "=? AND " + COL_MEAL_TYPE + "=?",
                new String[]{email, mealDate, mealType},
                null, null, null);

        long result;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEAL_ID)); // Changed from COL_ID to COL_MEAL_ID
            result = db.update(TABLE_MEALS, values, COL_MEAL_ID + "=?", new String[]{String.valueOf(id)});
            Log.d(TAG, "insertMeal: Updated meal with ID: " + id);
        } else {
            result = db.insert(TABLE_MEALS, null, values);
            Log.d(TAG, "insertMeal: Inserted new meal with ID: " + result);
        }
        cursor.close();
        Log.d(TAG, "insertMeal: " + (result != -1 ? "Successfully inserted/updated meal with ID: " + result : "Failed to insert/update meal"));
        return result;
    }

    public String getMeal(String email, String date, String mealType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String meal = null;
        try {
            cursor = db.rawQuery("SELECT meal_details FROM meals WHERE email = ? AND meal_date = ? AND meal_type = ?",
                    new String[]{email, date, mealType});
            if (cursor.moveToFirst()) {
                meal = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getMeal: Error querying meal for email=" + email + ", date=" + date + ", mealType=" + mealType, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return meal;
    }

    public ArrayList<Map<String, String>> getRecipes(String dietType, String recipeType) {
        if (dietType == null || recipeType == null) {
            Log.e(TAG, "getRecipes: Invalid input - dietType: " + dietType + ", recipeType: " + recipeType);
            return new ArrayList<>();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Map<String, String>> recipesList = new ArrayList<>();
        String[] columns = {COL_RECIPE_NAME, COL_INGREDIENTS, COL_DIRECTIONS, COL_CALORIES, COL_PROTEINS, COL_CARBS, COL_IMAGE};
        String selection = COL_DIET_TYPE + " = ? AND " + COL_RECIPE_TYPE + " = ?";
        String[] selectionArgs = {dietType, recipeType};
        Cursor cursor = db.query(TABLE_RECIPES, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> recipe = new HashMap<>();
            recipe.put(COL_RECIPE_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
            recipe.put(COL_INGREDIENTS, cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS)));
            recipe.put(COL_DIRECTIONS, cursor.getString(cursor.getColumnIndexOrThrow(COL_DIRECTIONS)));
            recipe.put(COL_CALORIES, cursor.getString(cursor.getColumnIndexOrThrow(COL_CALORIES)));
            recipe.put(COL_PROTEINS, cursor.getString(cursor.getColumnIndexOrThrow(COL_PROTEINS)));
            recipe.put(COL_CARBS, cursor.getString(cursor.getColumnIndexOrThrow(COL_CARBS)));
            recipe.put(COL_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
            recipesList.add(recipe);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getRecipes: DietType: " + dietType + ", RecipeType: " + recipeType + ", Recipes count: " + recipesList.size());
        return recipesList;
    }

    public ArrayList<Map<String, String>> getChallenges(String dietType) {
        if (dietType == null) {
            Log.e(TAG, "getChallenges: DietType is null");
            return new ArrayList<>();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Map<String, String>> challengesList = new ArrayList<>();
        String[] columns = {COL_CHALLENGE_NAME, COL_CHALLENGE_DESC, COL_IMAGE, COL_CHALLENGE_DIET_TYPE};
        String selection = COL_CHALLENGE_DIET_TYPE + " = ?";
        String[] selectionArgs = {dietType};
        Cursor cursor = db.query(TABLE_CHALLENGES, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> challenge = new HashMap<>();
            challenge.put(COL_CHALLENGE_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_NAME)));
            challenge.put(COL_CHALLENGE_DESC, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_DESC)));
            challenge.put(COL_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
            challenge.put(COL_CHALLENGE_DIET_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_DIET_TYPE)));
            challengesList.add(challenge);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getChallenges: DietType: " + dietType + ", Challenges count: " + challengesList.size());
        return challengesList;
    }

    public float[] getDailyNutrients(String email, String date) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "getDailyNutrients: Email is null or empty");
            return new float[]{0f, 0f, 0f};
        }
        SQLiteDatabase db = this.getReadableDatabase();
        float totalCalories = 0f, totalCarbs = 0f, totalProteins = 0f;

        String[] mealTypes = {"Breakfast", "Lunch", "Dinner"};
        for (String mealType : mealTypes) {
            String mealDetails = getMeal(email, date, mealType);
            if (mealDetails != null && !mealDetails.equals("Not set")) {
                Map<String, String> recipeDetails = getRecipeDetails(mealDetails);
                totalCalories += Float.parseFloat(recipeDetails.getOrDefault("calories", "0"));
                totalCarbs += Float.parseFloat(recipeDetails.getOrDefault("carbs", "0"));
                totalProteins += Float.parseFloat(recipeDetails.getOrDefault("proteins", "0"));
            }
        }

        db.close();
        Log.d(TAG, "getDailyNutrients: Email: " + email + ", Date: " + date + ", Calories: " + totalCalories + ", Carbs: " + totalCarbs + ", Proteins: " + totalProteins);
        return new float[]{totalCalories, totalCarbs, totalProteins};
    }

    public Map<String, String> getRecipeDetails(String recipeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> details = new HashMap<>();

        if (recipeName == null) {
            Log.e(TAG, "getRecipeDetails: RecipeName is null");
            return details;
        }

        Cursor cursor = db.query(TABLE_RECIPES,
                new String[]{COL_INGREDIENTS, COL_DIRECTIONS, COL_CALORIES, COL_PROTEINS, COL_CARBS, COL_IMAGE},
                COL_RECIPE_NAME + " = ?",
                new String[]{recipeName},
                null, null, null);

        if (cursor.moveToFirst()) {
            try {
                details.put("ingredients", cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS)));
                details.put("directions", cursor.getString(cursor.getColumnIndexOrThrow(COL_DIRECTIONS)));
                details.put("calories", cursor.getString(cursor.getColumnIndexOrThrow(COL_CALORIES)));
                details.put("proteins", cursor.getString(cursor.getColumnIndexOrThrow(COL_PROTEINS)));
                details.put("carbs", cursor.getString(cursor.getColumnIndexOrThrow(COL_CARBS)));
                details.put("image", cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getRecipeDetails: Column not found for recipe: " + recipeName, e);
            }
        } else {
            Log.w(TAG, "getRecipeDetails: No recipe found for recipeName: " + recipeName);
        }

        cursor.close();
        db.close();
        Log.d(TAG, "getRecipeDetails: Recipe: " + recipeName + ", Details: " + details);
        return details;
    }

    public String getChallengeDescription(String challengeName) {
        if (challengeName == null) {
            Log.e(TAG, "getChallengeDescription: ChallengeName is null");
            return "Description not found";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String description = "Description not found";

        String[] columns = {COL_CHALLENGE_DESC};
        String selection = COL_CHALLENGE_NAME + " = ?";
        String[] selectionArgs = {challengeName};

        Cursor cursor = db.query(TABLE_CHALLENGES, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            description = cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_DESC));
        }

        cursor.close();
        db.close();
        Log.d(TAG, "getChallengeDescription: ChallengeName: " + challengeName + ", Description: " + description);
        return description;
    }


    // Method to save profile image
    public boolean saveProfileImage(Context context, String email, byte[] imageBytes) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "saveProfileImage: Email is null or empty");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_IMAGE, imageBytes);
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
        db.close();
        Log.d(TAG, "saveProfileImage: Email: " + email + ", Image size: " + (imageBytes != null ? imageBytes.length : 0) + ", Rows updated: " + rows);
        return rows > 0;
    }

    // Method to retrieve profile image
    @SuppressLint("Range")
    public byte[] getProfileImage(String email) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "getProfileImage: Email is null or empty");
            return null;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageBytes = null;
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_PROFILE_IMAGE},
                COL_EMAIL + " = ?",
                new String[]{email.toLowerCase()},
                null, null, null);
        if (cursor.moveToFirst()) {
            imageBytes = cursor.getBlob(cursor.getColumnIndex(COL_PROFILE_IMAGE));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getProfileImage: Email: " + email + ", Image size: " + (imageBytes != null ? imageBytes.length : 0));
        return imageBytes;
    }


    public boolean deleteAccount(String email, String password) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "deleteAccount: Email is null or empty");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try {
            db.beginTransaction();
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{COL_EMAIL},
                    COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{email.toLowerCase(), password},
                    null, null, null);
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "deleteAccount: Invalid credentials for Email: " + email);
                cursor.close();
                return false;
            }
            cursor.close();

            int feedbackRows = db.delete(TABLE_FEEDBACK, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
            int reminderRows = db.delete(TABLE_REMINDERS, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
            int mealRows = db.delete(TABLE_MEALS, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
            int userRows = db.delete(TABLE_USERS, COL_EMAIL + " = ?", new String[]{email.toLowerCase()});
            Log.d(TAG, "deleteAccount: Deleted rows - Feedback: " + feedbackRows +
                    ", Reminders: " + reminderRows + ", Meals: " + mealRows + ", Users: " + userRows);

            if (userRows > 0) {
                db.setTransactionSuccessful();
                success = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteAccount: Error: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
        Log.d(TAG, "deleteAccount: Email: " + email + ", Success: " + success);
        return success;
    }

    // Helper method to ensure user_challenges table exists
    private void ensureUserChallengesTable(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_USER_CHALLENGES});
        boolean tableExists = cursor.moveToFirst();
        cursor.close();

        if (!tableExists) {
            String CREATE_USER_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_USER_CHALLENGES + "("
                    + COL_EMAIL + " TEXT,"
                    + COL_CHALLENGE_NAME + " TEXT,"
                    + COL_DIET_PREF + " TEXT,"
                    + "PRIMARY KEY (" + COL_EMAIL + ", " + COL_CHALLENGE_NAME + ")"
                    + ")";
            db.execSQL(CREATE_USER_CHALLENGES_TABLE);
            Log.d(TAG, "Created user_challenges table on-demand");
        }
    }

    // Method to add a followed challenge
    public boolean addFollowedChallenge(String email, String challengeName, String dietPreference) {
        SQLiteDatabase db = this.getWritableDatabase();
        ensureUserChallengesTable(db); // Ensure table exists before insert
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_CHALLENGE_NAME, challengeName);
        values.put(COL_DIET_PREF, dietPreference);

        long result = db.insert(TABLE_USER_CHALLENGES, null, values);
        db.close();
        if (result == -1) {
            Log.e(TAG, "Failed to add followed challenge: " + challengeName + " for " + email);
            return false;
        }
        Log.d(TAG, "Added followed challenge: " + challengeName + " for " + email);
        return true;
    }

    // Method to remove a followed challenge
    public boolean removeFollowedChallenge(String email, String challengeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ensureUserChallengesTable(db); // Ensure table exists before delete
        int result = db.delete(TABLE_USER_CHALLENGES,
                COL_EMAIL + " = ? AND " + COL_CHALLENGE_NAME + " = ?",
                new String[]{email, challengeName});
        db.close();
        if (result > 0) {
            Log.d(TAG, "Removed followed challenge: " + challengeName + " for " + email);
            return true;
        }
        Log.e(TAG, "Failed to remove followed challenge: " + challengeName + " for " + email);
        return false;
    }

    // Method to check if a challenge is followed
    public boolean isChallengeFollowed(String email, String challengeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ensureUserChallengesTable(db); // Ensure table exists before query
        Cursor cursor = db.query(TABLE_USER_CHALLENGES,
                new String[]{COL_CHALLENGE_NAME},
                COL_EMAIL + " = ? AND " + COL_CHALLENGE_NAME + " = ?",
                new String[]{email, challengeName},
                null, null, null);
        boolean isFollowed = cursor.moveToFirst();
        cursor.close();
        db.close();
        Log.d(TAG, "Challenge " + challengeName + " followed by " + email + ": " + isFollowed);
        return isFollowed;
    }


    public void verifyChallengeImages() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHALLENGES, new String[]{COL_CHALLENGE_NAME, COL_IMAGE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String challengeName = cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_NAME));
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));
            int resId = dbContext.getResources().getIdentifier(imageName, "drawable", dbContext.getPackageName());
            Log.d(TAG, "Challenge: " + challengeName + ", Image: " + imageName + ", Resource ID: " + resId);
            if (resId == 0) {
                Log.w(TAG, "Invalid image resource for challenge: " + challengeName + ", image: " + imageName);
            }
        }
        cursor.close();
        db.close();
    }

    public ArrayList<Map<String, String>> getFollowedChallenges(String email) {
        if (email == null) {
            Log.e(TAG, "getFollowedChallenges: Email is null");
            return new ArrayList<>();
        }
        email = email.toLowerCase();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Map<String, String>> challengesList = new ArrayList<>();

        String query = "SELECT uc." + COL_CHALLENGE_NAME + ", c." + COL_CHALLENGE_DESC + ", c." + COL_IMAGE + ", c." + COL_CHALLENGE_DIET_TYPE +
                " FROM " + TABLE_USER_CHALLENGES + " uc" +
                " JOIN " + TABLE_CHALLENGES + " c ON uc." + COL_CHALLENGE_NAME + " = c." + COL_CHALLENGE_NAME +
                " WHERE uc." + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        try {
            while (cursor.moveToNext()) {
                Map<String, String> challenge = new HashMap<>();
                challenge.put(COL_CHALLENGE_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_NAME)));
                challenge.put(COL_CHALLENGE_DESC, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_DESC)));
                challenge.put(COL_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
                challenge.put(COL_CHALLENGE_DIET_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COL_CHALLENGE_DIET_TYPE)));
                challengesList.add(challenge);
            }
        } catch (Exception e) {
            Log.e(TAG, "getFollowedChallenges: Error fetching followed challenges for email: " + email, e);
        } finally {
            cursor.close();
            db.close();
        }

        Log.d(TAG, "getFollowedChallenges: Email: " + email + ", Challenges count: " + challengesList.size());
        return challengesList;
    }

}

