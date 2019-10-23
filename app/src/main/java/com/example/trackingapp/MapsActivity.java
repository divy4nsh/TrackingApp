package com.example.trackingapp;

import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Double templatitude=30.7398;
    Double templongitude=76.7827;
    DatabaseReference globalDatabaseReference;
    ArrayList<String> uidList=new ArrayList<>();
    ArrayList<String> displayNameList=new ArrayList<>();
    ArrayList<Location> shopLocations=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapsInitializer.initialize(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        globalDatabaseReference=FirebaseDatabase.getInstance("https://testproject-9e3d2.firebaseio.com/").getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        globalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uid : dataSnapshot.getChildren())
                {
                    if(uid.getKey().equals("userData")||uid.getKey().equals("shopData"))
                        continue;
                    uidList.add(uid.getKey());
                    displayNameList.add((String) dataSnapshot.child("userData").child(uid.getKey()).getValue());
                    mapUpdate();
                }
                mapUpdate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });//makes a list of all UIDs

    }
    public void addUserMarker(LatLng latLng, String displayName)
    {
        MarkerOptions options =new MarkerOptions()
                .title(displayName)
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options.position(latLng)).showInfoWindow();
    }
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        marker.getPosition(), mMap.getCameraPosition().zoom),500, null);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),mMap.getCameraPosition().zoom));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom+5), 1000, null);
                    }
                }, 500);
                return true;
            }
        });
        mapUpdate();
    }
    public void mapUpdate(){
        mMap.clear();
        for(final String uid : uidList)
        {
            globalDatabaseReference.limitToLast(10).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot lastLocation=null;
                    for(DataSnapshot location : dataSnapshot.child(uid).getChildren())
                    {
                        lastLocation=location;
                    }
                    if(lastLocation.child("longitude").getValue()==null||lastLocation.child("latitude").getValue()==null)
                        return;
                    templongitude=Double.valueOf(lastLocation.child("longitude").getValue().toString());
                    templatitude=Double.valueOf(lastLocation.child("latitude").getValue().toString());
                    addUserMarker(new LatLng(templatitude,templongitude), (String) dataSnapshot.child("userData").child(uid).getValue());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public void addShopMarker(LatLng latLng, String shopName)
    {
        mMap.clear();
        MarkerOptions options =new MarkerOptions()
                .title(shopName)
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options.position(latLng)).showInfoWindow();
    }
}