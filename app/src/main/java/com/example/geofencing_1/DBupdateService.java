package com.example.geofencing_1;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class DBupdateService extends Service {

    private static final String TAG = "DBupdateService";
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 50;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    int ID = 1;
    int covhotspot = 0;
    int homeno = 0;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        StorageClass.phno = sharedPreferences.getString(TEXT, "");
    }
    public DBupdateService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        loadData();

        Toast.makeText(getApplicationContext(), "ID = " + StorageClass.phno , Toast.LENGTH_SHORT).show();

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        if(StorageClass.clear_geofence == 1){
            remove(); // Run once in a day
            Toast.makeText(getApplicationContext(),"Removed GeoFences", Toast.LENGTH_SHORT).show();
            StorageClass.clear_geofence = 0;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("current-locations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Changes Found in Current Location!");
                for(DataSnapshot snap: snapshot.getChildren()){
                    Log.d(TAG, snap.getKey());
                    if (!snap.getKey().equals(StorageClass.phno)){
                        LocationHelper object = snap.getValue(LocationHelper.class);
                        LatLng latlng = new LatLng(Double.parseDouble(object.getLatitude()), Double.parseDouble(object.getLongitude()));
                        addGeofence(latlng, GEOFENCE_RADIUS, snap.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        DatabaseReference hotspot = FirebaseDatabase.getInstance().getReference().child("covidhotspot");
        hotspot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Changes Found in Covid Hotspot Location's!");
                for(DataSnapshot snap: snapshot.getChildren()){
                    Log.d(TAG, snap.getKey());
                        LocationHelper object = snap.getValue(LocationHelper.class);
                        LatLng latlng = new LatLng(Double.parseDouble(object.getLatitude()), Double.parseDouble(object.getLongitude()));
                        String HotspotID = "covid - " + covhotspot;
                        addGeofence(latlng, GEOFENCE_RADIUS, HotspotID);
                        covhotspot += 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  Log.e(TAG, error.getMessage());  }
        });

        DatabaseReference homeloc = FirebaseDatabase.getInstance().getReference().child("homelocations/" + StorageClass.phno);
        homeloc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Changes Found in Home Location!");
                for(DataSnapshot snap: snapshot.getChildren()){
                    Log.d(TAG, snap.getKey());
                        LocationHelper object = snap.getValue(LocationHelper.class);
                        LatLng latlng = new LatLng(Double.parseDouble(object.getLatitude()), Double.parseDouble(object.getLongitude()));
                        String HotspotID = "home " + homeno;
                        addGeofence(latlng, GEOFENCE_RADIUS, HotspotID);
                        homeno += 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void addGeofence(LatLng latLng, float radius, String id) {
        GEOFENCE_ID = id;
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added... ID = " + GEOFENCE_ID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.e(TAG, "Failure While Adding Geofence: " + errorMessage);
                    }
                });
    }

    public void remove(){
        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




}