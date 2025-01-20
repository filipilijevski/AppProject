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
 * Adapter for displaying and managing client requests (PropertyRequest objects)
 * within a Landlord context.
 */

public class LandlordRequestAdapter extends RecyclerView.Adapter<LandlordRequestAdapter.RequestViewHolder> {

    private final Context context;
    private final DatabaseManager dbManager;
    private List<PropertyRequest> requests;

    public LandlordRequestAdapter(Context context, List<PropertyRequest> requests, DatabaseManager dbManager) {
        this.context = context;
        this.requests = requests;
        this.dbManager = dbManager;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.client_requests_landlord_view, parent, false);
        return new RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        PropertyRequest request = requests.get(position);

        // Populate request details
        holder.addressTextView.setText(request.getPropertyAddress());
        holder.clientRequestName.setText(request.getClientEmail());
        holder.clientTypeOfRequest.setText(request.getRequestType());

        // Accept request
        holder.acceptRequestButton.setOnClickListener(v -> {
            if (context instanceof LandlordRequestsPageActivity) {
                ((LandlordRequestsPageActivity) context).onAcceptRequest(request);
            }
        });

        // Reject request
        holder.rejectRequestButton.setOnClickListener(v -> {
            if (context instanceof LandlordRequestsPageActivity) {
                ((LandlordRequestsPageActivity) context).onRejectRequest(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * Updates the request list and refreshes the adapter.
     *
     * @param newRequests the updated list of requests
     */

    public void updateRequests(List<PropertyRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    // ------------------------------------------------
    // Inner ViewHolder class
    // ------------------------------------------------
    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView addressTextView;
        TextView clientRequestName;
        TextView clientTypeOfRequest;
        Button acceptRequestButton;
        Button rejectRequestButton;

        RequestViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            clientRequestName = itemView.findViewById(R.id.clientRequestName);
            clientTypeOfRequest = itemView.findViewById(R.id.clientTypeOfRequest);
            acceptRequestButton = itemView.findViewById(R.id.acceptRequestButton);
            rejectRequestButton = itemView.findViewById(R.id.rejectRequestButton);
        }
    }
}
