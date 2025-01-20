package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Activity that displays a Google Map with properties matching
 * search criteria, and navigates the user to property details on marker click.
 */

public class SearchResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String clientEmail;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_search_results);

        // Retrieve client email and load map
        clientEmail = getIntent().getStringExtra("email_address");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Home button
        ImageView clientHomeButton = findViewById(R.id.clientHomeButton);
        clientHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearchResultsActivity.this, ClientHomepageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearchResultsActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set custom info window and handle marker clicks
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(marker -> {
            Property property = (Property) marker.getTag();
            if (property != null) {
                Intent intent = new Intent(SearchResultsActivity.this, PropertyDetailsActivity.class);
                intent.putExtra("property_id", property.getId());
                intent.putExtra("email_address", clientEmail);
                startActivity(intent);
            }
        });

        // Perform property search
        performSearch();
    }

    /**
     * Retrieves search criteria from the intent and queries matching properties,
     * then displays them on the map.
     */
    private void performSearch() {
        // Extract search parameters from intent
        String address = getIntent().getStringExtra("searchBar");
        int numberOfRooms = parseOrDefault(getIntent().getStringExtra("numberOfRooms"), -1);
        int numberOfBathrooms = parseOrDefault(getIntent().getStringExtra("numberOfBathrooms"), -1);
        int numberOfFloors = parseOrDefault(getIntent().getStringExtra("numberOfFloors"), -1);
        int rent = parseOrDefault(getIntent().getStringExtra("rent"), -1);
        int area = parseOrDefault(getIntent().getStringExtra("area"), -1);

        boolean utilitiesIncludedHydro = getIntent().getBooleanExtra("utilitiesIncludedHydro", false);
        boolean utilitiesIncludedHeat = getIntent().getBooleanExtra("utilitiesIncludedHeat", false);
        boolean utilitiesIncludedWater = getIntent().getBooleanExtra("utilitiesIncludedWater", false);
        boolean houses = getIntent().getBooleanExtra("houses", false);
        boolean condo = getIntent().getBooleanExtra("condo", false);
        boolean commercial = getIntent().getBooleanExtra("commercial", false);
        boolean anyProperty = getIntent().getBooleanExtra("anyProperty", false);

        // Query DB
        DatabaseManager dbManager = new DatabaseManager(this);
        dbManager.open();
        List<Property> properties = dbManager.searchProperties(
                address,
                numberOfRooms,
                numberOfBathrooms,
                numberOfFloors,
                rent,
                area,
                utilitiesIncludedHydro,
                utilitiesIncludedHeat,
                utilitiesIncludedWater,
                houses,
                condo,
                commercial,
                anyProperty
        );
        dbManager.close();

        // Display results
        if (properties == null || properties.isEmpty()) {
            Toast.makeText(this, "No properties found matching the criteria.", Toast.LENGTH_LONG).show();
            return;
        }

        // Place markers for each property
        for (Property property : properties) {
            LatLng position = new LatLng(property.getLatitude(), property.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                    .title(property.getAddress()));
            marker.setTag(property);
        }

        // Move camera to the first property
        Property firstProperty = properties.get(0);
        LatLng firstLatLng = new LatLng(firstProperty.getLatitude(), firstProperty.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f));
    }

    /**
     * Attempts to parse an integer from a string; returns defaultValue if parsing fails.
     *
     * @param value the string to parse
     * @param defaultValue fallback if string is null/invalid
     * @return the parsed integer or defaultValue
     */

    private int parseOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
