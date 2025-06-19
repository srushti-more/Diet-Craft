package com.example.dietcraft;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.dietcraft.databinding.ActivityHealthTipsBinding;
import java.util.List;

public class HealthTipsActivity extends AppCompatActivity {
    private static final String TAG = "HealthTipsActivity";
    private ActivityHealthTipsBinding binding;
    private HealthTipsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthTipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            // Set title
            binding.healthTipsTitle.setText("Healthy Tips");

            // Set up RecyclerView
            binding.tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<HealthTip> tips = getHealthTips();
            adapter = new HealthTipsAdapter(tips);
            binding.tipsRecyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView set up with " + tips.size() + " tips");

            // Back button functionality
            binding.healthTipsBackButton.setOnClickListener(v -> finish());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            finish();
        }
    }

    private List<HealthTip> getHealthTips() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<HealthTip> tips = db.getHealthTips();
        Log.d(TAG, "Retrieved " + tips.size() + " health tips from database");
        return tips;
    }
}

class HealthTip {
    String category;
    String title;
    String description;

    HealthTip(String category, String title, String description) {
        this.category = category;
        this.title = title;
        this.description = description;
    }
}




//package com.example.dietcraft;
//
//import android.os.Bundle;
//import android.util.Log;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import com.example.dietcraft.databinding.ActivityHealthTipsBinding;
//import java.util.List;
//
//public class HealthTipsActivity extends AppCompatActivity {
//
//    private static final String TAG = "HealthTipsActivity";
//    private ActivityHealthTipsBinding binding;
//    private HealthTipsAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityHealthTipsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        try {
//            // Set title
//            binding.healthTipsTitle.setText("Healthy Tips");
//
//            // Set up RecyclerView
//            binding.tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//            adapter = new HealthTipsAdapter(getHealthTips());
//            binding.tipsRecyclerView.setAdapter(adapter);
//
//            // Back button functionality
//            binding.healthTipsBackButton.setOnClickListener(v -> finish());
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error in onCreate: ", e);
//            finish();
//        }
//    }
//
//    // Retrieve health tips from DatabaseHelper
//    private List<HealthTip> getHealthTips() {
//        DatabaseHelper db = new DatabaseHelper(this);
//        List<HealthTip> tips = db.getHealthTips();
//        Log.d(TAG, "Retrieved " + tips.size() + " health tips from database");
//        return tips;
//    }
//}
//
//// Data class for health tips
//class HealthTip {
//    String category;
//    String title;
//    String description;
//
//    HealthTip(String category, String title, String description) {
//        this.category = category;
//        this.title = title;
//        this.description = description;
//    }
//}
