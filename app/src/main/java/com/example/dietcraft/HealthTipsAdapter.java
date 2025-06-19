package com.example.dietcraft;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.TipViewHolder> {
    private static final String TAG = "HealthTipsAdapter";
    private final List<HealthTip> tips;

    public HealthTipsAdapter(List<HealthTip> tips) {
        this.tips = tips;
        Log.d(TAG, "Adapter initialized with " + tips.size() + " tips");
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        HealthTip tip = tips.get(position);
        Log.d(TAG, "Binding tip: " + tip.title);
        holder.categoryText.setText(tip.category);
        holder.titleText.setText(tip.title);
        holder.descriptionText.setText(tip.description);
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, titleText, descriptionText;

        TipViewHolder(View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.tip_category);
            titleText = itemView.findViewById(R.id.tip_title);
            descriptionText = itemView.findViewById(R.id.tip_description);
        }
    }
}


//package com.example.dietcraft;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//
//public class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.TipViewHolder> {
//
//    private List<HealthTip> tips;
//
//    public HealthTipsAdapter(List<HealthTip> tips) {
//        this.tips = tips;
//    }
//
//    @NonNull
//    @Override
//    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_tip, parent, false);
//        return new TipViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
//        HealthTip tip = tips.get(position);
//        holder.categoryTextView.setText(tip.category);
//        holder.titleTextView.setText(tip.title);
//        holder.descriptionTextView.setText(tip.description);
//    }
//
//    @Override
//    public int getItemCount() {
//        return tips.size();
//    }
//
//    static class TipViewHolder extends RecyclerView.ViewHolder {
//        TextView categoryTextView;
//        TextView titleTextView;
//        TextView descriptionTextView;
//
//        TipViewHolder(@NonNull View itemView) {
//            super(itemView);
//            categoryTextView = itemView.findViewById(R.id.tip_category);
//            titleTextView = itemView.findViewById(R.id.tip_title);
//            descriptionTextView = itemView.findViewById(R.id.tip_description);
//        }
//    }
//}