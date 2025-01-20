package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The Manager's main homepage for navigating to different parts of the app:
 * profile, messages/requests, history, or the same homepage (refresh).
 */

public class ManagerHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_homepage);

        // Retrieve manager's email address
        final String email = getIntent().getStringExtra("email_address");

        // Profile Button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Messages Button
        ImageView managerMessagesButton = findViewById(R.id.managerMessagesButton);
        managerMessagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, ManagerMessagesRequestsActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // History Button
        ImageView managerHistoryButton = findViewById(R.id.managerHistoryButton);
        managerHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, ManagerHistoryActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Home Button (reload same activity)
        ImageView managerHomeButton = findViewById(R.id.managerHomeButton);
        managerHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, ManagerHomepageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });
    }
}
