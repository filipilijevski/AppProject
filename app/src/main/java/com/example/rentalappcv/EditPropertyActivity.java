package com.example.rentalappcv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 0;

    private String landlordEmail;
    private int propertyId;
    private String selectedImageBase64;

    // UI Components
    private ImageView selectedImageView;
    private EditText addressReg, typeReg, numberOfRoomsReg, numberOfBathroomsReg,
            numberOfFloorsReg, areaReg, parkingSpotsReg, rentReg;
    private SwitchCompat laundryInUnitSwitch;
    private CheckBox utilitiesIncludedHydro, utilitiesIncludedHeat, utilitiesIncludedWater;
    private Button selectImageButton, updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_property);

        // Initialize UI references
        selectedImageView = findViewById(R.id.selectedImageView);
        addressReg = findViewById(R.id.addressReg);
        typeReg = findViewById(R.id.typeReg);
        numberOfRoomsReg = findViewById(R.id.numberOfRoomsReg);
        numberOfBathroomsReg = findViewById(R.id.numberOfBathroomsReg);
        numberOfFloorsReg = findViewById(R.id.numberOfFloorsReg);
        laundryInUnitSwitch = findViewById(R.id.laundryInUnitSwitch);
        areaReg = findViewById(R.id.areaReg);
        parkingSpotsReg = findViewById(R.id.parkingSpotsReg);
        rentReg = findViewById(R.id.rentReg);
        utilitiesIncludedHydro = findViewById(R.id.utilitiesIncludedHydro);
        utilitiesIncludedHeat = findViewById(R.id.utilitiesIncludedHeat);
        utilitiesIncludedWater = findViewById(R.id.utilitiesIncludedWater);
        selectImageButton = findViewById(R.id.selectImageButton);
        updateButton = findViewById(R.id.updateButton);

        // Retrieve extras
        landlordEmail = getIntent().getStringExtra("landlord_email");
        propertyId = getIntent().getIntExtra("property_id", -1);

        // If we have a valid property ID, load the existing property from the database
        if (propertyId != -1) {
            DatabaseManager dbManager = new DatabaseManager(this);
            dbManager.open();
            Property property = dbManager.getPropertyById(propertyId);
            dbManager.close();

            if (property != null) {
                // Populate fields from the property
                addressReg.setText(property.getAddress());
                typeReg.setText(property.getType());
                numberOfRoomsReg.setText(String.valueOf(property.getRooms()));
                numberOfBathroomsReg.setText(String.valueOf(property.getBathrooms()));
                numberOfFloorsReg.setText(String.valueOf(property.getFloors()));
                laundryInUnitSwitch.setChecked(property.isLaundryInUnit());
                areaReg.setText(String.valueOf(property.getArea()));
                parkingSpotsReg.setText(String.valueOf(property.getParkingSpots()));
                rentReg.setText(String.valueOf(property.getRent()));
                utilitiesIncludedHydro.setChecked(property.isHydroIncluded());
                utilitiesIncludedHeat.setChecked(property.isHeatIncluded());
                utilitiesIncludedWater.setChecked(property.isWaterIncluded());

                // If property has images, display the first one
                if (property.getImages() != null && !property.getImages().isEmpty()) {
                    String imageUrl = property.getImages().get(0);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                        selectedImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                        selectedImageBase64 = imageUrl;
                    }
                }
            }
        }

        // Set up listeners
        selectImageButton.setOnClickListener(v -> openImageChooser());
        updateButton.setText("Update Property");
        updateButton.setOnClickListener(v -> handleUpdateProperty());
    }

    /**
     * Opens the system image chooser to select a property image.
     */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the "Update Property" button click.
     * Validates fields, constructs a Property object, and triggers the update via AsyncTask.
     */
    private void handleUpdateProperty() {
        if (!allFieldsFilled()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct the updated Property
        Property property = new Property();
        property.setId(propertyId);
        property.setAddress(addressReg.getText().toString());
        property.setType(typeReg.getText().toString());
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

        // If a new image has been selected, add it to the property
        if (selectedImageBase64 != null) {
            List<String> images = new ArrayList<>();
            images.add(selectedImageBase64);
            property.setImages(images);
        }

        // Run the update task in the background
        new UpdatePropertyTask(property).execute();
    }

    /**
     * Checks whether all required fields have been filled in.
     */
    private boolean allFieldsFilled() {
        return !addressReg.getText().toString().isEmpty()
                && !typeReg.getText().toString().isEmpty()
                && !numberOfRoomsReg.getText().toString().isEmpty()
                && !numberOfBathroomsReg.getText().toString().isEmpty()
                && !numberOfFloorsReg.getText().toString().isEmpty()
                && !areaReg.getText().toString().isEmpty()
                && !parkingSpotsReg.getText().toString().isEmpty()
                && !rentReg.getText().toString().isEmpty();
    }

    /**
     * Receives the result from the image chooser and encodes the selected image to Base64.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                selectedImageView.setImageBitmap(bitmap);
                selectedImageBase64 = encodeImageToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Encodes a Bitmap image into a Base64 string.
     */
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    /**
     * AsyncTask to update the property in the database.
     */
    private class UpdatePropertyTask extends AsyncTask<Void, Void, Boolean> {
        private final Property property;

        public UpdatePropertyTask(Property property) {
            this.property = property;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Perform geocoding update
                double[] latLong = GeocodingUtils.getLatLongFromAddress(property.getAddress());
                property.setLatitude(latLong[0]);
                property.setLongitude(latLong[1]);

                // Update property in DB
                DatabaseManager dbManager = new DatabaseManager(EditPropertyActivity.this);
                dbManager.open();
                dbManager.updateProperty(property);
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
                Toast.makeText(EditPropertyActivity.this, "Property updated successfully!", Toast.LENGTH_SHORT).show();
                // Navigate back to landlord homepage
                Intent intent = new Intent(EditPropertyActivity.this, LandlordHomepageActivity.class);
                intent.putExtra("email_address", landlordEmail);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EditPropertyActivity.this, "Error updating property.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
