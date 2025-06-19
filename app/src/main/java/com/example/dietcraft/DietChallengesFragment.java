package com.example.dietcraft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Map;

public class DietChallengesFragment extends Fragment {

    private String dietPref;
    private String email;
    private String username;
    private DatabaseHelper db;
    private LinearLayout challengesList;
    private static final String TAG = "DietChallengesFragment";
    private static final String COL_CHALLENGE_NAME = "challenge_name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(requireContext());
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email", "").toLowerCase();
            username = args.getString("username", "");
            dietPref = args.getString("diet_preference");
        }
        if (dietPref == null || dietPref.isEmpty()) {
            dietPref = db.getDietPreference(email);
        }
        Log.d(TAG, "Initialized with email: " + email + ", username: " + username + ", diet: " + dietPref);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_challenges, container, false);
        challengesList = view.findViewById(R.id.challenges_list);
        if (challengesList == null) {
            Log.e(TAG, "Challenges list LinearLayout not found in layout");
//            Toast.makeText(requireContext(), "Error loading challenges layout", Toast.LENGTH_SHORT).show();
            return view;
        }
        loadChallenges();
        return view;
    }

    private void loadChallenges() {
        if (dietPref == null || dietPref.isEmpty()) {
            Log.w(TAG, "Diet preference is null or empty for email: " + email);
//            Toast.makeText(requireContext(), "Please select a diet preference", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Map<String, String>> challengeList = db.getChallenges(dietPref);
        String[] challenges = new String[challengeList.size()];
        for (int i = 0; i < challengeList.size(); i++) {
            challenges[i] = challengeList.get(i).get(COL_CHALLENGE_NAME);
            if (challenges[i] == null) {
                Log.w(TAG, "Null challenge name at index " + i + " for diet: " + dietPref);
                challenges[i] = "Unknown Challenge";
            }
        }
        Log.d(TAG, "Loaded " + challenges.length + " challenges for diet: " + dietPref);
        for (String challenge : challenges) {
            Log.d(TAG, "Challenge: " + challenge);
        }

        if (challenges.length == 0) {
            Log.w(TAG, "No challenges found for diet: " + dietPref);
            TextView noChallengesText = new TextView(requireContext());
            noChallengesText.setText("No challenges available for " + dietPref);
            noChallengesText.setTextSize(16);
            noChallengesText.setPadding(16, 16, 16, 16);
            challengesList.removeAllViews();
            challengesList.addView(noChallengesText);
            return;
        }

        challengesList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int cardCount = 0;
        for (int i = 0; i < challengeList.size(); i++) {
            Map<String, String> challengeData = challengeList.get(i);
            String challenge = challengeData.get(COL_CHALLENGE_NAME);
            String imageName = challengeData.get("image");

            View challengeCard = inflater.inflate(R.layout.challenge_card, challengesList, false);
            ImageView image = challengeCard.findViewById(R.id.challenge_image);
            TextView name = challengeCard.findViewById(R.id.challenge_name);
            TextView desc = challengeCard.findViewById(R.id.challenge_desc);
            Button followButton = challengeCard.findViewById(R.id.follow_button);

            if (image == null || name == null || desc == null || followButton == null) {
                Log.e(TAG, "Challenge card view components missing for challenge: " + challenge);
                continue;
            }

            if (imageName != null && !imageName.isEmpty()) {
                int resId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());
                if (resId != 0) {
                    image.setImageResource(resId);
                } else {
                    Log.w(TAG, "Drawable resource not found for challenge: " + challenge + ", image: " + imageName);
                    image.setImageResource(R.drawable.recipedetail);
                }
            } else {
                Log.w(TAG, "Image name is null or empty for challenge: " + challenge);
                image.setImageResource(R.drawable.recipedetail);
            }

            name.setText(challenge);
            desc.setText(db.getChallengeDescription(challenge));

            boolean isFollowed = db.isChallengeFollowed(email, challenge);
            updateButtonState(followButton, isFollowed);

            followButton.setOnClickListener(v -> {
                String buttonText = followButton.getText().toString();
                if (buttonText.equals("Follow")) {
                    boolean success = db.addFollowedChallenge(email, challenge, dietPref);
                    if (success) {
                        Toast.makeText(requireContext(), "Following " + challenge, Toast.LENGTH_SHORT).show();
                        updateButtonState(followButton, true);
                        // Broadcast update
                        Intent intent = new Intent("CHALLENGE_UPDATED");
                        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                    } else {
                        Toast.makeText(requireContext(), "Failed to follow " + challenge, Toast.LENGTH_SHORT).show();
                    }
                } else if (buttonText.equals("Unfollow")) {
                    boolean success = db.removeFollowedChallenge(email, challenge);
                    if (success) {
                        Toast.makeText(requireContext(), "Unfollowed " + challenge, Toast.LENGTH_SHORT).show();
                        updateButtonState(followButton, false);
                        // Broadcast update
                        Intent intent = new Intent("CHALLENGE_UPDATED");
                        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                    } else {
                        Toast.makeText(requireContext(), "Failed to unfollow " + challenge, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            challengeCard.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), ChallengeDetailActivity.class);
                intent.putExtra("challenge_name", challenge);
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                startActivity(intent);
                Log.d(TAG, "Started ChallengeDetailActivity for challenge: " + challenge);
            });

            challengesList.addView(challengeCard);
            cardCount++;
        }
        Log.d(TAG, "Displayed " + cardCount + " challenge cards for diet: " + dietPref);

        if (cardCount == 0) {
            Log.w(TAG, "No challenge cards were added to the UI for diet: " + dietPref);
            TextView noChallengesText = new TextView(requireContext());
            noChallengesText.setText("Unable to display challenges for " + dietPref);
            noChallengesText.setTextSize(16);
            noChallengesText.setPadding(16, 16, 16, 16);
            challengesList.addView(noChallengesText);
        }
    }

    private void updateButtonState(Button button, boolean isFollowed) {
        if (isFollowed) {
            button.setText("Unfollow");
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_light));
        } else {
            button.setText("Follow");
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String updatedDietPref = db.getDietPreference(email);
        if (!updatedDietPref.equals(dietPref)) {
            Log.d(TAG, "Diet preference changed from " + dietPref + " to " + updatedDietPref);
            dietPref = updatedDietPref;
            loadChallenges();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        Log.d(TAG, "Database closed");
    }
}