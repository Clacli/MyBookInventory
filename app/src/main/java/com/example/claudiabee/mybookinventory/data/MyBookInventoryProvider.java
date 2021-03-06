package com.example.claudiabee.mybookinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * {@link ContentProvider} for MyBookInventory app.
 */
public class MyBookInventoryProvider extends ContentProvider {

    /**
     * Tag for the log message
     */
    public static final String LOG_TAG = MyBookInventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code of the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object matches a content URI to a corresponding code.
     * The input passed into the constructor represent the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(MyBookInventoryContract.CONTENT_AUTHORITY, MyBookInventoryContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(MyBookInventoryContract.CONTENT_AUTHORITY, MyBookInventoryContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Database helper object
     */
    private MyBookInventoryDbHelper myBookInventoryDbHelper;

    /**
     * Initialize the provider and the database helper to gain access to the database.
     * It is called on the main thread.
     */
    @Override
    public boolean onCreate() {
        myBookInventoryDbHelper = new MyBookInventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.
     *
     * @param uri           is the given URI which specifies the resource we are interested in
     * @param projection    is an array which specifies the column/s of the database we want back
     *                      in the Cursor
     * @param selection     is a selection of the constraints which help to narrow the results of
     *                      the query
     * @param selectionArgs are the values associated with the selection, inserted here for safety.
     * @param sortOrder     is the order in which the result of the query is presented
     * @return a Cursor object
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Access the database in read mode
        SQLiteDatabase bookshopDb = myBookInventoryDbHelper.getReadableDatabase();

        // The cursor holding the result of the query
        Cursor cursor;

        // This variable stores an integer value to be used in the switch statement below
        int match = sUriMatcher.match(uri);

        // Check whether to query the table or a single row
        switch (match) {
            case BOOKS:
                // Perform a query on the BOOKS table
                cursor = bookshopDb.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // Selection and selectionArgs point to a specific row
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Perform a query on the books table for a specific row.
                cursor = bookshopDb.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        // Set the notification URI on the cursor. When the data at the given URI changes
        // the Cursor must be updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * @param uri uri is the given URI which specify the data we want to interact with
     * @return the MIME type of data for the content URI which should start with
     * vnd.android.cursor.item for a single record, or
     * vnd.android.cursor.dir/ for multiple items or
     * null if there is no type..
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     *
     * @param uri    specifies where are the resources we want to interact with.
     * @param values are the values to insert into the database
     * @return uri is the uri which specifies the location of the inserted resource.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Check if there is a match
        final int match = sUriMatcher.match(uri);
        // Only the BOOKS case is supported for insertion,
        // any other case falls in the default case throwing an exception..
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given values. Return the new content URI
     * for the the book that was just inserted into the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the product name , i.e. the book title, is not null
        String bookTitle = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        if (TextUtils.isEmpty(bookTitle)) {
            throw new IllegalArgumentException("The title of the book is a required field.");
        }

        // Check that the price of the book is not null and its value is equal to 0 or a
        // positive number.
        Double bookPrice = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
        if ((bookPrice == null) || (bookPrice < 0)) {
            throw new IllegalArgumentException("Valid price required");
        }

        // Check that the quantity is not null and its value is equal to 0 or a positive number
        Integer bookQuantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if ((bookQuantity == null) || (bookQuantity < 0)) {
            throw new IllegalArgumentException("Valid quantity required");
        }


        // Check that the information on whether the book is out of print or not is equal to
        // {@link #CHECK_IF_OUT_OF_PRINT}, {@link #NOT_OUT_OF_PRINT} or {@link #IS_OUT_OF_PRINT}.
        Integer production_info = values.getAsInteger(BookEntry.COLUMN_BOOK_PRODUCTION_INFO);
        if (production_info == null || !BookEntry.isValidInfo(production_info)) {
            throw new IllegalArgumentException(
                    "A valid information on whether the book is out of print or not is required");
        }

        // Check that the name of the supplier of books is not null
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("The supplier\'s name is required");
        }

        // Check that the phone number is not null and its value is equal to 0 or a positive number
        Long supplierPhoneNumber = values.getAsLong(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);
        if ((supplierPhoneNumber == null) || (supplierPhoneNumber < 0)) {
            throw new IllegalArgumentException("A valid supplier's phone number is required");
        }

        // Access the database in write mode.
        SQLiteDatabase bookshopDb = myBookInventoryDbHelper.getWritableDatabase();

        // Insert the new book record with the given values
        long newRowId = bookshopDb.insert(BookEntry.TABLE_NAME, null, values);

        // If the ID is -1, the insertion failed.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all the listener that the data has changed for the book URI,
        // the methods takes as argument the URI and  passing null to the second
        // argument the adapter object gets notified.
        getContext().getContentResolver().notifyChange(uri, null);

        // Append new row ID to CONTENT_URI and return the new URI of the last inserted record
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     *
     * @param uri           is what specifies the resources we want to interact with.
     * @param selection     is a selection of the constraints which help to define the set of data to delete
     * @param selectionArgs are the values associated with the selection
     * @return the number of the rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Number of rows deleted
        int numberOfRowsDeleted;

        // Get an instance of the SQLite database in write mode
        SQLiteDatabase bookshopDb = myBookInventoryDbHelper.getWritableDatabase();

        // Check if there is m match with the given uri
        final int match = sUriMatcher.match(uri);

        // Both the BOOKS case and the BOOK_ID are supported for deletion
        switch (match) {
            case BOOKS:
                // Delete multiple rows according to the selection and selectionArgs
                numberOfRowsDeleted = bookshopDb.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numberOfRowsDeleted = bookshopDb.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // Check if one or more rows were deleted
        if (numberOfRowsDeleted != 0) {
            // Notify all listener that one or more rows were deleted for the given uri
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return numberOfRowsDeleted;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     *
     * @param uri           is what specifies where are the resources we want to interact with.
     * @param values        are the values which update the database
     * @param selection     selection is a selection of the constraints which help to define the
     *                      set of data to update
     * @param selectionArgs are the values associated with the given selection
     * @return the number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        // Check if there is a match with the given uri
        final int match = sUriMatcher.match(uri);
        // Both the BOOKS case and the BOOK_ID are supported for insertion
        switch (match) {
            case BOOKS:
                // Update multiple rows according to the selection and selectionArgs
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                // Update just one row the ID of which is contained in selectionArgs.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update book records in the database with the given content values.
     * Apply the changes to the rows specified in the selection and selection arguments.
     * Return the number of rows successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link BookEntry#COLUMN_BOOK_TITLE} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String bookTitle = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (bookTitle == null) {
                throw new IllegalArgumentException("The title of the book is a required field.");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_PRICE} key is present,
        // check that the price of the book is not null and its value is equal to 0 or a
        // positive number.
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Double bookPrice = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
            if ((bookPrice == null) || (bookPrice < 0)) {
                throw new IllegalArgumentException("Valid price required");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_QUANTITY} key is present,
        // check that the quantity is not null and its value is equal to 0 or a positive number
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer bookQuantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if ((bookQuantity == null) || (bookQuantity < 0)) {
                throw new IllegalArgumentException("Valid price required");
            }
        }


        // If the {@link BookEntry#COLUMN_BOOK_PRODUCTION_INFO} key is present,
        // check that the information on whether the book is out of print or not is equal to
        // {@link #CHECK_IF_OUT_OF_PRINT}, {@link #NOT_OUT_OF_PRINT} or {@link #IS_OUT_OF_PRINT}.
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRODUCTION_INFO)) {
            Integer production_info = values.getAsInteger(BookEntry.COLUMN_BOOK_PRODUCTION_INFO);
            if (production_info == null || !BookEntry.isValidInfo(production_info)) {
                throw new IllegalArgumentException(
                        "A valid information on whether the book is out of print or not is required");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_SUPPLIER_NAME} key is present,
        // check that the name of the supplier of books is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("The supplier\'s name is required");
            }
        }

        // If the {@link BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the phone number is not null and its value is equal to 0 or a positive number
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER)) {
            Long supplierPhoneNumber = values.getAsLong(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);
            if ((supplierPhoneNumber == null) || (supplierPhoneNumber < 0)) {
                throw new IllegalArgumentException("A valid supplier\'s phone number is required");
            }
        }

        // If there are no values to update don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Access the database in write mode.
        SQLiteDatabase bookshopDb = myBookInventoryDbHelper.getWritableDatabase();

        // Update the new book record with the given values
        int numberOfRowsUpdated = bookshopDb.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // Check if there are one or more rows updated and in this case notify all listener
        // that data at the given URI were changed
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }
}
