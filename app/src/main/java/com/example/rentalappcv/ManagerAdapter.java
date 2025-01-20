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
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display a list of Manager objects in a RecyclerView.
 * Provides filtering by name and handles user interactions like
 * viewing the full manager's info or assigning the manager to a property.
 */

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {

    private final Context context;
    private List<Manager> managers;
    private final List<Manager> managersFull;

    public ManagerAdapter(Context context, List<Manager> managers) {
        this.context = context;
        this.managers = managers;
        // Keep a full copy of the manager list for filtering purposes
        this.managersFull = new ArrayList<>(managers);
    }

    @Override
    public ManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_manager, parent, false);
        return new ManagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManagerViewHolder holder, int position) {
        Manager manager = managers.get(position);

        // Display manager's name and median rating
        holder.nameTextView.setText(manager.getFirstName());
        holder.ratingTextView.setText("Rating: " + manager.getMedianRating());

        // Load manager profile picture (Base64)
        String imageUrl = manager.getProfilePicture();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.managerImageView.setImageResource(R.drawable.default_profile_picture);
        } else {
            byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
            holder.managerImageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
            );
        }

        // View full manager info
        holder.managerImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullManagerInformation.class);
            intent.putExtra("email", manager.getEmailAddress());
            context.startActivity(intent);
        });

        // Assign manager to a property (Landlord flow)
        holder.assignManagerButton.setOnClickListener(v -> {
            if (context instanceof LandlordHomepageActivity) {
                ((LandlordHomepageActivity) context).assignManagerToProperty(manager);
            }
        });
    }

    @Override
    public int getItemCount() {
        return managers.size();
    }

    /**
     * Updates the list of managers displayed and notifies the adapter.
     *
     * @param newManagers the updated list of managers
     */

    public void updateManagers(List<Manager> newManagers) {
        this.managers = newManagers;
        notifyDataSetChanged();
    }

    /**
     * Filters the displayed managers based on the provided text.
     *
     * @param text the text query used to filter managers by their first or last name.
     */

    public void filter(String text) {
        managers.clear();
        if (text.isEmpty()) {
            managers.addAll(managersFull);
        } else {
            String lowerQuery = text.toLowerCase();
            for (Manager manager : managersFull) {
                if (manager.getFirstName().toLowerCase().contains(lowerQuery)
                        || manager.getLastName().toLowerCase().contains(lowerQuery)) {
                    managers.add(manager);
                }
            }
        }
        notifyDataSetChanged();
    }

    // ------------------------------------------------
    // Inner ViewHolder class
    // ------------------------------------------------
    static class ManagerViewHolder extends RecyclerView.ViewHolder {
        ImageView managerImageView;
        TextView nameTextView;
        TextView ratingTextView;
        Button assignManagerButton;

        ManagerViewHolder(View itemView) {
            super(itemView);
            managerImageView = itemView.findViewById(R.id.managerImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            assignManagerButton = itemView.findViewById(R.id.assignManagerButton);
        }
    }
}
