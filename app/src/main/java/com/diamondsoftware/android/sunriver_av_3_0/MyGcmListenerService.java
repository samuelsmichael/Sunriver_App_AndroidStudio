package com.diamondsoftware.android.sunriver_av_3_0;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String contentTitle = data.getString("contentTitle");
        String emergencyMapURL=data.getString("emergencyMapURL");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        String topic="";
        if (from.startsWith("/topics/")) {
            // message received from some topic.
            topic=from.replace("/topics/","");
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        SharedPreferences sharedPreferences1 =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences mSharedPreferences=getSharedPreferences(
                getApplicationContext().getPackageName() + "_preferences"
                , Activity.MODE_PRIVATE);
        boolean doAlerts=mSharedPreferences.getBoolean("deliver_sunriver_alerts", true);
        boolean doEmergencies=mSharedPreferences.getBoolean("deliver_sunriver_emergencies",true);
        boolean doNewsFeeds=mSharedPreferences.getBoolean("deliver_sunriver_newsfeeds",false);
        if(
                ((topic.equals("alert") || topic.equals("alerttest")  || topic.equals("alerttestinternal")) && doAlerts)
                || ((topic.equals("emergency") || topic.equals("emergencytest") || topic.equals("emergencytestinternal")) && doEmergencies)
                || ((topic.equals("newsfeed") || topic.equals("newsfeedtest")|| topic.equals("newsfeedtestinternal")) && doNewsFeeds)
                ) {

            /**
             * Show a notification indicating to the user
             * that a message was received.
             */
            sendNotification(message, contentTitle, topic, emergencyMapURL);
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String contentTitle, String topic, String emergencyMapURL) {
        Intent intent=null;
        if(MainActivity.mSingleton==null) { //we're not in memory
            intent = new Intent(this, SplashPage.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("PushNotificationMessage", message);
        intent.putExtra("PushNotificationTitle",contentTitle);
        intent.putExtra("PushNotificationTopic",topic);
        intent.putExtra("PushNotificationEmergencyMapURL",emergencyMapURL);
        intent.setAction("PushNotification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sunriverlogoopaque)
                .setContentTitle(contentTitle)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setShowWhen(true)

                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
