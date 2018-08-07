package com.example.claudiabee.mybookinventory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * {@link ContentProvider} for MyBookInventory app.
 */
public class MyBookInventoryProvider extends ContentProvider {

    /** Tag for the log message */
    public static final String LOG_TAG = MyBookInventoryProvider.class.getSimpleName();

    /** Database helper object */
    MyBookInventoryDbHelper myBookInventoryDbHelper;

    /**
     * Initialize the provider and  the database object
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
     * @param projection    is an array which specifies the column/s of the database we want back in the Cursor
     * @param selection     is a selection of the contraints which help to narrow the results of the query
     * @param selectionArgs are the values associated with the selection, inserted here for safety.
     * @param sortOrder     is the order in which the result of the query is presented
     * @return a Cursor object
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    /**
     * @param uri uri is the given URI which specify the data we want to interact with
     * @return the MIME type of data for the content URI
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     *
     * @param uri    is what specifies the resources we want to interact with.
     * @param values are the values to insert into the database
     * @return uri is the location of the resource inserted
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
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
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     *
     * @param uri           is what specifies the resources we want to interact with.
     * @param values        are the values which update the database
     * @param selection     selection is a selection of the constraints which help to define the set of data
     *                      to update
     * @param selectionArgs are the values associated with the given selection
     * @return the number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
