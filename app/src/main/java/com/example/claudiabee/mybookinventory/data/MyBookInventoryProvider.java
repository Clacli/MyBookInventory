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

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * {@link ContentProvider} for MyBookInventory app.
 */
public class MyBookInventoryProvider extends ContentProvider {

    /** Tag for the log message */
    public static final String LOG_TAG = MyBookInventoryProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 100;

    /** URI matcher code of the content URI for a single book in the books table */
    private static final int BOOK_ID = 101;

    /** UriMatcher object matches a content URI to a corresponding code.
     *  The input passed into the constructor represent the code to return for the root URI.
     *  It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(MyBookInventoryContract.CONTENT_AUTHORITY, MyBookInventoryContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(MyBookInventoryContract.CONTENT_AUTHORITY, MyBookInventoryContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /** Database helper object */
    private MyBookInventoryDbHelper myBookInventoryDbHelper;

    /**
     * Initialize the provider and  the database helper to gain access to the database.
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
     * @param projection    is an array which specifies the column/s of the database we want back in the Cursor
     * @param selection     is a selection of the constraints which help to narrow the results of the query
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

        // decide whether to query the table or a single row
        switch (match) {
            case BOOKS:
                // Perform a query on the PETS table
                cursor = bookshopDb.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // Selection and selectionArgs point to a specific row
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Perform a query on the books table for a specific row.
                cursor = bookshopDb.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);

        }
        return cursor;
    }

    /**
     * @param    uri uri is the given URI which specify the data we want to interact with
     * @return   the MIME type of data for the content URI which should start with
     *           vnd.android.cursor.item for a single record, or
     *           vnd.android.cursor.dir/ for multiple items or
     *           null if there is no type..
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     *
     * @param   uri    is what specifies the resources we want to interact with.
     * @param   values are the values to insert into the database
     * @return  uri is the uri which specifies the location of the inserted resource.
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
