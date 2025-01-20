package com.example.rentalappcv;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_NAME = "ReConnect.db";
    private static final int DATABASE_VERSION = 7;

    // Table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_PROPERTY = "property";
    private static final String TABLE_TICKET = "ticket";
    private static final String TABLE_BOOKED_PROPERTIES = "booked_properties";
    private static final String TABLE_REQUESTS = "requests";
    private static final String TABLE_PROPERTY_MANAGER_REQUESTS = "property_manager_requests";

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        this.database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // --------------------------------------------------
    // User Operations
    // --------------------------------------------------

    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("email_address", user.getEmailAddress());
        values.put("account_password", user.getAccountPassword());
        values.put("role", user.getRole());

        // Handle specific user types
        if (user instanceof Manager) {
            Manager manager = (Manager) user;
            values.put("image_url", manager.getProfilePicture());
            values.put("rating", manager.getMedianRating());
        } else if (user instanceof Landlord) {
            Landlord landlord = (Landlord) user;
            values.put("image_url", landlord.getProfilePicture());
        } else if (user instanceof Client) {
            Client client = (Client) user;
            values.put("birth_year", client.getBirthYear());
            values.put("image_url", client.getProfilePicture());
        }

        database.insert(TABLE_USER, null, values);
    }

    /**
     * Retrieves a User object from the database based on their email.
     */
    public User getUser(String email) {
        if (email == null) {
            return null;
        }

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE email_address = ?",
                new String[]{ email }
        );

        if (cursor != null && cursor.moveToFirst()) {
            String role = getStringFromCursor(cursor, "role");
            String firstName = getStringFromCursor(cursor, "first_name");
            String lastName = getStringFromCursor(cursor, "last_name");
            String emailAddress = getStringFromCursor(cursor, "email_address");
            String password = getStringFromCursor(cursor, "account_password");
            String imageUrl = getStringFromCursor(cursor, "image_url");

            User user;
            switch (role) {
                case "Manager": {
                    Manager manager = new Manager(firstName, lastName, emailAddress, password, imageUrl);
                    double rating = getDoubleFromCursor(cursor, "rating");
                    if (rating != 0) {
                        manager.addRating(rating);
                    }
                    user = manager;
                    break;
                }
                case "Landlord": {
                    String address = getStringFromCursor(cursor, "address");
                    Landlord landlord = new Landlord(firstName, lastName, emailAddress, password, address, imageUrl);
                    user = landlord;
                    break;
                }
                case "Client": {
                    int birthYear = getIntFromCursor(cursor, "birth_year");
                    Client client = new Client(firstName, lastName, emailAddress, password, birthYear, imageUrl);
                    user = client;
                    break;
                }
                default: {
                    // Generic User object if role is something else
                    user = new User(firstName, lastName, emailAddress, password, role);
                    break;
                }
            }
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // --------------------------------------------------
    // Manager
    // --------------------------------------------------

    public List<Manager> getAllManagers() {
        List<Manager> managers = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE role = 'Manager'",
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String firstName = getStringFromCursor(cursor, "first_name");
                String lastName = getStringFromCursor(cursor, "last_name");
                String emailAddress = getStringFromCursor(cursor, "email_address");
                String password = getStringFromCursor(cursor, "account_password");
                String imageUrl = getStringFromCursor(cursor, "image_url");

                Manager manager = new Manager(firstName, lastName, emailAddress, password, imageUrl);
                double rating = getDoubleFromCursor(cursor, "rating");
                if (rating != 0.0) {
                    manager.addRating(rating);
                }
                managers.add(manager);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return managers;
    }

    public void addManager(Manager manager) {
        addUser(manager);
    }

    public void addManagerRating(String managerEmail, int rating) {
        Manager manager = (Manager) getUser(managerEmail);
        if (manager != null) {
            manager.addRating(rating);
            ContentValues values = new ContentValues();
            values.put("rating", manager.getMedianRating());
            database.update(TABLE_USER, values, "email_address = ?", new String[]{ managerEmail });
        }
    }

    /**
     * This method seems to add a rating to *all* managers found in the DB.
     * Possibly used for some global rating logic.
     */
    public void addManagerRating(int rating) {
        Cursor cursor = database.rawQuery("SELECT email_address, rating FROM " + TABLE_USER + " WHERE role = 'manager'", null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String managerEmail = cursor.getString(cursor.getColumnIndex("email_address"));
            @SuppressLint("Range") int currentRating = cursor.getInt(cursor.getColumnIndex("rating"));
            int newRating = (currentRating == 0) ? rating : (currentRating + rating) / 2;

            ContentValues values = new ContentValues();
            values.put("rating", newRating);

            database.update(TABLE_USER, values, "email_address = ?", new String[]{ managerEmail });
        }
        cursor.close();
    }

    // --------------------------------------------------
    // Landlord
    // --------------------------------------------------

    public void addLandlord(Landlord landlord) {
        addUser(landlord);
    }

    // --------------------------------------------------
    // Client
    // --------------------------------------------------

    public void addClient(Client client) {
        addUser(client);
    }

    @SuppressLint("Range")
    public String getClientProfileImage(String clientEmail) {
        if (clientEmail == null) {
            return null;
        }

        String imageUrl = null;
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(
                    "SELECT image_url FROM " + TABLE_USER + " WHERE email_address = ?",
                    new String[]{ clientEmail }
            );

            if (cursor != null && cursor.moveToFirst()) {
                imageUrl = cursor.getString(cursor.getColumnIndex("image_url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return imageUrl;
    }

    // --------------------------------------------------
    // Property (Add, Update, Retrieve)
    // --------------------------------------------------

    public void addProperty(Property property) throws Exception {
        double[] latLong = GeocodingUtils.getLatLongFromAddress(property.getAddress());
        property.setLatitude(latLong[0]);
        property.setLongitude(latLong[1]);

        ContentValues values = buildPropertyContentValues(property);
        database.insert(TABLE_PROPERTY, null, values);
    }

    public void updateProperty(Property property) throws Exception {
        double[] latLong = GeocodingUtils.getLatLongFromAddress(property.getAddress());
        property.setLatitude(latLong[0]);
        property.setLongitude(latLong[1]);

        ContentValues values = buildPropertyContentValues(property);
        database.update(TABLE_PROPERTY, values, "id = ?", new String[]{ String.valueOf(property.getId()) });
    }

    private ContentValues buildPropertyContentValues(Property property) {
        ContentValues values = new ContentValues();
        values.put("address", property.getAddress());
        values.put("type", property.getType());
        values.put("rooms", property.getRooms());
        values.put("bathrooms", property.getBathrooms());
        values.put("floors", property.getFloors());
        values.put("area", property.getArea());
        values.put("laundry_in_unit", property.isLaundryInUnit() ? 1 : 0);
        values.put("parking_spots", property.getParkingSpots());
        values.put("rent", property.getRent());
        values.put("hydro_included", property.isHydroIncluded() ? 1 : 0);
        values.put("heat_included", property.isHeatIncluded() ? 1 : 0);
        values.put("water_included", property.isWaterIncluded() ? 1 : 0);
        values.put("occupied", property.isOccupied() ? 1 : 0);
        values.put("manager_id", property.getManager() != null ? property.getManager().getEmailAddress() : null);
        values.put("commission", property.getCommission());
        values.put("landlord_email", property.getLandlordEmail());
        values.put("latitude", property.getLatitude());
        values.put("longitude", property.getLongitude());

        // Convert images list to a semicolon-delimited string
        if (property.getImages() == null || property.getImages().isEmpty()) {
            values.put("images", "");
        } else {
            StringBuilder imagesBuilder = new StringBuilder();
            for (String image : property.getImages()) {
                imagesBuilder.append(image).append(";");
            }
            values.put("images", imagesBuilder.toString());
        }
        return values;
    }

    public Property getPropertyById(int propertyId) {
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY + " WHERE id = ?",
                new String[]{ String.valueOf(propertyId) }
        );

        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        Property property = buildPropertyFromCursor(cursor);
        cursor.close();
        return property;
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_PROPERTY, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Property property = buildPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return properties;
    }

    public List<Property> getPropertiesByClient(String clientEmail) {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY + " WHERE client_id = ?",
                new String[]{ clientEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Property property = buildPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return properties;
    }

    public List<Property> getPropertiesByLandlord(String landlordEmail) {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY + " WHERE landlord_email = ?",
                new String[]{ landlordEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Property property = buildPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return properties;
    }

    public List<Property> getPropertiesByManager(String managerEmail) {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY + " WHERE manager_id = ?",
                new String[]{ managerEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Property property = buildPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return properties;
    }

    public List<Property> getPastPropertiesByManager(String managerEmail) {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY + " WHERE manager_id = ? AND occupied = 0",
                new String[]{ managerEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Property property = buildPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return properties;
    }

    public void assignManagerToProperty(int propertyId, String managerEmail) {
        ContentValues values = new ContentValues();
        values.put("manager_id", managerEmail);
        database.update(TABLE_PROPERTY, values, "id = ?", new String[]{ String.valueOf(propertyId) });
    }

    public void assignClientToProperty(int propertyId, String clientEmail) {
        ContentValues values = new ContentValues();
        values.put("client_id", clientEmail);
        database.update(TABLE_PROPERTY, values, "id = ?", new String[]{ String.valueOf(propertyId) });
    }

    public String getPropertyAddress(int propertyId) {
        Cursor cursor = database.rawQuery(
                "SELECT address FROM " + TABLE_PROPERTY + " WHERE id = ?",
                new String[]{ String.valueOf(propertyId) }
        );

        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
        cursor.close();
        return address;
    }

    // --------------------------------------------------
    // Property Search
    // --------------------------------------------------

    /**
     * Searches for properties based on various criteria.
     *
     * @param address  the address (or partial address) to search by, or null/empty to ignore.
     * @param bedrooms the exact number of bedrooms, or -1 to ignore.
     * @param bathrooms the exact number of bathrooms, or -1 to ignore.
     * @param floors the exact number of floors, or -1 to ignore.
     * @param rent the maximum rent, or -1 to ignore.
     * @param area the minimum area, or -1 to ignore.
     * @param hydro whether the property must include hydro
     * @param heat whether the property must include heat
     * @param water whether the property must include water
     * @param houses if true, search for type = 'House' unless anyProperty is true
     * @param condo if true, search for type = 'Condo' unless anyProperty is true
     * @param commercial if true, search for type = 'Commercial' unless anyProperty is true
     * @param anyProperty if true, ignore type filters
     *
     * @return a list of Property objects matching the criteria
     */
    public List<Property> searchProperties(String address,
                                           int bedrooms,
                                           int bathrooms,
                                           int floors,
                                           int rent,
                                           int area,
                                           boolean hydro,
                                           boolean heat,
                                           boolean water,
                                           boolean houses,
                                           boolean condo,
                                           boolean commercial,
                                           boolean anyProperty) {

        List<Property> propertyList = new ArrayList<>();

        // Start building the SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_PROPERTY + " WHERE 1=1");

        // If address is provided, do a partial match
        if (address != null && !address.trim().isEmpty()) {
            queryBuilder.append(" AND address LIKE '%").append(address).append("%'");
        }

        // Exact match for bedrooms, bathrooms, floors if not -1
        if (bedrooms > 0) {
            queryBuilder.append(" AND rooms = ").append(bedrooms);
        }
        if (bathrooms > 0) {
            queryBuilder.append(" AND bathrooms = ").append(bathrooms);
        }
        if (floors > 0) {
            queryBuilder.append(" AND floors = ").append(floors);
        }

        // Rent <= specified max rent if not -1
        if (rent > 0) {
            queryBuilder.append(" AND rent <= ").append(rent);
        }

        // Area >= specified minimum if not -1
        if (area > 0) {
            queryBuilder.append(" AND area >= ").append(area);
        }

        // Utility filters
        if (hydro) {
            queryBuilder.append(" AND hydro_included = 1");
        }
        if (heat) {
            queryBuilder.append(" AND heat_included = 1");
        }
        if (water) {
            queryBuilder.append(" AND water_included = 1");
        }

        // Property type filters:
        // Only apply if !anyProperty. If anyProperty == true, skip the type filter.
        if (!anyProperty) {
            // Collect all selected types
            List<String> types = new ArrayList<>();
            if (houses) {
                types.add("'House'");
            }
            if (condo) {
                types.add("'Condo'");
            }
            if (commercial) {
                types.add("'Commercial'");
            }
            // If the user specified at least one type
            if (!types.isEmpty()) {
                queryBuilder.append(" AND type IN (");
                for (int i = 0; i < types.size(); i++) {
                    queryBuilder.append(types.get(i));
                    if (i < types.size() - 1) {
                        queryBuilder.append(", ");
                    }
                }
                queryBuilder.append(")");
            }
        }

        // Convert StringBuilder to String
        String finalQuery = queryBuilder.toString();

        // Execute the query
        Cursor cursor = database.rawQuery(finalQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Use buildPropertyFromCursor to construct each Property object
                Property property = buildPropertyFromCursor(cursor);
                propertyList.add(property);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return propertyList;
    }

    // --------------------------------------------------
    // Requests and Tickets
    // --------------------------------------------------

    public void addRequest(PropertyRequest request) {
        ContentValues values = new ContentValues();
        values.put("property_id", request.getPropertyId());
        values.put("client_email", request.getClientEmail());
        values.put(NotificationCompat.CATEGORY_STATUS, request.getStatus());
        values.put("request_type", request.getRequestType());
        values.put("property_address", request.getPropertyAddress());
        values.put("client_profile_image", request.getClientProfileImage());
        database.insert(TABLE_REQUESTS, null, values);
    }

    public void updateRequest(PropertyRequest request) {
        ContentValues values = new ContentValues();
        values.put("property_id", request.getPropertyId());
        values.put("client_email", request.getClientEmail());
        values.put(NotificationCompat.CATEGORY_STATUS, request.getStatus());
        values.put("request_type", request.getRequestType());
        values.put("property_address", request.getPropertyAddress());
        values.put("client_profile_image", request.getClientProfileImage());

        database.update(TABLE_REQUESTS, values, "id = ?", new String[]{ String.valueOf(request.getId()) });
    }

    public void updateRequestStatus(int requestId, String status) {
        ContentValues values = new ContentValues();
        values.put(NotificationCompat.CATEGORY_STATUS, status);
        database.update(TABLE_PROPERTY_MANAGER_REQUESTS, values, "id = ?", new String[]{ String.valueOf(requestId) });
    }

    public void deleteRequest(int requestId) {
        database.delete(TABLE_PROPERTY_MANAGER_REQUESTS, "id = ?", new String[]{ String.valueOf(requestId) });
    }

    public void deleteClientRequest(int requestId) {
        database.delete(TABLE_REQUESTS, "id = ?", new String[]{ String.valueOf(requestId) });
    }

    @SuppressLint("Range")
    public List<PropertyRequest> getRequestsByPropertyId(int propertyId) {
        List<PropertyRequest> requests = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_REQUESTS + " WHERE property_id = ?",
                new String[]{ String.valueOf(propertyId) }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PropertyRequest request = new PropertyRequest();
                request.setId(cursor.getInt(cursor.getColumnIndex("id")));
                request.setPropertyId(cursor.getInt(cursor.getColumnIndex("property_id")));
                request.setClientEmail(cursor.getString(cursor.getColumnIndex("client_email")));
                request.setStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                // Additional fields if needed
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    @SuppressLint("Range")
    public List<PropertyRequest> getRequestsByClientEmail(String clientEmail) {
        List<PropertyRequest> requests = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_REQUESTS + " WHERE client_email = ?",
                new String[]{ clientEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PropertyRequest request = new PropertyRequest();
                request.setId(cursor.getInt(cursor.getColumnIndex("id")));
                request.setPropertyId(cursor.getInt(cursor.getColumnIndex("property_id")));
                request.setClientEmail(cursor.getString(cursor.getColumnIndex("client_email")));
                request.setStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                // Additional fields if needed
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    @SuppressLint("Range")
    public List<PropertyRequest> getRequestsByLandlord(String landlordEmail) {
        List<PropertyRequest> requests = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT r.* FROM " + TABLE_REQUESTS + " r JOIN " + TABLE_PROPERTY + " p ON r.property_id = p.id " +
                        "WHERE p.landlord_email = ?",
                new String[]{ landlordEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PropertyRequest request = new PropertyRequest();
                request.setId(cursor.getInt(cursor.getColumnIndex("id")));
                request.setPropertyId(cursor.getInt(cursor.getColumnIndex("property_id")));
                request.setClientEmail(cursor.getString(cursor.getColumnIndex("client_email")));
                request.setStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                request.setRequestType(cursor.getString(cursor.getColumnIndex("request_type")));
                request.setPropertyAddress(cursor.getString(cursor.getColumnIndex("property_address")));
                request.setClientProfileImage(cursor.getString(cursor.getColumnIndex("client_profile_image")));
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    // Manager Requests
    public void addManagerRequest(int propertyId, String managerEmail, String propertyAddress) {
        ContentValues values = new ContentValues();
        values.put("property_id", propertyId);
        values.put("property_address", propertyAddress);
        values.put("manager_email", managerEmail);
        values.put(NotificationCompat.CATEGORY_STATUS, "pending");
        database.insert(TABLE_PROPERTY_MANAGER_REQUESTS, null, values);
    }

    @SuppressLint("Range")
    public List<LandlordManagerRequest> getRequestsByManager(String managerEmail) {
        List<LandlordManagerRequest> requests = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_PROPERTY_MANAGER_REQUESTS + " WHERE manager_email = ?",
                new String[]{ managerEmail }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LandlordManagerRequest request = new LandlordManagerRequest();
                request.setId(cursor.getInt(cursor.getColumnIndex("id")));
                request.setPropertyId(cursor.getInt(cursor.getColumnIndex("property_id")));
                request.setAddress(cursor.getString(cursor.getColumnIndex("property_address")));
                request.setManagerEmail(cursor.getString(cursor.getColumnIndex("manager_email")));
                request.setStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    @SuppressLint("Range")
    public List<LandlordManagerRequest> getRequestsByStatusAndLandlord(String landlordEmail, String status) {
        List<LandlordManagerRequest> requests = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT r.*, p.address, u.image_url " +
                        "FROM property_manager_requests r " +
                        "JOIN property p ON r.property_id = p.id " +
                        "JOIN user u ON r.manager_email = u.email_address " +
                        "WHERE p.landlord_email = ? AND r.status = ?",
                new String[]{ landlordEmail, status }
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LandlordManagerRequest request = new LandlordManagerRequest();
                request.setId(cursor.getInt(cursor.getColumnIndex("id")));
                request.setPropertyId(cursor.getInt(cursor.getColumnIndex("property_id")));
                request.setManagerEmail(cursor.getString(cursor.getColumnIndex("manager_email")));
                request.setStatus(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS)));
                // Additional fields if needed
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    // --------------------------------------------------
    // Helper Methods
    // --------------------------------------------------

    @SuppressLint("Range")
    private Property buildPropertyFromCursor(Cursor cursor) {
        Property property = new Property();
        property.setId(cursor.getInt(cursor.getColumnIndex("id")));
        property.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        property.setType(cursor.getString(cursor.getColumnIndex("type")));
        property.setRooms(cursor.getInt(cursor.getColumnIndex("rooms")));
        property.setBathrooms(cursor.getInt(cursor.getColumnIndex("bathrooms")));
        property.setFloors(cursor.getInt(cursor.getColumnIndex("floors")));
        property.setArea(cursor.getInt(cursor.getColumnIndex("area")));
        property.setLaundryInUnit(cursor.getInt(cursor.getColumnIndex("laundry_in_unit")) == 1);
        property.setParkingSpots(cursor.getInt(cursor.getColumnIndex("parking_spots")));
        property.setRent(cursor.getInt(cursor.getColumnIndex("rent")));
        property.setHydroIncluded(cursor.getInt(cursor.getColumnIndex("hydro_included")) == 1);
        property.setHeatIncluded(cursor.getInt(cursor.getColumnIndex("heat_included")) == 1);
        property.setWaterIncluded(cursor.getInt(cursor.getColumnIndex("water_included")) == 1);
        property.setOccupied(cursor.getInt(cursor.getColumnIndex("occupied")) == 1);
        property.setCommission(cursor.getInt(cursor.getColumnIndex("commission")));
        property.setLandlordEmail(cursor.getString(cursor.getColumnIndex("landlord_email")));
        property.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
        property.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));

        // Set manager if present
        String managerEmail = cursor.getString(cursor.getColumnIndex("manager_id"));
        if (managerEmail != null) {
            property.setManager(getManager(managerEmail));
        }

        // Set images if present
        String imagesString = cursor.getString(cursor.getColumnIndex("images"));
        if (imagesString != null && !imagesString.isEmpty()) {
            property.setImages(new ArrayList<>(Arrays.asList(imagesString.split(";"))));
        }
        return property;
    }

    private Manager getManager(String email) {
        return (Manager) getUser(email);
    }

    private String getStringFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getString(columnIndex);
        }
        return null;
    }

    private int getIntFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getInt(columnIndex);
        }
        return 0;
    }

    private double getDoubleFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getDouble(columnIndex);
        }
        return 0.0d;
    }

    // --------------------------------------------------
    // DatabaseHelper Inner Class
    // --------------------------------------------------

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE user(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "first_name TEXT," +
                            "last_name TEXT," +
                            "email_address TEXT," +
                            "account_password TEXT," +
                            "role TEXT," +
                            "image_url TEXT," +
                            "birth_year INTEGER," +
                            "rating REAL)"
            );

            db.execSQL(
                    "CREATE TABLE property(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "address TEXT," +
                            "type TEXT," +
                            "rooms INTEGER," +
                            "bathrooms INTEGER," +
                            "floors INTEGER," +
                            "area INTEGER," +
                            "laundry_in_unit INTEGER," +
                            "parking_spots INTEGER," +
                            "rent INTEGER," +
                            "hydro_included INTEGER," +
                            "heat_included INTEGER," +
                            "water_included INTEGER," +
                            "occupied INTEGER," +
                            "manager_id TEXT," +
                            "client_id TEXT," +
                            "commission INTEGER," +
                            "images TEXT," +
                            "landlord_email TEXT," +
                            "latitude REAL," +
                            "longitude REAL," +
                            "FOREIGN KEY(manager_id) REFERENCES user(email_address))"
            );

            db.execSQL(
                    "CREATE TABLE ticket(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "type TEXT," +
                            "message TEXT," +
                            "urgency INTEGER," +
                            "status TEXT," +
                            "created_at DATETIME," +
                            "property_id INTEGER," +
                            "events TEXT," +
                            "FOREIGN KEY(property_id) REFERENCES property(id))"
            );

            db.execSQL(
                    "CREATE TABLE booked_properties(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "client_id TEXT," +
                            "property_id INTEGER," +
                            "FOREIGN KEY(client_id) REFERENCES user(email_address)," +
                            "FOREIGN KEY(property_id) REFERENCES property(id))"
            );

            db.execSQL(
                    "CREATE TABLE requests(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "property_id INTEGER," +
                            "client_email TEXT," +
                            "status TEXT," +
                            "request_type TEXT," +
                            "property_address TEXT," +
                            "client_profile_image TEXT," +
                            "FOREIGN KEY(property_id) REFERENCES property(id)," +
                            "FOREIGN KEY(client_email) REFERENCES user(email_address))"
            );

            db.execSQL(
                    "CREATE TABLE property_manager_requests(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "property_id INTEGER," +
                            "property_address TEXT," +
                            "manager_email TEXT," +
                            "status TEXT," +
                            "FOREIGN KEY(property_id) REFERENCES property(id)," +
                            "FOREIGN KEY(manager_email) REFERENCES user(email_address))"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKED_PROPERTIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY_MANAGER_REQUESTS);
            onCreate(db);
        }
    }
}
