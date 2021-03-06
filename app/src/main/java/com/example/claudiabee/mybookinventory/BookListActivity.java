package com.example.claudiabee.mybookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays a list of books stored in a database
 */
public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** This String constant is used for logging */
    public final static String LOG_TAG = BookListActivity.class.getSimpleName();

    /**  This constant is the loader ID, it identifies the loader */
    private static final int BOOK_URI_LOADER = 0;
    /**
     * This is the current uri
     */
    private Uri currentUri;

    // This is the projection, the rows of the books table that will be retrieved
    String[] LOADER_PROJECTION = {
            BookEntry._ID,
            BookEntry.COLUMN_BOOK_TITLE,
            BookEntry.COLUMN_BOOK_PRICE,
            BookEntry.COLUMN_BOOK_QUANTITY
    };


    /** This is the ListView that displays the list of books */
    @BindView(R.id.book_listview) ListView mBookListView;

    /** This is the empty view */
    @BindView(R.id.empty_view) View emptyView;

    /** This is the Floating Action Button */
    @BindView(R.id.fab) FloatingActionButton fab;

    /**
     *  This is the Cursor Adapter used to inflate list item views to which bind data to be
     * displayed in the ListView
     */
    MyBookInventoryCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        // Set the empty view on the ListView, so that it only shows when the list has 0 items.
        mBookListView.setEmptyView(emptyView);

        // Setup a CursorAdapter to create list item view for each row of the books data
        // in the Cursor. Until the loader finishes there is no data so pass null for the Cursor
        mBookCursorAdapter = new MyBookInventoryCursorAdapter(this, null);

        // Set the CursorAdapter on the listView
        mBookListView.setAdapter(mBookCursorAdapter);

        // Set onClickItemListener on the ListView so that when a list item gets clicked
        // an intent is sent to open the BookDetailActivity
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "This list item view has been clicked");
                Intent intent = new Intent (BookListActivity.this, BookDetailActivity.class);
                // Find the URI the selected list item
                currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                // Pass the data of the selected book to the new activity
                intent.setData(currentUri);
                // Launch the {@link BookDetailActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        // Prepare the loader. Either reconnect with an existing one, or start a new one.
        getSupportLoaderManager().initLoader(BOOK_URI_LOADER, null, this);

        /**
         * When this Fab gets clicked an intent is sent to start the AddBookActivity
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Fab is clicked an intent is sent to open the AddBookActivity
                startActivity(new Intent(BookListActivity.this, AddBookActivity.class));
            }
        });
    }

    /**
     * This method, insert a hardcoded sample book into the database when clicking the
     * "Add sample data" in the overflow menu.
     */
    private void insertBook() {

        // Create a ContentValues object. It specifies what data we want to insert
        ContentValues values = new ContentValues();
        // Populate the instance of the ContentValues class with data about a book
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Il manuale del fitopreparatore");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 30.00);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 1);
        values.put(BookEntry.COLUMN_BOOK_PRODUCTION_INFO, BookEntry.IS_OUT_OF_PRINT);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "OldBooksSupplier");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, 2025550122);

        // Inserting data, insert a new row for the sample book into the provider
        // using the ContentResolver.
        // {@link CONTENT_URI} specifies where we want to insert the new data (the books table).
        // This method returns the a new content URI related to the book just saved
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        Log.v(LOG_TAG, "New row ID: " + newUri);
        if (newUri == null){
            Toast.makeText(this, R.string.insert_error_text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.successful_insert_text, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu option buttons to the app bar
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Insert sample data in database
        insertBook();
        return super.onOptionsItemSelected(item);
    }

    // Creates the CursorLoader and defines the data to query from the content provider
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define the projection for the query
        String[] projection = LOADER_PROJECTION;

        // Create and return a CursorLoader, it executes the content provider query method
        // on the background thread and return a Cursor for the given projection
        return new CursorLoader(
                this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Update {@link MyBookInventoryCursorAdapter} with the new Cursor containing book data
        mBookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. The data are no longer needed.
        // We need to make sure we are no longer using it.
        mBookCursorAdapter.swapCursor(null);
    }
}
