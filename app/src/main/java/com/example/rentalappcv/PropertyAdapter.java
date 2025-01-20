package com.example.rentalappcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter to display a list of Property objects in a RecyclerView,
 * with an option to edit the property (for landlords).
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private final Context context;
    private List<Property> properties;

    public PropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = properties.get(position);

        // Basic property details
        holder.addressTextView.setText(property.getAddress());
        holder.numberOfBedroomsTextView.setText("Bedrooms: " + property.getRooms());
        holder.numberOfBathroomsTextView.setText("Bathrooms: " + property.getBathrooms());

        // Edit button -> navigate to EditPropertyActivity
        holder.editPropertyButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPropertyActivity.class);
            intent.putExtra("property_id", property.getId());
            intent.putExtra("landlord_email", property.getLandlordEmail());
            context.startActivity(intent);
        });

        // Display the first property image if available
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            String imageUrl = property.getImages().get(0);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                holder.propertyImageView.setImageBitmap(
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
                );
            }
        }
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    /**
     * Updates the list of properties and refreshes the RecyclerView.
     *
     * @param newProperties the updated list of properties
     */

    public void updateProperties(List<Property> newProperties) {
        this.properties = newProperties;
        notifyDataSetChanged();
    }

    // ------------------------------------------------
    // Inner ViewHolder class
    // ------------------------------------------------
    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImageView;
        TextView addressTextView;
        TextView numberOfBedroomsTextView;
        TextView numberOfBathroomsTextView;
        Button editPropertyButton;

        PropertyViewHolder(View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.managerImageView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            numberOfBedroomsTextView = itemView.findViewById(R.id.numberOfBedroomsTextView);
            numberOfBathroomsTextView = itemView.findViewById(R.id.numberOfBathroomsTextView);
            editPropertyButton = itemView.findViewById(R.id.editPropertyButton);
        }
    }
}
