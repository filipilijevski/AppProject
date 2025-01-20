package com.example.rentalappcv;

import java.util.List;

/**
 * Represents a rental property with attributes like address,
 * rooms, bathrooms, included utilities, and associated owner/manager.
 */

public class Property {

    private int id;
    private String address;
    private String type;
    private int rooms;
    private int bathrooms;
    private int floors;
    private int area;
    private boolean laundryInUnit;
    private int parkingSpots;
    private int rent;
    private boolean hydroIncluded;
    private boolean heatIncluded;
    private boolean waterIncluded;
    private boolean occupied;
    private int commission;
    private String landlordEmail;
    private double latitude;
    private double longitude;
    private List<String> images;
    private Manager manager;
    private Client client;

    // ---------------------------
    // Getters and Setters
    // ---------------------------

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public boolean isLaundryInUnit() {
        return laundryInUnit;
    }

    public void setLaundryInUnit(boolean laundryInUnit) {
        this.laundryInUnit = laundryInUnit;
    }

    public int getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(int parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public boolean isHydroIncluded() {
        return hydroIncluded;
    }

    public void setHydroIncluded(boolean hydroIncluded) {
        this.hydroIncluded = hydroIncluded;
    }

    public boolean isHeatIncluded() {
        return heatIncluded;
    }

    public void setHeatIncluded(boolean heatIncluded) {
        this.heatIncluded = heatIncluded;
    }

    public boolean isWaterIncluded() {
        return waterIncluded;
    }

    public void setWaterIncluded(boolean waterIncluded) {
        this.waterIncluded = waterIncluded;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public void setLandlordEmail(String landlordEmail) {
        this.landlordEmail = landlordEmail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
