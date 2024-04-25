package com.example.parkwise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context,"Geofence triggered...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: Geofence triggered....");

        NotificationHelper notificationHelper = new NotificationHelper(context);
//        notificationHelper.sendHighPriorityNotificationToOpenFragment("ParkWise Parking", "You have entered CSUN! Click here to pay for parking!", Payment.class);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        assert geofencingEvent != null;
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.d(TAG, errorMessage);
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "ParkWise Parking", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotificationToOpenFragment("You're Entering CSUN! Click here to pay for parking.", "", Payment.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "ParkWise Parking", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotificationToOpenFragment("Don't forget to pay for parking! Click here to pay.", "", Payment.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "ParkWise Parking", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotificationToOpenFragment("Thank you for the visit! Hope to see you again soon!", "", Payment.class);
                break;
        }


    }
}