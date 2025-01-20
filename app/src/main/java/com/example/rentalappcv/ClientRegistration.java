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

public class ClientRegistration extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText birthYear;
    private EditText confirmEmail;
    private EditText confirmPassword;
    private DatabaseManager dbManager;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText password;
    private Button registerClientButton;
    private Button selectImageButton;
    private String selectedImagePath;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_registration);

        // Initialize UI components
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        confirmEmail = findViewById(R.id.confirmEmail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        birthYear = findViewById(R.id.birthYear);
        registerClientButton = findViewById(R.id.registerClientButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.selectedImageView);

        // Initialize and open the database
        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Set click listener for selecting an image
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        // Set click listener for registration button
        registerClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });
    }

    private void handleRegistration() {
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String confirmEmailAddress = confirmEmail.getText().toString().trim();
        String pwd = password.getText().toString();
        String confirmPwd = confirmPassword.getText().toString();
        int bYear;

        // Validate birth year input
        try {
            bYear = Integer.parseInt(birthYear.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid birth year!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email and password confirmations
        if (!emailAddress.equals(confirmEmailAddress)) {
            Toast.makeText(this, "Emails do not match!", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(confirmPwd)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            // Create a new Client and register
            Client client = new Client(fname, lname, emailAddress, pwd, bYear, selectedImagePath);
            client.register(dbManager);
            Toast.makeText(this, "Client Registered Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

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
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the image to JPEG with 100% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        // Encode the image to Base64 string
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
