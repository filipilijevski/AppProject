package com.example.rentalappcv;

import androidx.core.app.NotificationCompat;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for retrieving latitude and longitude information from an address using the Google Geocoding API.
 */

public class GeocodingUtils {

    // Replace with actual API key. Make sure to secure it properly once published.
    private static final String GEOCODING_API_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=000000000000000000000000000000000000000";

    /**
     * Retrieves the latitude and longitude for a given address using Google's Geocoding API.
     *
     * @param address A string representing the address to geocode.
     * @return A double array where index 0 is latitude and index 1 is longitude.
     * @throws Exception if there is an error in the request or parsing the response.
     */

    public static double[] getLatLongFromAddress(String address) throws Exception {
        // Prepare the request URL by replacing spaces with "+" for proper encoding
        String urlString = String.format(GEOCODING_API_URL, address.replace(" ", "+"));

        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");

        // Read the response from the API
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        String status = jsonResponse.getString(NotificationCompat.CATEGORY_STATUS);

        if ("OK".equals(status)) {
            JSONObject location = jsonResponse
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            return new double[]{lat, lng};
        } else {
            throw new Exception("Error fetching geocoding data: " + status);
        }
    }
}
