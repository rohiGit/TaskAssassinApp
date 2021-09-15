package com.example.taskassassin;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotificationPublisher extends BroadcastReceiver {
    int id = (int) System.currentTimeMillis();
    public static String NOTIFICATION = "notification";
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.print("entered notify");
        NotificationManagerCompat notificationManager =    NotificationManagerCompat.from(context.getApplicationContext());
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        notificationManager.notify(id, notification);
    }
}
