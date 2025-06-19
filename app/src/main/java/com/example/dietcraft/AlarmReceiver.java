package com.example.dietcraft;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        String meal = intent.getStringExtra("meal");
        String channelId = intent.getStringExtra("channel_id");

        // Create notification
        Intent notificationIntent = new Intent(context, ReminderActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle(meal + " Time")
                .setContentText("It's time for your " + meal.toLowerCase() + "!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        // Reschedule for next day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("hour", 0));
        calendar.set(Calendar.MINUTE, intent.getIntExtra("minute", 0));
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent newIntent = new Intent(context, AlarmReceiver.class);
        newIntent.putExtra("meal", meal);
        newIntent.putExtra("channel_id", channelId);
        newIntent.putExtra("hour", calendar.get(Calendar.HOUR_OF_DAY));
        newIntent.putExtra("minute", calendar.get(Calendar.MINUTE));
        newIntent.putExtra("requestCode", intent.getIntExtra("requestCode", 0));

        PendingIntent newPendingIntent = PendingIntent.getBroadcast(
                context, intent.getIntExtra("requestCode", 0), newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Check permission for exact alarms (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                return; // Silently fail, as we can't prompt user
            }
            try {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        newPendingIntent
                );
            } catch (SecurityException e) {
                // Silently fail
            }
        }
    }
}