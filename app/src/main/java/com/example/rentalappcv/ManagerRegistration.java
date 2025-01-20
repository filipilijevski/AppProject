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
 * Activity for registering a new Manager, including selecting a profile image.
 */

public class ManagerRegistration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText firstName, lastName, email, confirmEmail, password, confirmPassword;
    private Button registerButton, selectImageButton;
    private ImageView selectedImageView;

    // Other fields
    private DatabaseManager dbManager;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_registration);

        // Initialize UI components
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        confirmEmail = findViewById(R.id.confirmEmail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerManagerButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.selectedImageView);

        // Initialize and open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Set click listener for selecting an image
        selectImageButton.setOnClickListener(v -> openImageChooser());

        // Set click listener for registration
        registerButton.setOnClickListener(v -> handleRegistration());
    }

    /**
     * Opens the image chooser for selecting a profile picture.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the manager registration logic after validating inputs.
     */

    private void handleRegistration() {
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String confirmEmailAddress = confirmEmail.getText().toString().trim();
        String pwd = password.getText().toString();
        String confirmPwd = confirmPassword.getText().toString();

        if (!emailAddress.equals(confirmEmailAddress)) {
            Toast.makeText(this, "Emails do not match!", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(confirmPwd)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            Manager manager = new Manager(fname, lname, emailAddress, pwd, selectedImagePath);
            manager.register(dbManager);

            Toast.makeText(this, "Property Manager Registered Successfully!", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    /**
     * Receives the result from image chooser, encodes the selected image to Base64.
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

    /**
     * Encodes a Bitmap to a Base64 string for storing as a profile picture.
     */

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress image to JPEG with 100% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Navigates back to the login or previous screen upon successful registration.
     */

    private void navigateToLogin() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
