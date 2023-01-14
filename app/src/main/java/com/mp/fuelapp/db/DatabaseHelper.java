package com.mp.fuelapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "FuelApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "refuelings";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FUEL_AMOUNT = "fuel_amount";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_PRICE_PER_LITER = "price_per_liter";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_IMAGE = "image";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FUEL_AMOUNT + " DOUBLE, " +
                COLUMN_TOTAL_PRICE + " DOUBLE, " +
                COLUMN_PRICE_PER_LITER + " DOUBLE, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_IMAGE + " BLOB);";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addRefueling(double fuelAmount, double totalPrice, double pricePerLiter, String date, byte[] image) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_FUEL_AMOUNT, fuelAmount);
        contentValues.put(COLUMN_TOTAL_PRICE, totalPrice);
        contentValues.put(COLUMN_PRICE_PER_LITER, pricePerLiter);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_IMAGE, image);

        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            Toast.makeText(context, "Failed to add refueling.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Refueling added successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteRefueling(String row_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        long result = sqLiteDatabase.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to delete refueling.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllRefuelings() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM  " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC;";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
        }

        return cursor;
    }
}
