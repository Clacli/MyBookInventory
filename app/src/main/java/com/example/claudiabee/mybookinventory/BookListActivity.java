package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;
import com.example.claudiabee.mybookinventory.data.MyBookInventoryDbHelper;

/**
 * Displays a list of books stored in a database
 */
public class BookListActivity extends AppCompatActivity {

    /** This String constant is used for logging */
    public final static String LOG_TAG = BookListActivity.class.getSimpleName();

    /** This database helper class helps crating, opening, modifying the existing databases. */
    private MyBookInventoryDbHelper mMyBookInventoryDbHelper;

    /** This is the ListView that displays the list of books */
    ListView mBookListView;

    /**
     *  This is the Cursor Adapter used to inflate list item views to which bind data to be
     * displayed in the ListView
     */
    MyBookInventoryCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Create an instance of the ListView displaying the list of books of the inventory.
        mBookListView = (ListView) findViewById(R.id.book_listview);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        mBookListView.setEmptyView(emptyView);

        // To access our database, we create an instance of MyBookInventoryDbHelper,
        // subclass of SQLiteOpenHelper, and pass the context, which is the current activity,
        // as argument.
        mMyBookInventoryDbHelper = new MyBookInventoryDbHelper(this);

        /**
         * When this Fab gets clicked an intent is sent to start the AddBookActivity
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Fab is clicked an intent is sent to open the AddBookActivity
                startActivity(new Intent(BookListActivity.this, AddBookActivity.class));
            }
        });
    }

    // When the activity restarts the count of the rows updates.
    @Override
    protected void onStart() {
        super.onStart();
        queryBookData();
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

    /**
     * Query the database with to retrieve the product name (book title), the price,
     * and the quantity.
     */
    private void queryBookData(){

        // Define a projection that specifies the column from the database to use in the query
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
        };

        // Perform a query on the Content Provider through the Content Resolver to get a
        // Cursor object,that contains id, product name, price and quantity rows.
        Cursor cursor = getContentResolver().query(
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        // Setup a CursorAdapter to create list item view to which bind book data found at each row.
        mBookCursorAdapter = new MyBookInventoryCursorAdapter(this, cursor);

        // Set the cursor adapter on the Listview
        mBookListView.setAdapter(mBookCursorAdapter);
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
        queryBookData();
        return super.onOptionsItemSelected(item);
    }
}
