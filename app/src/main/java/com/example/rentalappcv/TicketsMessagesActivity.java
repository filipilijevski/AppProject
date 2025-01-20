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

import java.util.List;

/**
 * Activity that displays messages or tickets for the client’s properties,
 * allowing them to see any issues or updates associated with each property.
 */

public class TicketsMessagesActivity extends AppCompatActivity {

    private String clientEmail;
    private DatabaseManager dbManager;
    private ClientPropertyAdapter propertyAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_messages_tickets);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        clientEmail = getIntent().getStringExtra("email_address");

        recyclerView = findViewById(R.id.recyclerView_container_client_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load properties for this client
        List<Property> properties = dbManager.getPropertiesByClient(clientEmail);
        if (properties.isEmpty()) {
            Toast.makeText(this, "No properties found", Toast.LENGTH_SHORT).show();
        }
        propertyAdapter = new ClientPropertyAdapter(this, properties);
        recyclerView.setAdapter(propertyAdapter);

        // Home button container
        LinearLayout clientHomeButtonContainer = findViewById(R.id.clientHomeButtonContainer);
        clientHomeButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(TicketsMessagesActivity.this, ClientHomepageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });

        // Profile button
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicketsMessagesActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    /**
     * Provides external access to this activity's client email.
     *
     * @return the client’s email
     */

    public String getClientEmail() {
        return clientEmail;
    }
}
