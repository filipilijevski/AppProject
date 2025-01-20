package com.example.rentalappcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Activity that displays manager requests and messages.
 * Allows accepting or rejecting LandlordManagerRequests.
 */

public class ManagerMessagesRequestsActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private String email;
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_messages_requests);

        // Open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve the manager's email from the intent
        email = getIntent().getStringExtra("email_address");

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView_container_landlord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the RequestAdapter with manager requests
        requestAdapter = new RequestAdapter(this, dbManager.getRequestsByManager(email),dbManager);
        recyclerView.setAdapter(requestAdapter);

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerMessagesRequestsActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Manager history button
        ImageView managerHistoryButton = findViewById(R.id.managerHistoryButton);
        managerHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerMessagesRequestsActivity.this, ManagerHistoryActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });

        // Manager home button
        ImageView managerHomeButton = findViewById(R.id.managerHomeButton);
        managerHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerMessagesRequestsActivity.this, ManagerHomepageActivity.class);
            intent.putExtra("email_address", email);
            startActivity(intent);
        });
    }

    /**
     * Handles accepting a LandlordManagerRequest by the manager.
     *
     * @param request The request to accept.
     */

    public void onAcceptRequest(LandlordManagerRequest request) {
        dbManager.updateRequestStatus(request.getId(), "accepted");
        dbManager.assignManagerToProperty(request.getPropertyId(), request.getManagerEmail());

        Toast.makeText(this, "Request accepted and manager assigned to property", Toast.LENGTH_SHORT).show();
        refreshRequests();
    }

    /**
     * Handles rejecting a LandlordManagerRequest by the manager.
     *
     * @param request The request to reject.
     */

    public void onRejectRequest(LandlordManagerRequest request) {
        dbManager.deleteRequest(request.getId());
        Toast.makeText(this, "Request rejected and removed", Toast.LENGTH_SHORT).show();
        refreshRequests();
    }

    /**
     * Refreshes the request list after an accept or reject operation.
     */

    private void refreshRequests() {
        requestAdapter.updateRequests(dbManager.getRequestsByManager(email));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
