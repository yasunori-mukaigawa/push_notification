/*
  Copyright 2020 YASUNORI MUKAIGAWA LIMITED. All Rights Reserved.
 */

package com.android.pushtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static String channelId = "Test";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = createNotification(messageBody, notificationManager);

        notificationManager.notify(0, builder.build());
    }

    /**
     * Create notificationnoticationChannel
     *
     * @param messageBody FCM message body received.
     * @param notificationManager NotificationManager
     * @return created NotificationCompat.Builder
     */
    private NotificationCompat.Builder createNotification(String messageBody, NotificationManager notificationManager){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.star_focus)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        setSound(notificationManager, notificationBuilder);

        return notificationBuilder;
    }

    /**
     * set notification Sound
     *
     * @param notificationManager NotificationManager
     * @param builder NotificationCompat.Builder
     */
    private void setSound(NotificationManager notificationManager, NotificationCompat.Builder builder){
        // Set the default sound as the ringtone
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, sound);
        } else {
            builder.setSound(sound);
        }
    }

    /**
     * create notification Channel
     *
     * @param notificationManager NotificationManager
     * @param sound default Sound Uri
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(NotificationManager notificationManager, Uri sound){
        NotificationChannel channel;
        channel = new NotificationChannel(channelId,
                "notification",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("notification");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(sound, audioAttributes);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
