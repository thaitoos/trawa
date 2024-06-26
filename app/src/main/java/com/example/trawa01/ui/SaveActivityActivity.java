package com.example.trawa01.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trawa01.R;

import model.ActivityType;

public class SaveActivityActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    Bitmap lastImage;

    EditText nameEditText;
    EditText descriptionEditText;
    Button discardButton;
    Button saveButton;
    Spinner activityTypeSpinner;
    Button takePhotoButton;
    ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_activity);
        nameEditText = findViewById(R.id.editTextName);
        descriptionEditText = findViewById(R.id.editTextDescription);
        discardButton = findViewById(R.id.buttonDiscard);
        saveButton = findViewById(R.id.buttonSave);
        activityTypeSpinner = findViewById(R.id.spinnerActivityType);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        photoImageView = findViewById(R.id.imageView);

        discardButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            if (nameEditText.getText().toString().isEmpty()) {
                nameEditText.setError("Name is required");
                return;
            }
            Intent replyIntent = new Intent();
            replyIntent.putExtra("name", nameEditText.getText().toString());
            replyIntent.putExtra("description", descriptionEditText.getText().toString());
            replyIntent.putExtra("activityType", ((ActivityType) activityTypeSpinner.getSelectedItem()).toString());
            if(lastImage != null)
                replyIntent.putExtra("photo", lastImage);
            setResult(RESULT_OK, replyIntent);
            finish();
        });

        ArrayAdapter<ActivityType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ActivityType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityTypeSpinner.setAdapter(adapter);

        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data == null){
                return;
            }
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            lastImage = imageBitmap;
            photoImageView.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }
}
