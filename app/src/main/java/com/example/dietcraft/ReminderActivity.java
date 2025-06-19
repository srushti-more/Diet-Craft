package com.example.dietcraft;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "MealReminderChannel";
    private TimePicker timePickerBreakfast, timePickerLunch, timePickerDinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize TimePickers
        timePickerBreakfast = findViewById(R.id.timePickerBreakfast);
        timePickerLunch = findViewById(R.id.timePickerLunch);
        timePickerDinner = findViewById(R.id.timePickerDinner);

        // Initialize Buttons
        Button btnSetBreakfast = findViewById(R.id.btnSetBreakfast);
        Button btnSetLunch = findViewById(R.id.btnSetLunch);
        Button btnSetDinner = findViewById(R.id.btnSetDinner);

        // Create Notification Channel
        createNotificationChannel();

        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Set Button Listeners
        btnSetBreakfast.setOnClickListener(v -> setReminder("Breakfast", timePickerBreakfast, 1));
        btnSetLunch.setOnClickListener(v -> setReminder("Lunch", timePickerLunch, 2));
        btnSetDinner.setOnClickListener(v -> setReminder("Dinner", timePickerDinner, 3));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Meal Reminder Channel";
            String description = "Channel for meal reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setReminder(String meal, TimePicker timePicker, int requestCode) {
        // Get time from TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Set up calendar with selected time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If time is in the past, schedule for next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Create Intent for AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("meal", meal);
        intent.putExtra("channel_id", CHANNEL_ID);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("requestCode", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Check permission for exact alarms (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in settings to set reminders", Toast.LENGTH_LONG).show();
                Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(settingsIntent);
                return;
            }
            try {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                Toast.makeText(this, meal + " reminder set for " + hour + ":" + String.format("%02d", minute), Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(this, "Failed to set reminder: Exact alarm permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to set reminder: AlarmManager unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. Reminders may not work.", Toast.LENGTH_LONG).show();
            }
        }
    }
}