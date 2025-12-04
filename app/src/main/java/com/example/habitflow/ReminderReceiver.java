package com.example.habitflow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "habitflow_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String activityName = intent.getStringExtra("activityName");
        if (activityName == null || activityName.trim().isEmpty()) {
            activityName = "votre activité";
        }

        // Créer le canal si nécessaire (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Rappels d'activités",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications pour les rappels d'activités");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Construire la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Rappel d'activité")
                .setContentText("C’est l’heure pour : " + activityName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Vérifier explicitement la permission
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                // Permission non accordée, ne rien faire
                return;
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = (int) (System.currentTimeMillis() & 0xfffffff);
            notificationManager.notify(notificationId, builder.build());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
