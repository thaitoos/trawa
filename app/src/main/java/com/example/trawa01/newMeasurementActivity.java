package com.example.trawa01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.ActivityEntity;
import model.ActivityType;
import model.MeasurementEntity;

public class newMeasurementActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private static final int LOCATION_REQUEST_CODE = 10001;
    Button startButton;
    Button finishButton;
    private FusedLocationProviderClient fusedLocationClient;
    private MeasurementViewModel measurementViewModel;
    private ActivityViewModel activityViewModel;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private ActivityEntity activity;
    List<MeasurementEntity> measurements = new ArrayList<>();

    TextView textView;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d("Location", "Location received: " + locationResult.toString());
            if (locationResult == null) {
                return;
            }
            Location location = locationResult.getLastLocation();
            MeasurementEntity measurement = new MeasurementEntity(location.getLatitude(), location.getLongitude(), location.getAltitude(),
                    location.getSpeed(), location.getAccuracy(), location.getTime(), activity.getStartTime());

            textView.setText(String.valueOf(measurement.getActivityStartTime()));
            measurements.add(measurement);
            measurementViewModel.insert(measurement);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        startButton = findViewById(R.id.button_start);
        finishButton = findViewById(R.id.button_finish);
        finishButton.setEnabled(false);
        textView = findViewById(R.id.text_view);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        startButton.setOnClickListener(view -> {
            checkSettingsAndStartLocationUpdates();
        });
        finishButton.setOnClickListener(view -> {
            stopLocationUpdates();
        });
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            Log.d("Location", "checkSettingsAndStartLocationUpdates: " + "All location settings are satisfied");
            startLocationUpdates();
        });
        locationSettingsResponseTask.addOnFailureListener(e -> {
            Log.d("Location", "checkSettingsAndStartLocationUpdates: " + "All location settings are N OT NOT T satisfied");
            if (e instanceof ResolvableApiException) {
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(newMeasurementActivity.this, LOCATION_REQUEST_CODE);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }

        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        activity = new ActivityEntity(Calendar.getInstance().getTimeInMillis());

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        finishButton.setEnabled(true);
        startButton.setEnabled(false);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        long duration = Calendar.getInstance().getTimeInMillis() - activity.getStartTime();
        double averageSpeed = 0;
        for(MeasurementEntity measurement : measurements){
            averageSpeed += measurement.getSpeed();
        }
        averageSpeed = averageSpeed / measurements.size();
        double distance = averageSpeed * duration / 1000000;
        activity = new ActivityEntity(activity.getStartTime(), duration,
                "test name", "test desc", false, ActivityType.RUNNING, distance);
        activityViewModel.insert(activity);

        finish();
    }

    @Override
    public void onStart(){
        super.onStart();
        //Log.d("Location", "onStart: " + "started");
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //checkSettingsAndStartLocationUpdates();
            //Log.d("Location", "onStart: " + "COOL");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            //Log.d("Location", "onStart: " + "NOT COOL");
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkSettingsAndStartLocationUpdates();
            }
        }
    }*/

}