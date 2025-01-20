package com.example.rentalappcv;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays the user's profile information (name, profile picture)
 * and provides options to return "home" or log out.
 */

public class ProfilePageActivity extends AppCompatActivity {

    private String clientEmail;
    private DatabaseManager dbManager;
    private ImageView homeButton;
    private Button logoutButton;
    private TextView userName;
    private ImageView userProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Initialize UI components
        userProfilePicture = findViewById(R.id.userProfilePicture);
        userName = findViewById(R.id.userName);
        logoutButton = findViewById(R.id.logoutButton);
        homeButton = findViewById(R.id.homeButton);

        // Open database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Retrieve user email from Intent
        clientEmail = getIntent().getStringExtra("email_address");
        if (clientEmail == null) {
            Toast.makeText(this, "No email provided. Cannot load user profile.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load user data
        User user = dbManager.getUser(clientEmail);
        if (user != null) {
            userName.setText(user.getFirstName() + " " + user.getLastName());

            // Determine profile picture source based on user role
            String imageUrl = null;
            if (user instanceof Client) {
                imageUrl = ((Client) user).getProfilePicture();
            } else if (user instanceof Landlord) {
                imageUrl = ((Landlord) user).getProfilePicture();
            } else if (user instanceof Manager) {
                imageUrl = ((Manager) user).getProfilePicture();
            }

            // Decode and display profile picture if available
            if (imageUrl != null && !imageUrl.isEmpty()) {
                byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                userProfilePicture.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
        } else {
            Toast.makeText(this, "User not found with the provided email.", Toast.LENGTH_LONG).show();
        }

        // Logout button listener
        logoutButton.setOnClickListener(v -> {
            // Redirect to MainActivity with flags to clear task
            Intent intent = new Intent(ProfilePageActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Home button listener
        homeButton.setOnClickListener(v -> {
            Intent intent;
            if (user instanceof Client) {
                intent = new Intent(ProfilePageActivity.this, ClientHomepageActivity.class);
            } else if (user instanceof Landlord) {
                intent = new Intent(ProfilePageActivity.this, LandlordHomepageActivity.class);
            } else if (user instanceof Manager) {
                intent = new Intent(ProfilePageActivity.this, ManagerHomepageActivity.class);
            } else {
                intent = new Intent(ProfilePageActivity.this, MainActivity.class);
            }

            // Clear activity stack and open the homepage
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email_address", clientEmail);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
