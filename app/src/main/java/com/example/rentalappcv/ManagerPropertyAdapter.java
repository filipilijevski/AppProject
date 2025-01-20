package com.example.rentalappcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying a manager's properties in a RecyclerView.
 * Each item shows basic property details like address and type.
 */

public class ManagerPropertyAdapter extends RecyclerView.Adapter<ManagerPropertyAdapter.PropertyViewHolder> {

    private final Context context;
    private final List<Property> properties;

    public ManagerPropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_manager_property_list, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = properties.get(position);

        holder.propertyAddressTextView.setText(property.getAddress());
        holder.propertyTypeTextView.setText(property.getType());
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    // ------------------------------------------------
    // Inner ViewHolder class
    // ------------------------------------------------
    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView propertyAddressTextView;
        TextView propertyTypeTextView;

        PropertyViewHolder(View itemView) {
            super(itemView);
            propertyAddressTextView = itemView.findViewById(R.id.propertyAddressTextView);
            propertyTypeTextView = itemView.findViewById(R.id.propertyTypeTextView);
        }
    }
}
