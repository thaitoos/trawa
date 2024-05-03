package com.example.trawa01.ui;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import model.MeasurementEntity;

public class ViewActivityDetailsActivity extends AppCompatActivity {
    private ActivityViewModel activityViewModel;
    private MeasurementViewModel measurementViewModel;
    private long id;
    private LineChart chart1;
    private LineChart chart2;
    private final int MAX_POINTS = 50;
    List<MeasurementEntity> measurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        chart1 = findViewById(R.id.lineChart1);
        chart2 = findViewById(R.id.lineChart2);

        measurements = measurementViewModel.getMeasurementsByActivityId(id);

        fillLineCharts();
    }

    class MySpeedValueFormatter extends ValueFormatter {
        @Override
        public String getPointLabel(Entry entry) {
            return "";
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(axis instanceof com.github.mikephil.charting.components.XAxis){
                if(value == 0)
                    return "0 min";
                return String.format("%.2f min", value / 1000 / 60);
            }
            else {
                return String.format("%.2f km/h", value);
            }
        }
    }

    class MyAltitudeValueFormatter extends ValueFormatter {
        @Override
        public String getPointLabel(Entry entry) {
            return "";
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(axis instanceof com.github.mikephil.charting.components.XAxis){
                if(value == 0)
                    return "0 min";
                return String.format("%.2f min", value / 1000 / 60);
            }
            else {
                return String.format("%.2f m", value);
            }
        }
    }

    void fillLineCharts(){
        List<Entry> speedEntries = new ArrayList<>();
        List<Entry> altitudeEntries = new ArrayList<>();
//        for(int i = 0; i < min(50, measurements.size()); i++){
//            entries.add(new Entry(i,  measurements.get(i).getSpeed()));
//        }
        int skip = max( measurements.size() / MAX_POINTS, 1);

        for(int i = 0; i < measurements.size(); i++){
            if(i % skip == 0){
                MeasurementEntity measurement = measurements.get(i);
                speedEntries.add(new Entry(measurement.getDuration(),  measurement.getSpeed()));
                altitudeEntries.add(new Entry(measurement.getDuration(),  (float)measurement.getAltitude()));
            }
        }
        // speed chart
        LineDataSet dataSet = new LineDataSet(speedEntries, "Pace");
        LineData lineData = new LineData(dataSet);
        lineData.setValueFormatter(new MySpeedValueFormatter());
        chart1.setData(lineData);

        chart1.getXAxis().setValueFormatter(new MySpeedValueFormatter());
        chart1.getAxisLeft().setValueFormatter(new MySpeedValueFormatter());
        chart1.invalidate();

        // altitude chart
        LineDataSet dataSet2 = new LineDataSet(altitudeEntries, "Altitude");
        LineData lineData2 = new LineData(dataSet2);
        lineData2.setValueFormatter(new MyAltitudeValueFormatter());
        chart2.setData(lineData2);

        chart2.getXAxis().setValueFormatter(new MyAltitudeValueFormatter());
        chart2.getAxisLeft().setValueFormatter(new MyAltitudeValueFormatter());
        chart2.invalidate();


    }
}
