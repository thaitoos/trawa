package com.example.trawa01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.List;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.MeasurementEntity;

public class ViewActivityActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityViewModel activityViewModel;
    private MeasurementViewModel measurementViewModel;
    long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity_activity);
        Button button = findViewById(R.id.button_go_back);

        // set renderer to latest
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, null);

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        button.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLon = Integer.MAX_VALUE;
        double maxLon = Integer.MIN_VALUE;
        List<MeasurementEntity> measurements = measurementViewModel.getMeasurementsByActivityId(id);
        PolylineOptions options = new PolylineOptions();
        for (MeasurementEntity measurement : measurements) {
            options.add(new LatLng(measurement.getLatitude(), measurement.getLongitude()));
            minLat = Math.min(minLat, measurement.getLatitude());
            maxLat = Math.max(maxLat, measurement.getLatitude());
            minLon = Math.min(minLon, measurement.getLongitude());
            maxLon = Math.max(maxLon, measurement.getLongitude());
        }
        googleMap.addPolyline(options);
        double firstLat = measurements.get(0).getLatitude();
        double firstLon = measurements.get(0).getLongitude();
        double lastLat = measurements.get(measurements.size() - 1).getLatitude();
        double lastLon = measurements.get(measurements.size() - 1).getLongitude();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(firstLat, firstLon)).title("Start"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lastLat, lastLon)).title("Finish"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((minLat + maxLat) / 2, (minLon + maxLon) / 2), 13));
    }
}
