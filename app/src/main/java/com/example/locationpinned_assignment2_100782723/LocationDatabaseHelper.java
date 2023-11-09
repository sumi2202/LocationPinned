package com.example.locationpinned_assignment2_100782723;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocationDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_REVERSE_GEOCODED_ADDRESS = "reverse_geocoded_address";  // New column
    private String COLUMN_NAME;

    // Constructor
    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_LOCATIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_REVERSE_GEOCODED_ADDRESS + " TEXT);";  // Include the new column
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public long addLocation(String locationName, String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_REVERSE_GEOCODED_ADDRESS, address);  // Include the new column
        long newRowId = db.insert(TABLE_LOCATIONS, null, values);
        db.close();
        return newRowId;
    }


    public boolean deleteLocationByAddress(String queryAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(
                TABLE_LOCATIONS,
                COLUMN_ADDRESS + " = ?",
                new String[]{queryAddress}
        );
        db.close();
        return deletedRows > 0;
    }

    public boolean updateLocationByAddress(String queryAddress, String newLatitude, String newLongitude, String newAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, newLatitude);
        values.put(COLUMN_LONGITUDE, newLongitude);
        values.put(COLUMN_ADDRESS, newAddress);

        int updatedRows = db.update(
                TABLE_LOCATIONS,
                values,
                COLUMN_ADDRESS + " = ?",
                new String[]{queryAddress}
        );
        db.close();
        return updatedRows > 0;
    }

    public String[] getLocationCoordinatesByName(String locationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_LATITUDE, COLUMN_LONGITUDE};
        String[] selectionArgs = {locationName};

        Cursor cursor = db.query(TABLE_LOCATIONS, columns, COLUMN_NAME + "=?", selectionArgs, null, null, null);

        String[] coordinates = null;
        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(0);  // Index 0 corresponds to COLUMN_LATITUDE
            double longitude = cursor.getDouble(1);  // Index 1 corresponds to COLUMN_LONGITUDE
            coordinates = new String[]{String.valueOf(latitude), String.valueOf(longitude)};
        }

        cursor.close();
        db.close();

        return coordinates;
    }



}




