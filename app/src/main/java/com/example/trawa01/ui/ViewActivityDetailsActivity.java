package com.example.trawa01.ui;

import static java.lang.Math.min;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trawa01.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

        fillChart1();
    }

    void fillChart1(){
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < min(10, measurements.size()); i++){
            entries.add(new Entry(i, (float) measurements.get(i).getSpeed()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Altitude");
        LineData lineData = new LineData(dataSet);
        chart1.setData(lineData);
        chart1.invalidate();
    }
}
