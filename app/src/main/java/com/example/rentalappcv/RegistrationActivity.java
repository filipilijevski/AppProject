package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that allows the user to choose what type of account to register:
 * Landlord, Client, or Manager.
 */

public class RegistrationActivity extends AppCompatActivity {

    private Button landlordButton;
    private Button clientButton;
    private Button managerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        landlordButton = findViewById(R.id.landlordButton);
        clientButton = findViewById(R.id.clientButton);
        managerButton = findViewById(R.id.managerButton);

        // Navigate to LandlordRegistration
        landlordButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LandlordRegistration.class);
            startActivity(intent);
        });

        // Navigate to ClientRegistration
        clientButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, ClientRegistration.class);
            startActivity(intent);
        });

        // Navigate to ManagerRegistration
        managerButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, ManagerRegistration.class);
            startActivity(intent);
        });
    }
}
