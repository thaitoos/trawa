package com.example.trawa01;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trawa01.ui.ProfileActivity;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ViewModel.ActivityViewModel;
import views.ActivityAdapter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, null);

        ActivityViewModel activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ActivityAdapter adapter = new ActivityAdapter(new ActivityAdapter.WordDiff(), activityViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        activityViewModel.getAllActivities().observe(this, adapter::submitList);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewMeasurementActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fabProfile = findViewById(R.id.fabProfile);
        fabProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fabBluetooth = findViewById(R.id.fabBluetooth);

        fabBluetooth.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivity(intent);
        });
    }

}