package com.example.trawa01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trawa01.databinding.ActivityMainBinding;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ViewModel.ActivityViewModel;
import ViewModel.MeasurementViewModel;
import views.ActivityAdapter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MeasurementViewModel measurementViewModel;
    private ActivityViewModel activityViewModel;
    public static final int NEW_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, null);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ActivityAdapter adapter = new ActivityAdapter(new ActivityAdapter.WordDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        activityViewModel.getAllActivities().observe(this, adapter::submitList);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewMeasurementActivity.class);
                startActivity(intent);
            }
        });

        /*recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "onClick: " + recyclerView.getChildItemId(view));
                recyclerView.getChildItemId(view);
                Intent intent = new Intent(MainActivity.this, ViewActivityActivity.class);
                startActivity(intent);
            }
        });*/
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }*/



}