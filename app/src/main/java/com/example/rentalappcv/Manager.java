package com.example.rentalappcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager class extending User, with a list of ratings,
 * a median rating calculation, and associated properties and tickets.
 */

public class Manager extends User {

    private List<Ticket> currentRequests = new ArrayList<>();
    private String profilePicture;
    private List<Property> propertiesManaged = new ArrayList<>();
    private List<Double> ratings;

    public Manager(String firstName, String lastName, String emailAddress, String accountPassword, String profilePicture) {
        super(firstName, lastName, emailAddress, accountPassword, "Manager");
        this.profilePicture = profilePicture;
        this.ratings = new ArrayList<>();
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Sets the manager's profile picture URL (Base64-encoded image).
     *
     * @param imageUrl Base64-encoded profile picture string
     */

    public void setImageUrl(String imageUrl) {
        this.profilePicture = imageUrl;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    /**
     * Adds a new rating for the manager and keeps the ratings list sorted.
     *
     * @param rating a double representing the new rating
     */

    public void addRating(double rating) {
        ratings.add(rating);
        Collections.sort(ratings);
    }

    /**
     * Calculates the median rating from the sorted list of ratings.
     * If no ratings exist, returns 0.0.
     *
     * @return the median rating
     */

    public double getMedianRating() {
        int size = ratings.size();
        if (size == 0) {
            return 0.0;
        }
        if (size % 2 == 0) {
            double left = ratings.get((size / 2) - 1);
            double right = ratings.get(size / 2);
            return (left + right) / 2.0;
        } else {
            return ratings.get(size / 2);
        }
    }

    public List<Property> getPropertiesManaged() {
        return propertiesManaged;
    }

    public void setPropertiesManaged(List<Property> propertiesManaged) {
        this.propertiesManaged = propertiesManaged;
    }

    public List<Ticket> getCurrentRequests() {
        return currentRequests;
    }

    public void setCurrentRequests(List<Ticket> currentRequests) {
        this.currentRequests = currentRequests;
    }

    /**
     * Registers this Manager in the database via the provided DatabaseManager.
     */

    public void register(DatabaseManager dbManager) {
        dbManager.addManager(this);
    }
}
