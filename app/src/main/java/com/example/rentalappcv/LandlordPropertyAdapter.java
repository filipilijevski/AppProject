package com.example.rentalappcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying properties in a landlord's context,
 * allowing manager assignment via a button.
 */

public class LandlordPropertyAdapter extends RecyclerView.Adapter<LandlordPropertyAdapter.PropertyViewHolder> {

    private Context context;
    private List<Property> properties;

    /**
     * Constructs a LandlordPropertyAdapter with the given context and property list.
     *
     * @param context   the context (should be an instance of LandlordHomepageActivity)
     * @param properties the list of properties to display
     */

    public LandlordPropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_landlord_property_list, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        final Property property = properties.get(position);

        holder.addressTextView.setText(property.getAddress());
        holder.numberOfBedroomsTextView.setText("Bedrooms: " + property.getRooms());
        holder.numberOfBathroomsTextView.setText("Bathrooms: " + property.getBathrooms());

        // Assign manager button triggers confirmation on the landlord homepage
        holder.assignManagerButton.setOnClickListener(v -> {
            if (context instanceof LandlordHomepageActivity) {
                ((LandlordHomepageActivity) context).confirmManagerAssignment(property);
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    /**
     * Updates the internal list of properties and refreshes the RecyclerView.
     *
     * @param newProperties the updated property list
     */

    public void updateProperties(List<Property> newProperties) {
        this.properties = newProperties;
        notifyDataSetChanged();
    }

    // ----------------------------------------------------------------
    // Inner ViewHolder class for displaying a single property's data
    // ----------------------------------------------------------------
    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        TextView numberOfBedroomsTextView;
        TextView numberOfBathroomsTextView;
        Button assignManagerButton;

        PropertyViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            numberOfBedroomsTextView = itemView.findViewById(R.id.numberOfBedroomsTextView);
            numberOfBathroomsTextView = itemView.findViewById(R.id.numberOfBathroomsTextView);
            assignManagerButton = itemView.findViewById(R.id.assignManagerButton);
        }
    }
}
