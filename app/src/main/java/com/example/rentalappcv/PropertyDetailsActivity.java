package com.example.rentalappcv;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

/**
 * Activity that displays detailed information about a property for the client,
 * including images, utilities, and an option to send requests (rent/view).
 */

public class PropertyDetailsActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private String clientEmail;

    // UI Components
    private ImageView firstPropertyImageView;
    private TextView propertyAddressClient;
    private TextView propertyTypeClient;
    private TextView propertyNumberOfRoomsClient;
    private TextView propertyNumberOfBathroomsClient;
    private TextView propertyNumberOfFloorsClient;
    private TextView propertyLaundryInUnitClient;
    private TextView propertyAreaClient;
    private TextView propertyNumberOfParkingSpotsClient;
    private TextView propertyRentClient;
    private TextView propertyLandlordAssignedClient;
    private TextView propertyManagerAssignedClient;
    private CheckBox propertyUtilitiesIncludedHydroClient;
    private CheckBox propertyUtilitiesIncludedHeatClient;
    private CheckBox propertyUtilitiesIncludedWaterClient;
    private Button rentPropertyRequestButton;
    private Button viewPropertyRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_details_client);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve client email and property ID from intent
        clientEmail = getIntent().getStringExtra("email_address");
        Log.d("PropertyDetailsActivity", "Received client email: " + clientEmail);

        if (clientEmail == null) {
            Toast.makeText(this, "Client email is missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        int propertyId = getIntent().getIntExtra("property_id", -1);
        if (propertyId == -1) {
            Toast.makeText(this, "Invalid property ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Retrieve property from DB
        Property property = dbManager.getPropertyById(propertyId);
        if (property == null) {
            Toast.makeText(this, "Property not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize UI and populate with property details
        initializeViews();
        populatePropertyDetails(property);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    /**
     * Initializes all the UI elements in the layout.
     */

    private void initializeViews() {
        firstPropertyImageView = findViewById(R.id.firstPropertyImageView);
        propertyAddressClient = findViewById(R.id.propertyAddressClient);
        propertyTypeClient = findViewById(R.id.propertyTypeClient);
        propertyNumberOfRoomsClient = findViewById(R.id.propertyNumberOfRoomsClient);
        propertyNumberOfBathroomsClient = findViewById(R.id.propertyNumberOfBathroomsClient);
        propertyNumberOfFloorsClient = findViewById(R.id.propertyNumberOfFloorsClient);
        propertyLaundryInUnitClient = findViewById(R.id.propertyLaundryInUnitClient);
        propertyAreaClient = findViewById(R.id.propertyAreaClient);
        propertyNumberOfParkingSpotsClient = findViewById(R.id.propertyNumberOfParkingSpotsClient);
        propertyRentClient = findViewById(R.id.propertyRentClient);
        propertyLandlordAssignedClient = findViewById(R.id.propertyLandlordAssignedClient);
        propertyManagerAssignedClient = findViewById(R.id.propertyManagerAssignedClient);
        propertyUtilitiesIncludedHydroClient = findViewById(R.id.propertyUtilitiesIncludedHydroClient);
        propertyUtilitiesIncludedHeatClient = findViewById(R.id.propertyUtilitiesIncludedHeatClient);
        propertyUtilitiesIncludedWaterClient = findViewById(R.id.propertyUtilitiesIncludedWaterClient);
        rentPropertyRequestButton = findViewById(R.id.rentPropertyRequestButton);
        viewPropertyRequestButton = findViewById(R.id.viewPropertyRequestButton);
    }

    /**
     * Fills in the views with data from the given Property object.
     *
     * @param property the Property to display
     */

    private void populatePropertyDetails(Property property) {
        propertyAddressClient.setText(property.getAddress());
        propertyTypeClient.setText(property.getType());
        propertyNumberOfRoomsClient.setText(String.valueOf(property.getRooms()));
        propertyNumberOfBathroomsClient.setText(String.valueOf(property.getBathrooms()));
        propertyNumberOfFloorsClient.setText(String.valueOf(property.getFloors()));
        propertyLaundryInUnitClient.setText(property.isLaundryInUnit() ? "Yes" : "No");
        propertyAreaClient.setText(String.valueOf(property.getArea()));
        propertyNumberOfParkingSpotsClient.setText(String.valueOf(property.getParkingSpots()));
        propertyRentClient.setText(String.valueOf(property.getRent()));
        propertyLandlordAssignedClient.setText(property.getLandlordEmail());
        propertyManagerAssignedClient.setText(property.getManager() != null
                ? property.getManager().getEmailAddress() : "None");

        propertyUtilitiesIncludedHydroClient.setChecked(property.isHydroIncluded());
        propertyUtilitiesIncludedHeatClient.setChecked(property.isHeatIncluded());
        propertyUtilitiesIncludedWaterClient.setChecked(property.isWaterIncluded());

        // Load first image using Picasso if available
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            Picasso.get().load(property.getImages().get(0)).into(firstPropertyImageView);
        }

        // Setup buttons for sending requests
        rentPropertyRequestButton.setOnClickListener(v -> sendRequest("rent", property));
        viewPropertyRequestButton.setOnClickListener(v -> sendRequest("view", property));
    }

    /**
     * Sends a rent or view request for the given property.
     *
     * @param type     "rent" or "view"
     * @param property the property to request
     */

    private void sendRequest(String type, Property property) {
        // Re-open DB in case it was closed
        dbManager.open();

        PropertyRequest request = new PropertyRequest();
        request.setPropertyId(property.getId());
        request.setClientEmail(clientEmail);
        request.setStatus("pending");
        request.setRequestType(type);
        request.setPropertyAddress(property.getAddress());

        // Include client profile image if available
        String profileImage = dbManager.getClientProfileImage(clientEmail);
        if (profileImage != null) {
            request.setClientProfileImage(profileImage);
        }

        Log.d("PropertyDetailsActivity", "Sending request: " + request);

        dbManager.addRequest(request);
        dbManager.close();

        Toast.makeText(this, "Request to " + type + " property sent", Toast.LENGTH_LONG).show();
    }
}
