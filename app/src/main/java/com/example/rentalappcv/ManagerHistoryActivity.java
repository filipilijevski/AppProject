package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManagerHistoryActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private String managerEmail;
    private ManagerPropertyAdapter propertyAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_property_history);

        // Open database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve manager's email
        managerEmail = getIntent().getStringExtra("email_address");

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView_container_landlord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Current Properties button
        findViewById(R.id.managerCurrentPropertiesButton).setOnClickListener(v -> showCurrentProperties());

        // Past Properties button
        findViewById(R.id.managerPastPropertiesButton).setOnClickListener(v -> showPastProperties());

        // Home button container
        LinearLayout managerHomeButtonContainer = findViewById(R.id.managerHomeButtonContainer);
        managerHomeButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHistoryActivity.this, ManagerHomepageActivity.class);
            intent.putExtra("email_address", managerEmail);
            startActivity(intent);
        });

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHistoryActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", managerEmail);
            startActivity(intent);
        });
    }

    /**
     * Shows the manager's current properties.
     * Logs them to Logcat and displays them via ManagerPropertyAdapter.
     */

    private void showCurrentProperties() {
        List<Property> properties = dbManager.getPropertiesByManager(managerEmail);

        if (properties.isEmpty()) {
            Log.d("ManagerPropertyHistoryActivity", "No current properties found.");
        } else {
            for (Property property : properties) {
                Log.d("ManagerPropertyHistoryActivity", "Property: " + property.getAddress());
            }
        }

        propertyAdapter = new ManagerPropertyAdapter(this, properties);
        recyclerView.setAdapter(propertyAdapter);

        Toast.makeText(this, "Showing current properties", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows the manager's past properties and updates RecyclerView.
     */

    private void showPastProperties() {
        List<Property> pastProperties = dbManager.getPastPropertiesByManager(managerEmail);
        propertyAdapter = new ManagerPropertyAdapter(this, pastProperties);
        recyclerView.setAdapter(propertyAdapter);

        Toast.makeText(this, "Showing past properties", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
