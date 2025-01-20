package com.example.rentalappcv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying LandlordManagerRequest items to a manager,
 * allowing accept or reject operations.
 */

public class ManagerRequestAdapter extends RecyclerView.Adapter<ManagerRequestAdapter.ManagerRequestViewHolder> {

    private final Context context;
    private final DatabaseManager dbManager;
    private final List<LandlordManagerRequest> requests;

    public ManagerRequestAdapter(Context context, List<LandlordManagerRequest> requests, DatabaseManager dbManager) {
        this.context = context;
        this.requests = requests;
        this.dbManager = dbManager;
    }

    @Override
    public ManagerRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_requests_landlord, parent, false);
        return new ManagerRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManagerRequestViewHolder holder, int position) {
        LandlordManagerRequest request = requests.get(position);

        // Populate request details
        holder.addressTextView.setText(request.getAddress());

        // Accept request logic
        holder.acceptRequestButton.setOnClickListener(v -> {
            request.setStatus("accepted");
            dbManager.updateRequestStatus(request.getId(), "accepted");
            dbManager.assignManagerToProperty(request.getPropertyId(), request.getManagerEmail());

            // Mark other requests for the same property as invalid
            for (LandlordManagerRequest r : requests) {
                if (r.getPropertyId() == request.getPropertyId() && r.getId() != request.getId()) {
                    dbManager.updateRequestStatus(r.getId(), "invalid");
                }
            }

            Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        });

        // Reject request logic
        holder.rejectRequestButton.setOnClickListener(v -> {
            dbManager.deleteRequest(request.getId());
            Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show();

            // Remove this item from the list and notify
            requests.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, requests.size());
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    // ------------------------------------------------
    // Inner ViewHolder class
    // ------------------------------------------------
    static class ManagerRequestViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        Button acceptRequestButton;
        Button rejectRequestButton;

        ManagerRequestViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            acceptRequestButton = itemView.findViewById(R.id.acceptRequestButton);
            rejectRequestButton = itemView.findViewById(R.id.rejectRequestButton);
        }
    }
}
