package com.diamondsoftware.android.sunriver_av_3_0;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

public class PushNotificationsManager extends Service {
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "PushNotificationsManager";
    private GoogleApiClient client;
    private boolean mDeviceIsSupportedInPlayServices=true;

    public PushNotificationsManager() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: 12/30/2015 Perhaps I should check if things are already started
        /*
        boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        if (!sentToken) {
         */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(GlobalState.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(PushNotificationsManager.this, "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PushNotificationsManager.this, "Failure", Toast.LENGTH_LONG).show();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GlobalState.REGISTRATION_COMPLETE));

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent2 = new Intent(this, RegistrationIntentService.class);
            startService(intent2);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        return START_STICKY;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            mDeviceIsSupportedInPlayServices=false;
            return false;
        } else {
            mDeviceIsSupportedInPlayServices = true;
            return true;
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
