package com.example.trawa01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.ui.SaveActivityActivity;
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

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.ActivityEntity;
import model.ActivityType;
import model.MeasurementEntity;

public class NewMeasurementActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    public static final int FINISH_REQUEST_CODE = 2;
    private final int gpsInterval = 1000;
    private double currentDistance = 0;
    private long currentDuration = 0;
    private static final int LOCATION_REQUEST_CODE = 10001;
    Button startButton;
    Button finishButton;
    TextView timeValue;
    TextView paceValue;
    TextView distanceValue;
    TextView paceLabel;
    private FusedLocationProviderClient fusedLocationClient;
    private MeasurementViewModel measurementViewModel;
    private ActivityViewModel activityViewModel;
    private LocationRequest locationRequest;
    private ActivityEntity activity;
    List<MeasurementEntity> measurementsSinceLastRestart = new ArrayList<>();

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

            measurementsSinceLastRestart.add(measurement);

            currentDistance += measurement.getSpeed() * gpsInterval / 1000000;
            if(measurementsSinceLastRestart.size() == 1){
                currentDuration += gpsInterval;
            }
            else{
                currentDuration += location.getTime() - measurementsSinceLastRestart.get(measurementsSinceLastRestart.size() - 2).getTime();
            }

            distanceValue.setText(String.format("%.2f", currentDistance) + " km");
            if(measurementsSinceLastRestart.size() > 5) {
                double pace = getPace();
                // pace is s / km
                paceValue.setText(String.format("%02d:%02d min/km", (int) pace / 60, (int) pace % 60));
            }
            timeValue.setText(String.format("%02d:%02d", currentDuration / 60000, (currentDuration % 60000) / 1000));
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
        timeValue = findViewById(R.id.time_value);
        paceValue = findViewById(R.id.pace_value);
        distanceValue = findViewById(R.id.distance_value);
        paceLabel = findViewById(R.id.pace_label);
        paceLabel.setText("Pace last minute:");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(gpsInterval);
        locationRequest.setFastestInterval(gpsInterval); // todo
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        startButton.setOnClickListener(view -> {
            checkSettingsAndStartLocationUpdates();
        });
        finishButton.setOnClickListener(view -> {
            paceValue.setText("--:-- min/km");
            fusedLocationClient.removeLocationUpdates(locationCallback);
            measurementsSinceLastRestart.clear();
            stopLocationUpdates();
        });
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            startLocationUpdates();
        });
        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(NewMeasurementActivity.this, LOCATION_REQUEST_CODE);
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
        Intent intent = new Intent(NewMeasurementActivity.this, SaveActivityActivity.class);
        startActivityForResult(intent, FINISH_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FINISH_REQUEST_CODE && resultCode == RESULT_OK) {

            double distance = currentDistance;
            long duration = currentDuration;

            String name = data.getStringExtra("name");
            String description = data.getStringExtra("description");

            activity = new ActivityEntity(activity.getStartTime(), duration,
                    name, description, false, ActivityType.RUNNING, distance);
            activityViewModel.insert(activity);
            finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        //Log.d("Location", "onStart: " + "started");
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //checkSettingsAndStartLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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

    private double getPace(){
        double pace = 0;
        int secondsToCheck = Math.min(60, measurementsSinceLastRestart.size());
        double distance = 0;
        for(int i = 0; i < secondsToCheck; i++){
            distance += measurementsSinceLastRestart.get(measurementsSinceLastRestart.size() - i - 1).getSpeed() * gpsInterval / 1000000;
        }
        pace = secondsToCheck / distance; //
        return pace;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRestart(){
        super.onRestart();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


}