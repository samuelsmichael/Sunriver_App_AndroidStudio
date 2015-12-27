package com.diamondsoftware.android.sunriver_av_3_0;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;


import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    Intent broadcastIntent = new Intent().addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
    protected static final String TAG = "GeofenceTransitions";

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            int transitionType = geoFenceEvent.getGeofenceTransition();
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)||
                    (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
                if (MainActivity.mSingleton != null) {
                    Context context = null;
                    String locationDescription = "";
                    // Post a notification
                    List<Geofence> geofences = geoFenceEvent
                            .getTriggeringGeofences();
                    String[] geofenceIds = new String[geofences.size()];
                    String jdId = "0";
                    for (int index = 0; index < geofences.size(); index++) {
                        jdId = geofences.get(index).getRequestId();
                        geofenceIds[index] = jdId;
	           	/*
	        	 * If my main activity happens to still be in memory, I can fetch the name
	        	 * of the location; otherwise, I have to show a general message. I wrap it
	        	 * in a try catch, too, as we don't need the name that badly
	        	 */
                        try {

                            context = MainActivity.mSingleton.getApplicationContext();
                            locationDescription = MainActivity.mSingleton.findFirstItemLocationWhoseIdIs(jdId).getmName();
                        } catch (Exception ee) {
                        }
                    }
                    String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,
                            geofenceIds);
                    int locationId = 0;
                    try {
                        locationId = Integer.valueOf(jdId);
                    } catch (Exception ee3) {
                    }
                    ItemLocation theItemToNotify = MainActivity.mSingleton.findFirstItemLocationWhoseIdIs(String.valueOf(locationId));
                    if (theItemToNotify.ismIsGPSPopup()) {
                        sendNotification(transitionType, locationDescription, locationId,context);
                    }

                    // Create an Intent to broadcast to the app
                    broadcastIntent
                            .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                            .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
                            .putExtra(GeofenceUtils.EXTRA_GEOFENCE_ID, ids)
                            .putExtra(GeofenceUtils.EXTRA_GEOFENCE_TRANSITION_TYPE,
                                    transitionType);

                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(broadcastIntent);

                    // Log the transition type and a message
                    Log.d(GeofenceUtils.APPTAG, transitionType + ": " + ids);
                    Log.d(GeofenceUtils.APPTAG,
                            context.getString(R.string.geofence_transition_notification_text));

                    // In debug mode, log the result
                    Log.d(GeofenceUtils.APPTAG, "transition");
                }
            }
        }
    }


    /**
     * Posts a notification in the notification bar when a transition is
     * detected. If the user clicks the notification, control goes to the main
     * Activity.
     *
     * @param transition
     *            The type of transition that occurred.
     *
     */

    private void sendNotification(int transition, String locationName, int theIdOfLocation, Context context) {

        // Create an explicit content Intent that starts the main Activity
        Intent notificationIntent =
                new Intent(context,SplashPage.class); // not sure about this: .putExtra("GoToMaps", true);

        // Construct a task stack
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);

        // Adds the main Activity to the task stack as the parent
        stackBuilder.addParentStack(SplashPage.class);

        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Set the notification contents
        String buildingContentText="Click notification to see more details";
        if(locationName!=null && locationName!="") {
            buildingContentText+=" for";
        }

        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.geofence_transition_notification_titletii))
                .setContentText(buildingContentText)
                .setContentIntent(notificationPendingIntent);
        if(locationName!=null && locationName!="") {
            builder.setSubText(locationName);
        }


        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Issue the notification
            mNotificationManager.notify(theIdOfLocation, builder.build());
        } else {
            if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                mNotificationManager.cancel(theIdOfLocation);
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

}
