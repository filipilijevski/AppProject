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
 * Adapter for listing and handling manager requests in a RecyclerView.
 * Each request displays an address, manager email, and status,
 * with accept and reject options.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final Context context;
    private final DatabaseManager dbManager;
    private List<LandlordManagerRequest> requests;

    public RequestAdapter(Context context, List<LandlordManagerRequest> requests, DatabaseManager dbManager) {
        this.context = context;
        this.requests = requests;
        this.dbManager = dbManager;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_requests_landlord, parent, false);
        return new RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        LandlordManagerRequest request = requests.get(position);

        holder.addressTextView.setText(request.getAddress());
        holder.clientRequestName.setText(request.getManagerEmail());
        holder.clientTypeOfRequest.setText(request.getStatus());

        // Accept request
        holder.acceptRequestButton.setOnClickListener(v -> {
            if (context instanceof ManagerMessagesRequestsActivity) {
                ((ManagerMessagesRequestsActivity) context).onAcceptRequest(request);
            }
        });

        // Reject request
        holder.rejectRequestButton.setOnClickListener(v -> {
            if (context instanceof ManagerMessagesRequestsActivity) {
                ((ManagerMessagesRequestsActivity) context).onRejectRequest(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * Updates the list of requests and refreshes the adapter.
     *
     * @param newRequests the updated list of LandlordManagerRequest
     */

    public void updateRequests(List<LandlordManagerRequest> newRequests) {
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
