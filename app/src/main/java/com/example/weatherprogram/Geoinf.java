package com.example.weatherprogram;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Geoinf extends AppCompatActivity implements LocationListener {
    MapView map = null;
    MyLocationNewOverlay myLocationNewOverlay = null;
    LocationManager locationManager;
    double point_long = 0;
    double point_lat = 0;
    double loc_long = 0;
    double loc_lat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                    }
                    Boolean coarseLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    }
                    if (fineLocationGranted != null && fineLocationGranted) {
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            } else {
                                // Разрешение не получено
                                Toast.makeText(getApplicationContext(), "Для полной работы приложения необходимо разрешить " +
                                        "отслеживать геолокацию", Toast.LENGTH_LONG).show();
                            }
                        }
                );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_geoinf);

        map = (MapView) findViewById(R.id.map);
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.e("MapView", "normal click");
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.e("MapView", "long click");
                Toast.makeText(getApplicationContext(), "Long click location " + p
                        , Toast.LENGTH_SHORT).show();
                point_lat = p.getLatitude();
//                double lat2 = myLocationNewOverlay.getMyLocation().getLatitude();
                point_long = p.getLongitude();
//                double lon2 = myLocationNewOverlay.getMyLocation().getLongitude();
//                double R = 6371e3; //в метрах
//                double ph1 = lat1 * Math.PI/180;
//                double ph2 = lat2 * Math.PI/180;
//                double dlph = (lat2-lat1) * Math.PI/180;
//                double dllb = (lon2-lon1) * Math.PI/180;

//                double a = Math.sin(dlph/2) * Math.sin(dlph/2) +
//                        Math.cos(ph1) * Math.cos(ph2) *
//                                Math.sin(dllb/2) * Math.sin(dllb/2);
//                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//                double d = R * c; //в метрах
//                Toast.makeText(getApplicationContext(), "distance between points " + d
//                        , Toast.LENGTH_LONG).show();
                return false;
            }
        }));


        map.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = map.getController();
        mapController.setZoom(1.0);
        map.setMinZoomLevel(1.0);
        //Минимальный зум, чтоб карта не делилась на несколько при отдалении
        //map.setHorizontalMapRepetitionEnabled(false);
        //map.setVerticalMapRepetitionEnabled(false);
        //Убирает повторение карты при перемещении в одну из сторон, но при максимальнот отдалении карта была на белом фоне.
        //map.setUseDataConnection(false);
        map.setClickable(true);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //map.setUseDataConnection(false);
        map.setFocusable(true);
        map.setFocusableInTouchMode(true);
        Button location = findViewById(R.id.location);
        Button point = findViewById(R.id.result_point);
        Button loc = findViewById(R.id.result_location);
        GpsMyLocationProvider provider = new GpsMyLocationProvider(getApplicationContext());
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
        myLocationNewOverlay = new MyLocationNewOverlay(provider, map);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.getMyLocation();
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "my location " + myLocationNewOverlay.getMyLocation() // Из MyLocationOverlay берем данные о местоположении
                        , Toast.LENGTH_LONG).show();
                map.getOverlays().add(myLocationNewOverlay);
                GeoPoint startPoint = new GeoPoint(myLocationNewOverlay.getMyLocation().getLatitude(), myLocationNewOverlay.getMyLocation().getLongitude());
                mapController.setCenter(startPoint);
                loc_long = myLocationNewOverlay.getMyLocation().getLongitude();
                loc_lat = myLocationNewOverlay.getMyLocation().getLatitude();
            }
        });

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                System.out.println(point_long + " " + point_lat);
                intent.putExtra("Long", String.valueOf(point_long));
                intent.putExtra("Lat", String.valueOf(point_lat));
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "Проверка по точке " + point_lat + point_long, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                double longa = myLocationNewOverlay.getMyLocation().getLongitude();
                double lati = myLocationNewOverlay.getMyLocation().getLatitude();
                intent.putExtra("Long", String.valueOf(longa));
                intent.putExtra("Lat", String.valueOf(lati));
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "Проверка определения " + lati + longa, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);



    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude() * 1E6);
        int lng = (int) (location.getLongitude() * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        map.invalidate();
    }

    public void onResume(){
        super.onResume();
        map.onResume();
    }

    public void onPause(){
        super.onPause();
        map.onPause();
    }
}