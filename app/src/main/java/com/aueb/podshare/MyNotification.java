package com.aueb.podshare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aueb.podshare.classes.Episode;

public class MyNotification {

    public static final String CHANNEL_ID = "";

    public static Notification notification;

    public static void createNotification(Context context, Episode episode, int play_button, int pos, int size) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.play);

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {

            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.pause)
                    .setContentTitle(episode.get_name())
                    .setContentText("publisher")
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1,notification);
        }
    }
}
