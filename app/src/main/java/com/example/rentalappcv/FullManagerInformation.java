package com.example.rentalappcv;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FullManagerInformation extends AppCompatActivity {

    private DatabaseManager dbManager;
    private TextView emailTextView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView medianRatingTextView;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_full_information);

        // Open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Initialize UI references
        firstNameTextView = findViewById(R.id.firstName);
        lastNameTextView = findViewById(R.id.lastName);
        emailTextView = findViewById(R.id.email);
        medianRatingTextView = findViewById(R.id.medianRating);
        profileImageView = findViewById(R.id.selectedImageView);

        // Retrieve the manager by email
        String managerEmail = getIntent().getStringExtra("email");
        Manager manager = (Manager) dbManager.getUser(managerEmail);

        // Populate UI if manager is found
        if (manager != null) {
            firstNameTextView.setText(manager.getFirstName());
            lastNameTextView.setText(manager.getLastName());
            emailTextView.setText(manager.getEmailAddress());
            medianRatingTextView.setText(String.valueOf(manager.getMedianRating()));

            // Decode and display profile picture if available
            String profilePicture = manager.getProfilePicture();
            if (profilePicture != null && !profilePicture.isEmpty()) {
                byte[] imageBytes = Base64.decode(profilePicture, Base64.DEFAULT);
                profileImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
        }

        dbManager.close();
    }
}
