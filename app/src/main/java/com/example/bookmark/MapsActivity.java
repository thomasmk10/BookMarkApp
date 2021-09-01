package com.example.bookmark;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.List;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerX;
    Button signout, messages, addlocs, go_to_NYC;
    ArrayList<LatLng>arrayList = new ArrayList<LatLng>();
    //Statue of Liberty
    LatLng statue = new LatLng(40.69051733561877, -74.04488143082702);
    //Empire State Building
    LatLng ESB = new LatLng(40.749043988024745, -73.9855317147652);
    //Chrysler Building
    LatLng CB = new LatLng(40.75316665349052, -73.97470482712367);
    //Charging Bull
    LatLng cBull = new LatLng(40.70611902329508, -74.0133766878832);
    //World Trade Center
    LatLng WTC = new LatLng(40.71287287624352, -74.01343611006664);
    //for markers (title and description)
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> description = new ArrayList<String>();

//    ArrayList<BitmapDescriptor> icon = new ArrayList<BitmapDescriptor>();
//
//    BitmapDescriptor icon_statue = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_charging_bull);
//    BitmapDescriptor icon_esb = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_charging_bull);
//    BitmapDescriptor icon_cb = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_charging_bull);
//    BitmapDescriptor icon_cbull = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_charging_bull);
//    BitmapDescriptor icon_wtc = bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_charging_bull);



    //private LocationRequest locationRequest;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap= Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        arrayList.add(statue);
        arrayList.add(ESB);
        arrayList.add(CB);
        arrayList.add(cBull);
        arrayList.add(WTC);

        title.add("Statue of Liberty");
        title.add("Empire State Building");
        title.add("Chrysler Building");
        title.add("Charging Bull");
        title.add("World Trade Center");

        description.add("Location:" +
                "\nNew York, NY 10004" + "\n\nThe Statue of Liberty is a colossal neoclassical sculpture on Liberty Island in New York Harbor within New York City, in the United States.");
        description.add("Location:" +
                "\n20 W 34th St, New York, NY 10001" +"\n\nThe Empire State Building is a 102-story Art Deco skyscraper in Midtown Manhattan in New York City, United States. It was designed by Shreve, Lamb & Harmon and built from 1930 to 1931. Its name is derived from \"Empire State\", the nickname of the state of New York.");
        description.add("Location:" +
                "\n405 Lexington Ave, New York, NY 10174" +"\n\nThe Chrysler Building is an Art Deco skyscraper in the Turtle Bay neighborhood on the East Side of Manhattan, New York City, at the intersection of 42nd Street and Lexington Avenue near Midtown Manhattan.");
        description.add("Location:" +
                "\nNew York, NY 10004" +"\n\nCharging Bull, sometimes referred to as the Wall Street Bull or the Bowling Green Bull, is a bronze sculpture that stands on Broadway just north of Bowling Green in the Financial District of Manhattan in New York City. ");
        description.add("Location:" +
                "\n285 Fulton St, New York, NY 10007" +"\n\nOne World Trade Center is the main building of the rebuilt World Trade Center complex in Lower Manhattan, New York City. One WTC is the tallest building in the United States, the tallest building in the Western Hemisphere, and the sixth-tallest in the world.");

        signout = (Button) findViewById(R.id.sign_outButton);
        messages = (Button) findViewById(R.id.messages_button);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, Login_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        addlocs = (Button) findViewById(R.id.addMonuments);

        addlocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, AddLocationReview_Activity.class);
                startActivity(i);
            }
        });
        go_to_NYC = (Button) findViewById(R.id.goToNYC);

        go_to_NYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a LatLngBounds that includes the city of Adelaide in Australia.
                LatLngBounds NYC = new LatLngBounds(
                        new LatLng( 40.68392799015035,-74.04728500751165), // SW bounds
                        new LatLng( 40.87764500765852, -73.91058699000139)  // NE bounds
                );

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(NYC.getCenter(), 12));

            }
        });


//        String[] colorsTxt = getApplicationContext().getResources().getStringArray(R.array.colors);
//        List<Integer> colors = new ArrayList<Integer>();
//        for (int i = 0; i < colorsTxt.length; i++) {
//            int newColor = Color.parseColor(colorsTxt[i]);
//            colors.add(newColor);
//        }
//        icon.add(icon_statue);
//        icon.add(icon_esb);
//        icon.add(icon_cb);
//        icon.add(icon_cbull);
//        icon.add(icon_wtc);
    }

    @Override
    public void onMapClick(LatLng point) {
        //   mTapTextView.setText("tapped, point=" + point);
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
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        Toast.makeText(MapsActivity.this, "Click on Info Window to Hide", Toast.LENGTH_LONG).show();

        UiSettings mUiSettings = mMap.getUiSettings();
        for (int i = 0;i<arrayList.size();i++){
            for (int j = 0; j<title.size(); j++){
                for (int k = 0; k<description.size(); k++) {
                    markerX =mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title(String.valueOf(title.get(i))).snippet(String.valueOf(description.get(i))));

//                      for custom markers

//                    for (int l = 0; l<icon.size(); l++) {
//                        mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title(String.valueOf(title.get(i))).snippet(String.valueOf(description.get(i))).icon(icon.get(i)));
//                    }
                }
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
        }
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markertitle = marker.getTitle();
                String markerdescription = marker.getSnippet();

                Intent i = new Intent(MapsActivity.this, DetailsActivity.class);
                i.putExtra("title", markertitle);
                i.putExtra("description", markerdescription);
                startActivity(i);
                return false;
            }
        });

        //Create a LatLngBounds that includes the city of Adelaide in Australia.
        LatLngBounds NYC = new LatLngBounds(
                new LatLng( 40.68392799015035,-74.04728500751165), // SW bounds
                new LatLng( 40.87764500765852, -73.91058699000139)  // NE bounds
        );

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NYC.getCenter(), 11));


        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        //mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setIndoorLevelPickerEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, 100);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
}
