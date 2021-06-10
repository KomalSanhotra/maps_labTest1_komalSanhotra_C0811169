package com.example.labtest1_komal_c0811169;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.labtest1_komal_c0811169.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker marker;
    private Marker points;
    Polyline line;
    Polygon quadrilateral;
    public static final int Polygon_sides = 4;
    List<Marker> markerList = new ArrayList<>(); 

    LocationManager locationManager;
    LocationListener locationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)); */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                setHomeMarker(location);

            }
        };
        if (!isGrantedPermission())
            requestLocationPermission();
        else
            startUpdateLocation();
       mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
           @Override
           public void onMapLongClick(LatLng latLng) {
               setMarker(latLng);
           }
       });
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,0, locationListener);
    }

    private void requestLocationPermission() {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean isGrantedPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0 , locationListener);
        }
    }



    private void setHomeMarker(Location location) {
        LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(userLoc)
                .title("you are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        marker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15));

    }
    private void setMarker(LatLng latLng){
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Your Point");
        /*if (points != null)
            clearMap();
        points = mMap.addMarker(options);
        drawLine();*/
        if (markerList.size() == Polygon_sides)
            clearMap();
        markerList.add(mMap.addMarker(options));
        if (markerList.size() == Polygon_sides)
            drawShape();
    }

    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x5900FF00)
                .strokeColor(Color.RED)
                .strokeWidth(5);
        for (int i = 0; i < Polygon_sides; i++)
            options.add(markerList.get(i).getPosition());
        quadrilateral = mMap.addPolygon(options);
    }

    private void clearMap() {
       /* if (points != null) {
            points.remove();
            points = null;
        }
        line.remove();*/
        for (Marker marker : markerList)
            marker.remove();
        markerList.clear();
        quadrilateral.remove();
        quadrilateral = null;
    }
    private void drawLine() {
        PolylineOptions options = new PolylineOptions()
                .color(Color.BLACK)
                .width(10)
                .add(marker.getPosition(), points.getPosition());
        line = mMap.addPolyline(options);
    }
}