package com.example.rentalappcv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ClientRatingManagerActivity extends AppCompatActivity {
    private String clientEmail;
    private DatabaseManager dbManager;
    private int propertyId;
    private Button submitButton;
    private SeekBar urgencySeekBar;
    private TextView urgencyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_rating_manager);

        // Initialize DatabaseManager and open connection
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve extras from intent
        clientEmail = getIntent().getStringExtra("client_email");
        propertyId = getIntent().getIntExtra("property_id", -1);

        // Initialize UI components
        urgencySeekBar = findViewById(R.id.urgencySeekBar);
        urgencyTextView = findViewById(R.id.urgencyTextView);
        submitButton = findViewById(R.id.submitButton);

        // Set up SeekBar listener
        urgencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                urgencyTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Add behavior when touch starts
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Add behavior when touch stops
            }
        });

        // Set up Submit button listener
        submitButton.setOnClickListener(v -> {
            int rating = urgencySeekBar.getProgress();
            if (rating > 0) {
                dbManager.addManagerRating(rating);
                Toast.makeText(
                        ClientRatingManagerActivity.this,
                        "Review submitted",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            } else {
                Toast.makeText(
                        ClientRatingManagerActivity.this,
                        "Please select a rating",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database when the activity is destroyed
        dbManager.close();
    }
}
