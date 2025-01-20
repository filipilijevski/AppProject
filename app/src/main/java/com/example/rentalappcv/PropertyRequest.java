package com.example.rentalappcv;

/**
 * Represents a request made by a client to rent or view a property.
 * Stores relevant info such as property ID, client email, status, etc.
 */

public class PropertyRequest {

    private int id;
    private int propertyId;
    private String clientEmail;
    private String status;
    private String requestType;
    private String propertyAddress;
    private String clientProfileImage;

    // Constructors
    public PropertyRequest(int id,
                           int propertyId,
                           String clientEmail,
                           String status,
                           String requestType,
                           String propertyAddress,
                           String clientProfileImage) {
        this.id = id;
        this.propertyId = propertyId;
        this.clientEmail = clientEmail;
        this.status = status;
        this.requestType = requestType;
        this.propertyAddress = propertyAddress;
        this.clientProfileImage = clientProfileImage;
    }

    public PropertyRequest() {
        // Default constructor
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

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getClientProfileImage() {
        return clientProfileImage;
    }

    public void setClientProfileImage(String clientProfileImage) {
        this.clientProfileImage = clientProfileImage;
    }

    @Override
    public String toString() {
        return "PropertyRequest{" +
                "id=" + id +
                ", propertyId=" + propertyId +
                ", clientEmail='" + clientEmail + '\'' +
                ", status='" + status + '\'' +
                ", requestType='" + requestType + '\'' +
                ", propertyAddress='" + propertyAddress + '\'' +
                ", clientProfileImage='" + clientProfileImage + '\'' +
                '}';
    }
}
