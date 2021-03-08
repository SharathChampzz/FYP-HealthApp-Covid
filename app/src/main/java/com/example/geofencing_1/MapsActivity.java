package com.example.geofencing_1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener
        , GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";
    private static final float GEOFENCE_RADIUS = 50;
    private GoogleMap mMap;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("covidpatients");
    LatLng home = new LatLng(13.799228, 75.728976);
    ArrayList<LatLng> list = new ArrayList<>();

    Spinner spinner;
    private static final String[] paths = {"Choose Actions Here", "Corona Positives Nearby",
            "Show All Home Locations","Delete All Home Locations", "Show Covid Hotspots",
            "Show Nearby Users","Other Feature will be Added Soon!"};

    DatabaseReference homelocation = FirebaseDatabase.getInstance().getReference("homelocations/" + StorageClass.phno);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        list.add(home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);

        //mMap.setOnMapClickListener(this);

    }

    //    @Override
    protected void CovidPatientsNearby() {
//        super.onStart();

        if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @NonNull
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            LocationHelper object = ds.getValue(LocationHelper.class);
                            assert object != null;
                            String lat = object.getLatitude();
                            String lon = object.getLongitude();
//                            Toast.makeText(MapsActivity.this, lat + "," + lon, Toast.LENGTH_SHORT).show();
                            LatLng coordinates = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                            list.add(coordinates);
                        }
//                        Toast.makeText(MapsActivity.this, "Size = " + list.size() , Toast.LENGTH_SHORT).show();
                        //Remove array list and add this for loop inside
                        // You can also show covid patient name from obtained firebase object
//                        if(list.size() > 1)
                        for (LatLng object : list) {
//                            Toast.makeText(MapsActivity.this, "Loc set", Toast.LENGTH_SHORT).show();
                            mMap.addMarker(new MarkerOptions().position(object).title("Alert"));
//                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10F));
//                            MapsActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLng(object));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(object, 10F));
                        }
//                        AdapterClass adapterClass = new AdapterClass(list);
//                        recyclerView.setAdapter(adapterClass);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(this);
//        CovidPatientsNearby();
//        for (LatLng object : list){
            Toast.makeText(MapsActivity.this, "Choose Actions Above to Perform!", Toast.LENGTH_SHORT).show();
//            mMap.addMarker(new MarkerOptions().position(object).title("Alert"));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15F));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(object));
//        }
        // Add a marker in Sydney and move the camera
//        LatLng home = new LatLng(13.799228, 75.728976);
//        mMap.addMarker(new MarkerOptions().position(home).title("Home Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 10F));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected=parent.getItemAtPosition(position).toString();
        mMap.clear();
        if( sSelected.equals("Corona Positives Nearby")){
            CovidPatientsNearby();
            Toast.makeText(this, "Showing Nearby Covid Cases..!", Toast.LENGTH_SHORT).show();
        }
        else if(sSelected.equals("Show All Home Locations")){
            showLocations("home");
            Toast.makeText(this, "Showing all your previously added home locations!", Toast.LENGTH_SHORT).show();
        }
        else if(sSelected.equals("Delete All Home Locations")){
            deleteLocations();
            Toast.makeText(this, "Deleting all your previously added home locations!", Toast.LENGTH_SHORT).show();
        }
        else if(sSelected.equals("Show Covid Hotspots")){
            showLocations("hotspot");
            Toast.makeText(this, "Showing all near by Covid Hotspots!", Toast.LENGTH_SHORT).show();
        }
        else if(sSelected.equals("Show Nearby Users")){
            showLocations("users");
            Toast.makeText(this, "Showing all Nearby Users!", Toast.LENGTH_SHORT).show();
        }
        else if(sSelected.equals("Other Feature will be Added Soon!")){
            Toast.makeText(this, "Thank you for Your Interest😍\nWe will add more features.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLocations(String s) {
        DatabaseReference reference = null;
        switch (s) {
            case "home":
                reference = FirebaseDatabase.getInstance().getReference().child("homelocations/" + StorageClass.phno);
                break;
            case "hotspot":
                reference = FirebaseDatabase.getInstance().getReference().child("covidhotspot");
                break;
            case "users":
                reference = FirebaseDatabase.getInstance().getReference().child("current-locations");
                break;
        }
        
        reference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Log.d(TAG, snap.getKey());
                    if (!snap.getKey().equals(StorageClass.phno)){ // remove later
                        LocationHelper object = snap.getValue(LocationHelper.class);
                        String latitude = object.getLatitude();
                        String longitude = object.getLongitude();
//                    Toast.makeText(getApplicationContext(), latitude + "," + longitude, Toast.LENGTH_SHORT).show();
                        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        addMarker(latlng);
                        addCircle(latlng, GEOFENCE_RADIUS);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void deleteLocations() {
        homelocation.setValue(null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(getApplicationContext(),"Adding this as Home!", Toast.LENGTH_SHORT).show();
        String latitude = String.valueOf(latLng.latitude);
        String longitude = String.valueOf(latLng.longitude);
        addhomelocation(latitude, longitude);
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        builder.setMessage("Do you want to get notified to take Mask while you go out from here?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addhomelocation(latitude, longitude);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Okay! Not adding this location to give you a reminder", Toast.LENGTH_SHORT).show();
                    }
                });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();  */

    }

    private void addhomelocation(String latitude, String longitude) {
        LocationHelper object = new LocationHelper(latitude, longitude);
        homelocation.push().setValue(object);
//        Toast.makeText(getApplicationContext(),"Sucessfully Added I guess!",Toast.LENGTH_SHORT).show();
    }
}