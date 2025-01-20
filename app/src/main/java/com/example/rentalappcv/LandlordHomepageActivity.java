package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LandlordHomepageActivity extends AppCompatActivity {

    private boolean assigningManager = false;
    private boolean showingManagers = true;

    private String email;
    private Manager selectedManager;

    private DatabaseManager dbManager;
    private RecyclerView recyclerView;

    // Adapters
    private ManagerAdapter managerAdapter;
    private PropertyAdapter propertyAdapter;
    private LandlordRentRequestAdapter landlordRentRequestAdapter;

    // Data Lists
    private List<Manager> managerList;
    private List<Property> propertyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landlord_homepage);

        // Open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve landlord email from intent
        email = getIntent().getStringExtra("email_address");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_container_landlord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load data and set up adapters
        managerList = dbManager.getAllManagers();
        managerAdapter = new ManagerAdapter(this, managerList);

        propertyList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(this, propertyList);

        // By default, show the list of managers
        recyclerView.setAdapter(managerAdapter);

        // Setup search bar listener
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (showingManagers) {
                    managerAdapter.filter(s.toString());
                } else {
                    filterProperties(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandlordHomepageActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Home button container
        LinearLayout landlordHomeButtonContainer = findViewById(R.id.landlordHomeButtonContainer);
        landlordHomeButtonContainer.setOnClickListener(v -> {
            // Reload LandlordHomepageActivity with the same email
            Intent intent = new Intent(LandlordHomepageActivity.this, LandlordHomepageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Register new property
        Button registerPropertyButton = findViewById(R.id.registerPropertyButton);
        registerPropertyButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandlordHomepageActivity.this, PropertyRegistration.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Show property list
        Button showPropertyButton = findViewById(R.id.showPropertyButton);
        showPropertyButton.setOnClickListener(v -> showProperties(email));

        // History / Requests
        LinearLayout landlordHistoryButtonContainer = findViewById(R.id.landlordHistoryButtonContainer);
        landlordHistoryButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(LandlordHomepageActivity.this, LandlordRequestsPageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });
    }

    /**
     * Loads and displays properties associated with this landlord's email.
     */

    private void showProperties(String email) {
        showingManagers = false;
        propertyList = dbManager.getPropertiesByLandlord(email);

        if (assigningManager) {
            // If we are in the process of assigning a manager, use LandlordRentRequestAdapter
            landlordRentRequestAdapter = new LandlordRentRequestAdapter(this, propertyList, selectedManager, dbManager);
            recyclerView.setAdapter(landlordRentRequestAdapter);
        } else {
            // Otherwise, just show the property list
            propertyAdapter.updateProperties(propertyList);
            recyclerView.setAdapter(propertyAdapter);
        }
    }

    /**
     * Filters the current property list based on the query.
     */
    private void filterProperties(String query) {
        List<Property> filteredList = new ArrayList<>();
        for (Property property : propertyList) {
            if (property.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(property);
            }
        }

        if (assigningManager && landlordRentRequestAdapter != null) {
            landlordRentRequestAdapter.updateProperties(filteredList);
        } else if (propertyAdapter != null) {
            propertyAdapter.updateProperties(filteredList);
        }
    }

    /**
     * Called when a manager is selected. This triggers manager assignment mode.
     */

    public void assignManagerToProperty(Manager manager) {
        selectedManager = manager;
        assigningManager = true;
        showProperties(email);
        Toast.makeText(this,
                "Select a property to assign " + manager.getFirstName() + " " + manager.getLastName(),
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Confirms assigning the currently selected manager to the given property.
     */

    public void confirmManagerAssignment(Property property) {
        if (selectedManager != null) {
            dbManager.assignManagerToProperty(property.getId(), selectedManager.getEmailAddress());
            property.setManager(selectedManager);

            Toast.makeText(this,
                    "Assigned " + selectedManager.getFirstName() + " to " + property.getAddress(),
                    Toast.LENGTH_SHORT
            ).show();

            selectedManager = null;
            assigningManager = false;
            showProperties(email);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
