package com.example.rentalappcv;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity mContext;
    private final View mWindow;

    public CustomInfoWindowAdapter(Activity context) {
        this.mContext = context;
        // Inflate the custom info window layout
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.property_listing, null);
    }

    private void renderWindowText(Marker marker, View view) {
        // Initialize UI elements in the info window
        ImageView image = view.findViewById(R.id.property_image_box);
        TextView address = view.findViewById(R.id.info_window_address);
        TextView bedrooms = view.findViewById(R.id.info_window_bedrooms);
        TextView bathrooms = view.findViewById(R.id.info_window_bathrooms);
        TextView rent = view.findViewById(R.id.info_window_rent);

        // Retrieve the property associated with this marker
        Property property = (Property) marker.getTag();

        if (property != null) {
            // Set property details in the info window
            bedrooms.setText("Bedrooms: " + property.getRooms());
            bathrooms.setText("Bathrooms: " + property.getBathrooms());
            rent.setText("Rent: $" + property.getRent());
            address.setText(property.getAddress());

            // Load property image using Picasso, if available
            if (property.getImages() != null && !property.getImages().isEmpty()) {
                Picasso.get().load(property.getImages().get(0)).into(image);
            }
        }

        // Set a click listener for the entire info window view
        view.setOnClickListener(v -> {
            if (property != null) {
                Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                intent.putExtra("property_id", property.getId());
                // Retrieve client email from shared preferences
                String clientEmail = mContext.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE)
                        .getString("client_email", null);
                intent.putExtra("client_email", clientEmail);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Customize the entire info window
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Alternatively, customize only the contents of the info window
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
