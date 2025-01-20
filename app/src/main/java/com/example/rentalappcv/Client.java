package com.example.rentalappcv;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private int birthYear;
    private List<Property> bookedProperties = new ArrayList();
    private String profilePicture;

    public Client(String firstName, String lastName, String emailAddress, String accountPassword, int birthYear, String profilePicture) {
        super(firstName, lastName, emailAddress, accountPassword, "Client");
        this.birthYear = birthYear;
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getBirthYear() {
        return this.birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public List<Property> getBookedProperties() {
        return this.bookedProperties;
    }

    public void setBookedProperties(List<Property> bookedProperties) {
        this.bookedProperties = bookedProperties;
    }

    public void register(DatabaseManager dbManager) {
        dbManager.addClient(this);
    }
}