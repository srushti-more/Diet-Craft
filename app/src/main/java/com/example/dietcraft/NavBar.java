package com.example.dietcraft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavBar extends AppCompatActivity {

    private static final String TAG = "NavBar";
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private String email;
    private String username;
    private String dietPref;
    private int age;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "Starting NavBar onCreate");
            setContentView(R.layout.activity_nav_bar);

            // Verify root view ID
            if (findViewById(R.id.navbar) == null) {
                throw new IllegalStateException("Root view ID 'navbar' not found in activity_nav_bar.xml");
            }
            Log.d(TAG, "Root view ID 'navbar' found");

            // Retrieve Intent extras
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            username = intent.getStringExtra("username");
            dietPref = intent.getStringExtra("diet_preference");
            age = intent.getIntExtra("age", 0);
            gender = intent.getStringExtra("gender");
            Log.d(TAG, "Intent extras retrieved: email=" + email + ", username=" + username + ", dietPref=" + dietPref + ", age=" + age + ", gender=" + gender);

            // Normalize and fallback to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            if (email == null || email.isEmpty()) {
                email = prefs.getString("email", "");
                Log.w(TAG, "Email not in Intent, using SharedPreferences: " + email);
            }
            email = email != null ? email.toLowerCase() : "";
            username = username != null ? username : prefs.getString("username", "");
            dietPref = dietPref != null ? dietPref : prefs.getString("diet_preference", "");
            gender = gender != null ? gender : prefs.getString("gender", "");
            Log.d(TAG, "Normalized extras: email=" + email + ", username=" + username + ", diet=" + dietPref + ", age=" + age + ", gender=" + gender);

            // Validate email
            if (email.isEmpty()) {
                Log.e(TAG, "Email is empty, redirecting to LoginActivity");
                Toast.makeText(this, "Error: User email not found", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Verify BottomNavigationView
            bottomNavigationView = findViewById(R.id.bottom_nav);
            if (bottomNavigationView == null) {
                throw new IllegalStateException("BottomNavigationView ID 'bottom_nav' not found in activity_nav_bar.xml");
            }
            Log.d(TAG, "BottomNavigationView ID 'bottom_nav' found");

            // Verify FragmentContainerView
            if (findViewById(R.id.nav_host_fragment) == null) {
                throw new IllegalStateException("FragmentContainerView ID 'nav_host_fragment' not found in activity_nav_bar.xml");
            }
            Log.d(TAG, "FragmentContainerView ID 'nav_host_fragment' found");

            // Set up Navigation
            Log.d(TAG, "Setting up NavController");
            try {
                // Use NavHost to get NavController safely
                NavHost navHost = (NavHost) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHost == null) {
                    throw new IllegalStateException("NavHostFragment not found for ID 'nav_host_fragment'");
                }
                navController = navHost.getNavController();
                Log.d(TAG, "NavController initialized");
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
                Log.d(TAG, "NavigationUI setup completed");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to initialize NavController: ", e);
                Toast.makeText(this, "Navigation setup error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Pass arguments to navigation graph
            Bundle args = new Bundle();
            args.putString("email", email);
            args.putString("username", username);
            args.putString("diet_preference", dietPref);
            args.putInt("age", age);
            args.putString("gender", gender);
            args.putString("selected_date", prefs.getString("selected_date", null));
            Log.d(TAG, "Bundle args prepared: " + args.toString());

            if (savedInstanceState == null) {
                try {
                    Log.d(TAG, "Setting navigation graph");
                    navController.setGraph(R.navigation.main_nav, args);
                    bottomNavigationView.setSelectedItemId(R.id.menu_home);
                    Log.d(TAG, "Navigation graph set with args: email=" + email + ", username=" + username);
                } catch (Exception e) {
                    Log.e(TAG, "Error setting navigation graph: ", e);
                    Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return;
                }
            }

            // Handle BottomNavigationView item selections
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                // Prevent re-navigation to current destination
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == itemId) {
                    Log.d(TAG, "Already on destination: " + itemId);
                    return true;
                }

                try {
                    Bundle newArgs = new Bundle();
                    newArgs.putString("email", email);
                    newArgs.putString("username", username);
                    newArgs.putString("diet_preference", dietPref);
                    newArgs.putInt("age", age);
                    newArgs.putString("gender", gender);
                    newArgs.putString("selected_date", prefs.getString("selected_date", null));

                    if (itemId == R.id.menu_home) {
                        navController.navigate(R.id.menu_home, newArgs);
                        return true;
                    } else if (itemId == R.id.menu_recipe) {
                        navController.navigate(R.id.menu_recipe, newArgs);
                        return true;
                    } else if (itemId == R.id.menu_challenge) {
                        navController.navigate(R.id.menu_challenge, newArgs);
                        return true;
                    } else if (itemId == R.id.menu_profile) {
                        navController.navigate(R.id.menu_profile, newArgs);
                        return true;
                    }
                    Log.w(TAG, "Unknown item selected: " + itemId);
                    return false;
                } catch (Exception e) {
                    Log.e(TAG, "Navigation error for item " + itemId + ": ", e);
                    Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing NavBar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}




