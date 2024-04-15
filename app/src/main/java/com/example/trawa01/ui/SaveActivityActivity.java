package com.example.trawa01.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trawa01.R;

public class SaveActivityActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText descriptionEditText;
    Button discardButton;
    Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_activity);
        nameEditText = findViewById(R.id.editTextName);
        descriptionEditText = findViewById(R.id.editTextDescription);
        discardButton = findViewById(R.id.buttonDiscard);
        saveButton = findViewById(R.id.buttonSave);

        discardButton.setOnClickListener(v -> {
            finish();
        });

        saveButton.setOnClickListener(v -> {
            if(nameEditText.getText().toString().isEmpty()){
                nameEditText.setError("Name is required");
                return;
            }
            Intent replyIntent = new Intent();
            replyIntent.putExtra("name", nameEditText.getText().toString());
            replyIntent.putExtra("description", descriptionEditText.getText().toString());
            setResult(RESULT_OK, replyIntent);
            finish();
        });
    }
}
