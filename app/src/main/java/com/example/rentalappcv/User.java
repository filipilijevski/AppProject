package com.example.rentalappcv;

/**
 * A base User class representing common user fields and behaviors.
 * Other classes like Client, Landlord, and Manager extend this class.
 */

public class User {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String accountPassword;
    private String role;

    /**
     * Constructs a User with the given information.
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param emailAddress the user's email
     * @param accountPassword the user's password
     * @param role the user's role (e.g., "Client", "Landlord", "Manager")
     */

    public User(String firstName, String lastName, String emailAddress, String accountPassword, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.accountPassword = accountPassword;
        this.role = role;
    }

    // ---------------------------
    // Getters
    // ---------------------------
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public String getRole() {
        return role;
    }

    // ---------------------------
    // Setters
    // ---------------------------
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
