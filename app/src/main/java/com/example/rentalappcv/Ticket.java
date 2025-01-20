package com.example.rentalappcv;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a support or maintenance ticket associated with a property.
 * Includes details like creation time, urgency, and an events log.
 */

public class Ticket {

    private long createdAt = System.currentTimeMillis();
    private List<String> events;
    private int id;
    private String message;
    private int propertyId;
    private String status = "To-Do";
    private String type;
    private int urgency;

    /**
     * Constructs a new Ticket with the given details, automatically recording its creation time.
     *
     * @param type       the type of ticket (e.g., "Maintenance", "Complaint", etc.)
     * @param message    the main message or description
     * @param urgency    urgency level, represented by an integer
     * @param propertyId the ID of the property to which this ticket relates
     */

    public Ticket(String type, String message, int urgency, int propertyId) {
        this.type = type;
        this.message = message;
        this.urgency = urgency;
        this.propertyId = propertyId;
        this.events = new ArrayList<>();
        addEvent("Created at " + this.createdAt);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    /**
     * Adds an event (a string describing an action or update) to the ticket's event log.
     *
     * @param event the event description
     */

    public void addEvent(String event) {
        this.events.add(event);
    }

}
