package com.example.rentalappcv;

/**
 * Represents a request from a manager to manage a landlord's property.
 * Includes relevant IDs and status for tracking.
 */

public class LandlordManagerRequest {
    private int id;
    private int propertyId;
    private String managerEmail;
    private String status;
    private String address;

    public LandlordManagerRequest() {
        // Default constructor
    }

    public LandlordManagerRequest(int id, int propertyId, String managerEmail, String status, String address) {
        this.id = id;
        this.propertyId = propertyId;
        this.managerEmail = managerEmail;
        this.status = status;
        this.address = address;
    }

    // ---------------------------
    // Getters and Setters
    // ---------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
