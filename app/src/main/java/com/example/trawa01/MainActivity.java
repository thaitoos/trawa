package com.example.trawa01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trawa01.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ViewModel.MeasurementViewModel;
import model.MeasurementEntity;
import views.MeasurementAdapter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MeasurementViewModel measurementViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MeasurementAdapter adapter = new MeasurementAdapter(new MeasurementAdapter.WordDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);

        measurementViewModel.getAllMeasurements().observe(this, measurements -> {
            adapter.submitList(measurements);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, newMeasurementActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            MeasurementEntity measurement =
                new MeasurementEntity(0,0,0,0,0,69,0 );
            measurementViewModel.insert(measurement);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Empty not saved!",
                    Toast.LENGTH_LONG).show();
        }
    }



}