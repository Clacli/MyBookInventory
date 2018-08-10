package com.example.claudiabee.mybookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * Database helper for the BookInventory app.
 * This helper class manages the database creation and its version management.
 */
public class MyBookInventoryDbHelper extends SQLiteOpenHelper {

    // This is used for logging
    public static final String LOG_TAG = MyBookInventoryDbHelper.class.getSimpleName();

    /**
     * The name of the database file.
     */
    private static final String DATABASE_NAME = "bookshop.db";

    /**
     * The bookshop database version.
     * If the database schema gets modified, the database version must be incremented.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * This is the constructor for a new instance of the {@link MyBookInventoryDbHelper}.
     *
     * @param context is the contextof the app
     */
    public MyBookInventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     * This is the first method to override.
     *
     * @param db is the database to create
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "(" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, " +
                BookEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL DEFAULT 0.00, " +
                BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                BookEntry.COLUMN_BOOK_PRODUCTION_INFO + " INTEGER NOT NULL,  " +
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
        // Log the SQL_CREATE_BOOKS_TABLE String
        Log.v(LOG_TAG, "SQL_CREATE_BOOKS_TABLE: " + SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     *
     * @param db is the bookshop database to update
     * @param oldVersion is the old version of the bookshop database
     * @param newVersion is the new version of the bookshop database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so do nothing for the moment.
    }
}
