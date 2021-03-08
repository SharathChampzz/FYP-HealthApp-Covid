package com.example.geofencing_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("my-contacts/" + StorageClass.phno +
            "/" + date);

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
//        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        Location location = geofencingEvent.getTriggeringLocation(); // Our location
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
//        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
//                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
            // Send notification details as a String
            Log.d(TAG, geofenceTransitionDetails);
            String title = "Health App";
            if(geofenceTransitionDetails.contains("covid")){
                title = "Covid Area Alert";
                if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
                    geofenceTransitionDetails = "Entering inside Covid Area";
                else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
                    geofenceTransitionDetails = "Exiting from Covid Area";
                else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL )
                    geofenceTransitionDetails = "Dwelling Inside Covid Area";
            }
            else if(geofenceTransitionDetails.contains("home")){
                title = "Home Alert";
                if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
                    geofenceTransitionDetails = "Going Out? Take Mask";
            }
            else{
                if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ){
                    String friend = geofenceTransitionDetails.substring(geofenceTransitionDetails.lastIndexOf(" ")+1);
//                    Toast.makeText(context, friend + " is near you!", Toast.LENGTH_SHORT).show();
                    geofenceTransitionDetails = friend + " is near you!";
                    LocationHelper object = new LocationHelper(latitude, longitude);
                    ref.child(friend).setValue(object);
                }
            }
            Toast.makeText(context, geofenceTransitionDetails, Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification(title, geofenceTransitionDetails, MapsActivity.class);
//            sendNotification(geofenceTransitionDetails);

        }

        /*
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        Location location = geofencingEvent.getTriggeringLocation(); // Our location
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId()); //can  loop through all geofences
            String geofenceid = String.valueOf(geofence.getRequestId());
            if(!geofenceid.equals(StorageClass.phno)){
                Toast.makeText(context, geofence.getRequestId() + " is near you!", Toast.LENGTH_SHORT).show();
                LocationHelper object = new LocationHelper(latitude, longitude);
                ref.child(geofenceid).setValue(object);
            }
        }


//        Toast.makeText(context,"Lat:" + latitude + ", Long: " + longitude, Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Lat:" + latitude + ", Long: " + longitude );
        int transitionType = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "onReceive: " + transitionType);

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"GEOFENCE_TRANSITION_ENTER");
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"GEOFENCE_TRANSITION_DWELL" );
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"GEOFENCE_TRANSITION_EXIT" );
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }   */
    }

    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL )
            status = "Dwelling Inside ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }



}