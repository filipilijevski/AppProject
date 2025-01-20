package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Activity that shows all client requests made to a landlord's properties.
 * Allows the landlord to accept or reject requests.
 */

public class LandlordRequestsPageActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private String landlordEmail;
    private RecyclerView recyclerView;
    private LandlordRequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requests_landlord_view);

        // Open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve landlord email from Intent
        landlordEmail = getIntent().getStringExtra("email_address");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_container_landlord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter with all requests for the landlord
        requestAdapter = new LandlordRequestAdapter(
                this,
                dbManager.getRequestsByLandlord(landlordEmail),
                dbManager
        );
        recyclerView.setAdapter(requestAdapter);

        // Home button container
        LinearLayout landlordHomeButtonContainer = findViewById(R.id.landlordHomeButtonContainer);
        landlordHomeButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(LandlordRequestsPageActivity.this, LandlordHomepageActivity.class);
            intent.putExtra("email_address", landlordEmail);
            startActivity(intent);
        });

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandlordRequestsPageActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", landlordEmail);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    /**
     * Called when the landlord accepts a property request.
     * @param request The property request to accept.
     */

    public void onAcceptRequest(PropertyRequest request) {
        dbManager.updateRequestStatus(request.getId(), "accepted");
        dbManager.assignClientToProperty(request.getPropertyId(), request.getClientEmail());

        Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show();
        refreshRequests();
    }

    /**
     * Called when the landlord rejects a property request.
     * @param request The property request to reject.
     */

    public void onRejectRequest(PropertyRequest request) {
        dbManager.updateRequestStatus(request.getId(), "rejected");
        dbManager.deleteClientRequest(request.getId());

        Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();
        refreshRequests();
    }

    /**
     * Refreshes the list of requests after an accept or reject operation.
     */

    private void refreshRequests() {
        requestAdapter.updateRequests(dbManager.getRequestsByLandlord(landlordEmail));
    }
}
