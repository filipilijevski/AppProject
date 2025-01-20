package com.example.rentalappcv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ClientHomepageActivity extends AppCompatActivity {
    private CheckBox anyPropertyCheckbox;
    private EditText areaReg;
    private TextView bathroomsCount;
    private TextView bedroomsCount;
    private String clientEmail;
    private CheckBox commercialCheckbox;
    private CheckBox condoCheckbox;
    private TextView floorsCount;
    private CheckBox housesCheckbox;
    private EditText rentReg;
    private EditText searchBar;
    private CheckBox utilitiesIncludedHeat;
    private CheckBox utilitiesIncludedHydro;
    private CheckBox utilitiesIncludedWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Retrieve email from intent and store it in SharedPreferences
        clientEmail = getIntent().getStringExtra("email_address");
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString("client_email", clientEmail);
        editor.apply();

        // Initialize UI components
        searchBar = findViewById(R.id.searchBar);
        bedroomsCount = findViewById(R.id.bedroomsCount);
        bathroomsCount = findViewById(R.id.bathroomsCount);
        floorsCount = findViewById(R.id.floorsCount);
        rentReg = findViewById(R.id.rentEditText);
        areaReg = findViewById(R.id.areaEditText);
        utilitiesIncludedHydro = findViewById(R.id.hydroCheckbox);
        utilitiesIncludedHeat = findViewById(R.id.heatCheckbox);
        utilitiesIncludedWater = findViewById(R.id.waterCheckbox);
        housesCheckbox = findViewById(R.id.housesCheckbox);
        condoCheckbox = findViewById(R.id.condoCheckbox);
        commercialCheckbox = findViewById(R.id.commercialCheckbox);
        anyPropertyCheckbox = findViewById(R.id.anyPropertyCheckbox);

        // Set up click listeners for increment/decrement buttons
        findViewById(R.id.bedroomsIncrement).setOnClickListener(v -> incrementCount(bedroomsCount));
        findViewById(R.id.bedroomsDecrement).setOnClickListener(v -> decrementCount(bedroomsCount));
        findViewById(R.id.bathroomsIncrement).setOnClickListener(v -> incrementCount(bathroomsCount));
        findViewById(R.id.bathroomsDecrement).setOnClickListener(v -> decrementCount(bathroomsCount));
        findViewById(R.id.floorsIncrement).setOnClickListener(v -> incrementCount(floorsCount));
        findViewById(R.id.floorsDecrement).setOnClickListener(v -> decrementCount(floorsCount));

        ((Button) findViewById(R.id.searchButton)).setOnClickListener(v -> initiateSearch());

        ((ImageView) findViewById(R.id.profileButton)).setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, ProfilePageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });

        ((ImageView) findViewById(R.id.historyButton)).setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, ClientHistoryActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });

        ((ImageView) findViewById(R.id.homeButton)).setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, ClientHomepageActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });

        ((LinearLayout) findViewById(R.id.clientMessagesButtonContainer)).setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, TicketsMessagesActivity.class);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
        });
    }

    private void incrementCount(TextView textView) {
        int currentCount = Integer.parseInt(textView.getText().toString());
        textView.setText(String.valueOf(currentCount + 1));
    }

    private void decrementCount(TextView textView) {
        int currentCount = Integer.parseInt(textView.getText().toString());
        if (currentCount > 0) {
            textView.setText(String.valueOf(currentCount - 1));
        }
    }

    private void initiateSearch() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchBar", searchBar.getText().toString());
        intent.putExtra("numberOfRooms", bedroomsCount.getText().toString());
        intent.putExtra("numberOfBathrooms", bathroomsCount.getText().toString());
        intent.putExtra("numberOfFloors", floorsCount.getText().toString());
        intent.putExtra("rent", rentReg.getText().toString());
        intent.putExtra("area", areaReg.getText().toString());
        intent.putExtra("utilitiesIncludedHydro", utilitiesIncludedHydro.isChecked());
        intent.putExtra("utilitiesIncludedHeat", utilitiesIncludedHeat.isChecked());
        intent.putExtra("utilitiesIncludedWater", utilitiesIncludedWater.isChecked());
        intent.putExtra("houses", housesCheckbox.isChecked());
        intent.putExtra("condo", condoCheckbox.isChecked());
        intent.putExtra("commercial", commercialCheckbox.isChecked());
        intent.putExtra("anyProperty", anyPropertyCheckbox.isChecked());
        intent.putExtra("email_address", clientEmail);
        startActivity(intent);
    }
}
