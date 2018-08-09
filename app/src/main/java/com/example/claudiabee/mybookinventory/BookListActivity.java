package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

        // To access our database, we create an instance of MyBookInventoryDbHelper,
        // subclass of SQLiteOpenHelper, and pass the context, which is the current activity,
        // as argument.
        mMyBookInventoryDbHelper = new MyBookInventoryDbHelper(this);

        /**
         * When this Fab gets clicked an intent is sent to start the BookManagingActivity
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Fab is clicked an intent is sent to open the BookManagingActivity
                startActivity(new Intent(BookListActivity.this, BookManagingActivity.class));
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
     * "Add sample data" in the overflow menu
     */
    private void insertBook() {

        // Create and/or open a database in write mode
        SQLiteDatabase db = mMyBookInventoryDbHelper.getWritableDatabase();

        // Create an object containing key-values pair with data to insert into the books table of
        // the database.
        ContentValues values = new ContentValues();
        // Populate the instance of the ContentValues class with data about a book
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Il manuale del fitopreparatore");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 30.00);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 1);
        values.put(BookEntry.COLUMN_BOOK_OUT_OF_PRINT, BookEntry.IS_OUT_OF_PRINT);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "OldBooksSupplier");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, "2025550122");

        // Inserting data, this method returns the ID of the newly inserted rows.
        // returns -1 in case of error.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, "New row ID: " + newRowId);
        Toast.makeText(this, "Number of books into the database: " + newRowId, Toast.LENGTH_SHORT).show();
        queryBookData();
    }

    /**
     * Query the database to retrieve the product name (book title), the price,
     * whether it is out of print or not.
     * At the end of thr reading close the cursor to releases all its resources
     * and makes it invalid.
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

        // Create an instance of the ListView displaying the list of books of the inventory.
        mBookListView = (ListView) findViewById(R.id.book_listview);

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

        return super.onOptionsItemSelected(item);
    }
}
