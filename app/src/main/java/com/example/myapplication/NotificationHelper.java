package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class NotificationHelper {

    public static final String CHANNEL_ID_HIGH_PRIORITY = "high_priority_channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el canal de alta prioridad
            CharSequence name = "High Priority Channel";
            String description = "Channel for high priority notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            Log.d("NotificationHelper", "Creating notification channel...");
            Log.d("NotificationHelper", "Channel ID: " + CHANNEL_ID_HIGH_PRIORITY);
            Log.d("NotificationHelper", "Channel name: " + name);
            Log.d("NotificationHelper", "Channel description: " + description);
            Log.d("NotificationHelper", "Channel importance: " + importance);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_HIGH_PRIORITY, name, importance);
            channel.setDescription(description);

            // Registrar el canal en el sistema
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
