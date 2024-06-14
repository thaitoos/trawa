package com.example.trawa01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.ui.SaveActivityActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.ActivityEntity;
import model.ActivityType;
import model.MeasurementEntity;

public class NewMeasurementActivity extends AppCompatActivity {
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
    BarChart chart1;
    private FusedLocationProviderClient fusedLocationClient;
    private MeasurementViewModel measurementViewModel;
    private ActivityViewModel activityViewModel;
    private LocationRequest locationRequest;
    private ActivityEntity activity;
    final List<MeasurementEntity> measurementsSinceLastRestart = new ArrayList<>();
    final List<MeasurementEntity> allMeasurements = new ArrayList<>();
    private boolean finished = false;

    final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            MeasurementEntity measurement = new MeasurementEntity(location.getLatitude(), location.getLongitude(), location.getAltitude(),
                    location.getSpeed(), location.getAccuracy(), location.getTime(), activity.getStartTime(), (float)currentDistance, currentDuration);

            measurementsSinceLastRestart.add(measurement);
            allMeasurements.add(measurement);

            currentDistance += measurement.getSpeed() * gpsInterval / 1000000;
            if(measurementsSinceLastRestart.size() == 1){
                currentDuration += gpsInterval;
            }
            else{
                currentDuration += location.getTime() - measurementsSinceLastRestart.get(measurementsSinceLastRestart.size() - 2).getTime();
            }

            distanceValue.setText(String.format("%.2f km", currentDistance));
            if(measurementsSinceLastRestart.size() > 5) {
                double pace = getPace();
                // pace is s / km
                paceValue.setText(String.format("%02d:%02d min/km", (int) pace / 60, (int) pace % 60));
            }
            timeValue.setText(String.format("%02d:%02d", currentDuration / 60000, (currentDuration % 60000) / 1000));
            measurementViewModel.insert(measurement);

            updateChart();
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
        chart1 = findViewById(R.id.chart1);
        chart1.setNoDataText("");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, gpsInterval)
                .build();

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        startButton.setOnClickListener(view -> checkSettingsAndStartLocationUpdates());
        finishButton.setOnClickListener(view -> {
            paceValue.setText("--:-- min/km");
            fusedLocationClient.removeLocationUpdates(locationCallback);
            measurementsSinceLastRestart.clear();
            startFinishingActivity();
        });
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> startLocationUpdates());
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

    private void startFinishingActivity() {
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
            String activityType = data.getStringExtra("activityType");
            Bitmap photo = null;
            if(data.getParcelableExtra("photo") != null){
                photo = data.getParcelableExtra("photo");
            }

            activity = new ActivityEntity(activity.getStartTime(), duration,
                    name, description, false, ActivityType.valueOf(activityType.toUpperCase()), distance, photo != null ? bitmapToByteArray(photo) : null);
            activityViewModel.insert(activity);

            finished = true;

            fusedLocationClient.removeLocationUpdates(locationCallback);

            finish();
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onStart(){
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    private double getPace(){
        int secondsToCheck = Math.min(60, measurementsSinceLastRestart.size());
        double distance = 0;
        for(int i = 0; i < secondsToCheck; i++){
            distance += measurementsSinceLastRestart.get(measurementsSinceLastRestart.size() - i - 1).getSpeed() * gpsInterval / 1000000;
        }
        return (secondsToCheck / distance);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRestart(){
        super.onRestart();
        if(finished){
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateChart(){
        if(allMeasurements.size() < 3){
            return;
        }

        int kmVisited = (int) allMeasurements.get(allMeasurements.size() - 1).getDistance() + 1;
        List<Long> firstTimestamp = new ArrayList<>();
        List<Long> lastTimestamp = new ArrayList<>();
        for(int i = 0; i < kmVisited; i++){
            firstTimestamp.add(0L);
            lastTimestamp.add(0L);
        }

        for(MeasurementEntity measurement : allMeasurements){
            int km = (int) measurement.getDistance();
            if(firstTimestamp.get(km) == 0){
                firstTimestamp.set(km, measurement.getTime());
            }
            lastTimestamp.set(km, measurement.getTime());
        }

        List<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < kmVisited; i++){
            entries.add(new BarEntry(i, (lastTimestamp.get(i) - firstTimestamp.get(i))));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Time spent on each km");
        BarData barData = new BarData(dataSet);
        chart1.setData(barData);
        chart1.getAxisLeft().setAxisMinimum(0); // TODO cleanup the chart, also make scrollable
        chart1.getDescription().setText("");
        chart1.invalidate();

    }


}