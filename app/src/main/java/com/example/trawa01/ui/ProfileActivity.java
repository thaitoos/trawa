package com.example.trawa01.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.ActivityEntity;
import model.ActivityType;
import model.MeasurementEntity;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private LineChart lineChart1;
    private Spinner dropdown1;
    private ActivityViewModel activityViewModel;
    private MeasurementViewModel measurementViewModel;
    private EditText recordInput;
    private Button checkButton;
    private TextView recordNameText;
    private TextView recordTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);

        lineChart1 = findViewById(R.id.line_chart1);
        dropdown1 = findViewById(R.id.dropdown1);

        recordInput = findViewById(R.id.record_input);
        checkButton = findViewById(R.id.check);
        recordNameText = findViewById(R.id.record_name);
        recordTimeText = findViewById(R.id.record_time);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distance = Double.parseDouble(recordInput.getText().toString());
                fillRecord(distance);
            }
        });


        initializeDropdowns();
    }

    private void initializeDropdowns(){
        List<String> allTypesList = new ArrayList<>();
        for(ActivityType a : ActivityType.values()){
            allTypesList.add(a.toString() + "- time");
        }
        String[] allTypes = allTypesList.toArray(new String[0]);

        String[] movingTypes = {ActivityType.WALKING.toString()+"- distance", ActivityType.RUNNING.toString()+"- distance", ActivityType.CYCLING.toString()+"- distance"};

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Stream.concat(Stream.of(allTypes), Stream.of(movingTypes)).toArray(String[]::new));
        dropdown1.setAdapter(adapter);
        dropdown1.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        boolean isTime = item.endsWith("time");
        String activity = item.split("-")[0];
        fillLineChart(activity, isTime);
    }

    private void fillLineChart(String activity, boolean isTime){
        List<Entry> entries = new ArrayList<>();
        List<ActivityEntity> activities = activityViewModel.getAllActivitiesList();
        if(isTime){
            List<Long> MillisInMonths = new ArrayList<>();
            for(int i = 0; i < 12; i++){
                MillisInMonths.add(0L);
            }
            for(ActivityEntity a : activities){
                if(!a.getType().toString().equals(activity)){
                    continue;
                }
                int monthsPassed = getMonthsPassed(a.getStartTime());
                if(monthsPassed < 12){
                    MillisInMonths.set(11-monthsPassed, MillisInMonths.get(11-monthsPassed) + a.getDuration());
                }
            }

            for(int i = 0; i < 12; i++){
                entries.add(new Entry(i, MillisInMonths.get(i)));
            }
        }
        else{
            List<Float> distanceInMonths = new ArrayList<>();
            for(int i = 0; i < 12; i++){
                distanceInMonths.add(0f);
            }
            for(ActivityEntity a : activities){
                if(!a.getType().toString().equals(activity)){
                    continue;
                }
                int monthsPassed = getMonthsPassed(a.getStartTime());
                if(monthsPassed < 12){
                    distanceInMonths.set(11-monthsPassed, distanceInMonths.get(11-monthsPassed) + (float)a.getDistance());
                }
            }

            for(int i = 0; i < 12; i++){
                entries.add(new Entry(i, distanceInMonths.get(i)));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, activity);
        LineData lineData = new LineData(dataSet);
        lineChart1.setData(lineData);
        lineChart1.invalidate();

    }

    private void fillRecord(double distance){
        /*long INF = 10000000000000000L;
        List<MeasurementEntity> measurements = measurementViewModel.getAllMeasurementsInOrderList();
        List<List<MeasurementEntity>> measurementsByActivity = new ArrayList<>();
        for(MeasurementEntity measurement : measurements){
            if(measurementsByActivity.isEmpty() ||
                    measurementsByActivity.get(measurementsByActivity.size()-1).get(0).getActivityStartTime() != measurement.getActivityStartTime()){
                measurementsByActivity.add(new ArrayList<>());
            }
            measurementsByActivity.get(measurementsByActivity.size()-1).add(measurement);
        }
        long BestTimeMillis = INF;
        for(List<MeasurementEntity> measurementList : measurementsByActivity){
            long bestgetBestTime(measurementList, distance);
        }*/

    }

    /*private long getBestTime(List<MeasurementEntity> measurementList, double distance){
        double bestTime = Double.MAX_VALUE;
        long bestTimeMillis = 0
        for(MeasurementEntity measurement : measurementList){
            if(measurement.getDistance() == distance && measurement.getTime() < bestTime){
                bestTime = measurement.getTime();
                bestTimeMillis = measurement.getTimeMillis();
            }
        }
    }*/

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private int getMonthsPassed(long startTime){
        long diff = System.currentTimeMillis() - startTime;
        return (int) (diff / (1000L * 60 * 60 * 24 * 30)); // overflow?
    }
}
