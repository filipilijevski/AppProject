package com.example.rentalappcv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to register a new Property, including uploading an image and
 * specifying details such as rooms, bathrooms, rent, etc.
 */

public class PropertyRegistration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 0;

    private String landlordEmail;
    private String selectedImageBase64;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_registration);

        // Retrieve landlord email
        landlordEmail = getIntent().getStringExtra("email_address");

        // Initialize UI components
        selectedImageView = findViewById(R.id.selectedImageView);
        EditText addressReg = findViewById(R.id.addressReg);
        EditText typeReg = findViewById(R.id.typeReg);
        EditText numberOfRoomsReg = findViewById(R.id.numberOfRoomsReg);
        EditText numberOfBathroomsReg = findViewById(R.id.numberOfBathroomsReg);
        EditText numberOfFloorsReg = findViewById(R.id.numberOfFloorsReg);
        EditText areaReg = findViewById(R.id.areaReg);
        SwitchCompat laundryInUnitSwitch = findViewById(R.id.laundryInUnitSwitch);
        EditText parkingSpotsReg = findViewById(R.id.parkingSpotsReg);
        EditText rentReg = findViewById(R.id.rentReg);

        CheckBox utilitiesIncludedHydro = findViewById(R.id.utilitiesIncludedHydro);
        CheckBox utilitiesIncludedHeat = findViewById(R.id.utilitiesIncludedHeat);
        CheckBox utilitiesIncludedWater = findViewById(R.id.utilitiesIncludedWater);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button registerButton = findViewById(R.id.registerButton);

        // Set up image selection
        selectImageButton.setOnClickListener(v -> openImageChooser());

        // Register button logic
        registerButton.setOnClickListener(v -> {
            if (!allFieldsFilled(
                    addressReg,
                    typeReg,
                    numberOfRoomsReg,
                    numberOfBathroomsReg,
                    numberOfFloorsReg,
                    areaReg,
                    parkingSpotsReg,
                    rentReg
            )) {
                Toast.makeText(PropertyRegistration.this,
                        "Please fill in all fields.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Construct Property object
            Property property = new Property();
            property.setAddress(addressReg.getText().toString().trim());
            property.setType(typeReg.getText().toString().trim());
            property.setRooms(Integer.parseInt(numberOfRoomsReg.getText().toString()));
            property.setBathrooms(Integer.parseInt(numberOfBathroomsReg.getText().toString()));
            property.setFloors(Integer.parseInt(numberOfFloorsReg.getText().toString()));
            property.setArea(Integer.parseInt(areaReg.getText().toString()));
            property.setLaundryInUnit(laundryInUnitSwitch.isChecked());
            property.setParkingSpots(Integer.parseInt(parkingSpotsReg.getText().toString()));
            property.setRent(Integer.parseInt(rentReg.getText().toString()));
            property.setHydroIncluded(utilitiesIncludedHydro.isChecked());
            property.setHeatIncluded(utilitiesIncludedHeat.isChecked());
            property.setWaterIncluded(utilitiesIncludedWater.isChecked());
            property.setLandlordEmail(landlordEmail);
            property.setCommission(0);
            property.setOccupied(false);
            property.setManager(null);
            property.setClient(null);

            // Attach image if selected
            if (selectedImageBase64 != null) {
                List<String> images = new ArrayList<>();
                images.add(selectedImageBase64);
                property.setImages(images);
            }

            // Register property asynchronously
            new RegisterPropertyTask(property).execute();
        });
    }

    /**
     * Opens the system image picker to select a property image.
     */

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Receives the result from the image chooser and encodes the selected image.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(data.getData())
                );
                selectedImageView.setImageBitmap(bitmap);
                selectedImageBase64 = encodeImageToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Encodes the selected image to a Base64 string for storage.
     */

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Checks if required fields are non-empty.
     */

    private boolean allFieldsFilled(EditText... fields) {
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * AsyncTask to register the property in the background, including geocoding.
     */

    private class RegisterPropertyTask extends AsyncTask<Void, Void, Boolean> {
        private final Property property;

        public RegisterPropertyTask(Property property) {
            this.property = property;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Fetch latitude and longitude using GeocodingUtils
                double[] latLong = GeocodingUtils.getLatLongFromAddress(property.getAddress());
                property.setLatitude(latLong[0]);
                property.setLongitude(latLong[1]);

                // Insert property into DB
                DatabaseManager dbManager = new DatabaseManager(PropertyRegistration.this);
                dbManager.open();
                dbManager.addProperty(property);
                dbManager.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(
                        PropertyRegistration.this,
                        "Property registered successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                // Navigate to LandlordHomepageActivity
                Intent intent = new Intent(PropertyRegistration.this, LandlordHomepageActivity.class);
                intent.putExtra("email_address", landlordEmail);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(
                        PropertyRegistration.this,
                        "Error registering property.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
