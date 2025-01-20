package com.example.rentalappcv;

import java.util.ArrayList;
import java.util.List;

/**
 * A Landlord is a specialized User with an address and a list of properties.
 * They can also have a profile picture.
 */

public class Landlord extends User {

    private String address;
    private String profilePicture;
    private List<Property> listedProperties = new ArrayList<>();

    /**
     * Constructs a Landlord object.
     *
     * @param firstName       the landlord's first name
     * @param lastName        the landlord's last name
     * @param emailAddress    the landlord's email address
     * @param accountPassword the landlord's account password
     * @param address         the landlord's address
     * @param profilePicture  the Base64-encoded profile image (optional)
     */

    public Landlord(String firstName,
                    String lastName,
                    String emailAddress,
                    String accountPassword,
                    String address,
                    String profilePicture) {
        super(firstName, lastName, emailAddress, accountPassword, "Landlord");
        this.address = address;
        this.profilePicture = profilePicture;
    }

    // ---------------------------
    // Getters and Setters
    // ---------------------------

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Property> getListedProperties() {
        return listedProperties;
    }

    public void setListedProperties(List<Property> listedProperties) {
        this.listedProperties = listedProperties;
    }

    // ---------------------------
    // Database Registration
    // ---------------------------

    /**
     * Registers this Landlord in the database using the provided DatabaseManager.
     *
     * @param dbManager the database manager to use for storing this Landlord
     */

    public void register(DatabaseManager dbManager) {
        dbManager.addLandlord(this);
    }
}
