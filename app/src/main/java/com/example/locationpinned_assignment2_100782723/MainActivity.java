package com.example.locationpinned_assignment2_100782723;

import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText addressEditText;
    private EditText locationEditText;
    private EditText queryEditText;
    private TextView locationInfoTextView;
    private LocationDatabaseHelper dbHelper; // Database helper class
    private Map<String, String> longitudeLatitudeMap; // Map for coordinates and associated location names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize longitudeLatitudeMap here
        longitudeLatitudeMap = new HashMap<>();

        addressEditText = findViewById(R.id.addressEditText);
        locationEditText = findViewById(R.id.locationNameEditText);
        Button addLocationButton = findViewById(R.id.addLocationButton);
        queryEditText = findViewById(R.id.queryEditText);
        Button queryLocationButton = findViewById(R.id.queryLocationButton);
        locationInfoTextView = findViewById(R.id.locationInfoTextView);
        Button deleteLocationButton = findViewById(R.id.deleteLocationButton);
        Button updateLocationButton = findViewById(R.id.updateLocationButton);

        dbHelper = new LocationDatabaseHelper(this);

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String address = addressEditText.getText().toString();
                String location = locationEditText.getText().toString();

                // Perform geocoding to get the latitude and longitude
                String[] coordinates = getCoordinatesByAddress(address);

                if (coordinates != null) {
                    String latitude = coordinates[0];
                    String longitude = coordinates[1];

                    // Add the location to the database
                    long newRowId = dbHelper.addLocation(location, address, Double.parseDouble(latitude), Double.parseDouble(longitude));

                    if (newRowId != -1) {
                        locationInfoTextView.setText("Location added with ID: " + newRowId);
                    } else {
                        locationInfoTextView.setText("Failed to add location");
                    }
                } else {
                    locationInfoTextView.setText("Location not found or invalid address.");
                }
            }
        });
        queryLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input (location name)
                String locationName = queryEditText.getText().toString();

                // Find coordinates from the text file or database
                String[] coordinates = findCoordinatesByLocationName(locationName);

                if (coordinates != null && coordinates.length >= 2) {
                    locationInfoTextView.setText("Latitude: " + coordinates[0] + "\nLongitude: " + coordinates[1]);
                } else {
                    locationInfoTextView.setText("Location not found");
                }
            }
        });

        // Read input file and populate longitudeLatitudeMap
        loadLongitudeLatitudePairsFromInputFile("location_files.txt");
    }

    private void loadLongitudeLatitudePairsFromInputFile(String fileName) {
        longitudeLatitudeMap = new HashMap<>();
        AssetManager assetManager = getAssets();

        try {
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // Change this part of loadLongitudeLatitudePairsFromInputFile
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String locationName = parts[0].trim();
                    String latitude = parts[1].trim();
                    String longitude = parts[2].trim();

                    // Use the location name as the key in the map
                    longitudeLatitudeMap.put(locationName, latitude + "," + longitude);
                }
            }


            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getCoordinatesByAddress(String address) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(address, 1);

            if (!addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                return new String[]{String.valueOf(latitude), String.valueOf(longitude)};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Location not found
    }

    private String[] findCoordinatesByLocationName(String locationName) {
        for (Map.Entry<String, String> entry : longitudeLatitudeMap.entrySet()) {
            // Check if the locationName matches the key (location name)
            if (entry.getKey().equals(locationName)) {
                String[] parts = entry.getValue().split(",");
                if (parts.length == 2) {
                    String latitude = parts[0].trim();
                    String longitude = parts[1].trim();
                    return new String[]{latitude, longitude};
                }
            }
        }

        // If not found in the map, check the database
        return dbHelper.getLocationCoordinatesByName(locationName);
    }

}



