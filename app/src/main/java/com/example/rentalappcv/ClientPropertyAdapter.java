package com.example.rentalappcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClientPropertyAdapter extends RecyclerView.Adapter<ClientPropertyAdapter.PropertyViewHolder> {
    private Context context;
    private List<Property> properties;

    public ClientPropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.item_client_property_list, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = properties.get(position);

        holder.addressTextView.setText(property.getAddress());
        holder.typeTextView.setText(property.getType());
        holder.roomsTextView.setText(String.valueOf(property.getRooms()));

        // Load image if available, otherwise set a default icon
        String imageUrl = property.getImages().isEmpty() ? null : property.getImages().get(0);
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.propertyImageView.setImageResource(R.drawable.homeicon);
        } else {
            byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
            holder.propertyImageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
            );
        }

        // Set click listener for the item view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClientRatingManagerActivity.class);
            intent.putExtra("property_id", property.getId());

            // Safely cast context to TicketsMessagesActivity to get clientEmail, if applicable
            if (context instanceof TicketsMessagesActivity) {
                String clientEmail = ((TicketsMessagesActivity) context).getClientEmail();
                intent.putExtra("client_email", clientEmail);
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void updateProperties(List<Property> newProperties) {
        this.properties = newProperties;
        notifyDataSetChanged();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        ImageView propertyImageView;
        TextView roomsTextView;
        TextView typeTextView;

        PropertyViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            roomsTextView = itemView.findViewById(R.id.roomsTextView);
            propertyImageView = itemView.findViewById(R.id.propertyImageView);
        }
    }
}
