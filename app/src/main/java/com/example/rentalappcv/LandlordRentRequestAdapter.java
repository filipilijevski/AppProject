package com.example.rentalappcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying properties when a manager is selected
 * and sending a manager request to the landlord's properties.
 */

public class LandlordRentRequestAdapter extends RecyclerView.Adapter<LandlordRentRequestAdapter.PropertyViewHolder> {

    private final Context context;
    private final DatabaseManager dbManager;
    private final Manager selectedManager;
    private List<Property> properties;

    public LandlordRentRequestAdapter(Context context, List<Property> properties, Manager selectedManager, DatabaseManager dbManager) {
        this.context = context;
        this.properties = properties;
        this.selectedManager = selectedManager;
        this.dbManager = dbManager;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_landlord_property_list, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = properties.get(position);

        // Populate property details
        holder.addressTextView.setText(property.getAddress());
        holder.numberOfBedroomsTextView.setText("Bedrooms: " + property.getRooms());
        holder.numberOfBathroomsTextView.setText("Bathrooms: " + property.getBathrooms());

        // Send a manager request when the button is clicked
        holder.assignManagerButton.setOnClickListener(v -> {
            dbManager.addManagerRequest(property.getId(), selectedManager.getEmailAddress(), property.getAddress());
            Toast.makeText(context, "Request to manage sent!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    /**
     * Updates the property list and refreshes the adapter.
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

        TextView addressTextView;
        TextView numberOfBedroomsTextView;
        TextView numberOfBathroomsTextView;
        Button assignManagerButton;
        ImageView propertyImageView;

        PropertyViewHolder(View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.managerImageView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            numberOfBedroomsTextView = itemView.findViewById(R.id.numberOfBedroomsTextView);
            numberOfBathroomsTextView = itemView.findViewById(R.id.numberOfBathroomsTextView);
            assignManagerButton = itemView.findViewById(R.id.assignManagerButton);
        }
    }
}
