package com.example.rentalappcv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Activity for registering a new landlord, including selecting a profile image.
 */

public class LandlordRegistration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText firstName, lastName, email, confirmEmail, password, confirmPassword, address;
    private Button registerButton, selectImageButton;
    private ImageView selectedImageView;
    private String selectedImagePath;

    // Database
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landlord_registration);

        // Initialize UI components
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        confirmEmail = findViewById(R.id.confirmEmail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.addressLandlordRegistration);
        registerButton = findViewById(R.id.registerLandlordButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.selectedImageView);

        // Set up and open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Image selection button
        selectImageButton.setOnClickListener(v -> openImageChooser());

        // Register button
        registerButton.setOnClickListener(v -> handleRegistration());
    }

    /**
     * Opens the image chooser for the user to pick a profile image.
     */

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the landlord registration logic.
     * Validates email, password, and stores the new Landlord in the database.
     */

    private void handleRegistration() {
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String confirmEmailAddress = confirmEmail.getText().toString().trim();
        String pwd = password.getText().toString();
        String confirmPwd = confirmPassword.getText().toString();
        String addr = address.getText().toString().trim();

        if (!emailAddress.equals(confirmEmailAddress)) {
            Toast.makeText(this, "Emails do not match!", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(confirmPwd)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            Landlord landlord = new Landlord(fname, lname, emailAddress, pwd, addr, selectedImagePath);
            landlord.register(dbManager);

            Toast.makeText(this, "Landlord Registered Successfully!", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    /**
     * Encodes the selected image to a Base64 string.
     */

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress image to JPEG with 100% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Navigate back to a login or previous screen upon successful registration.
     */

    private void navigateToLogin() {
        finish();
    }

    /**
     * Called when the image chooser returns a result.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                selectedImageView.setImageBitmap(bitmap);
                selectedImagePath = encodeImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
