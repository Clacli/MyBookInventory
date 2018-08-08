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
import android.widget.TextView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryDbHelper;
import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * Displays a list of books stored in a database
 */
public class BookListActivity extends AppCompatActivity {

    /** This String constant is used for logging */
    public final static String LOG_TAG = BookListActivity.class.getSimpleName();

    /** This database helper class helps crating, opening, modifying the existing databases. */
    private MyBookInventoryDbHelper mMyBookInventoryDbHelper;

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

        // Define a projection array of strings
        String[] projection = {
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

        //This TextView displays the list of books saved in the books table of the bookshop database.
        TextView displayRecordsView = (TextView) findViewById(R.id.book_text_view);

        try {
            // This line of code is very important, it prevents the results of the query
            // to be displayed multiple times after a record book insertion.
            // By the way it displays the count of the book records in the books table
            // of the database.
            displayRecordsView.setText(String.format(getString(R.string.book_count_message), cursor.getCount()));
            // Find the index for each of the selected column
            int bookTitleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

            // Loop through each row of the cursor, and at each position of the cursor
            // extract the desired String, double and int values using the indices obtained
            // above.
            while (cursor.moveToNext()) {
                String currentBookTitle = cursor.getString(bookTitleColumnIndex);
                double currentBookPrice = cursor.getDouble(bookPriceColumnIndex);
                int currentBookQuantity = cursor.getInt(bookQuantityColumnIndex);

                // Display the values just retrieved from this row on the screen
                displayRecordsView.append(
                        "\n" + currentBookTitle + " - "
                             + currentBookPrice + " - "
                             + currentBookQuantity + "\n");
            }
        } finally {
            // Close the cursor and release all of its resources
            cursor.close();
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
}
