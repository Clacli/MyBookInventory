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

import com.example.claudiabee.mybookinventory.data.BookDbHelper;
import com.example.claudiabee.mybookinventory.data.BookContract.BookEntry;

public class BookListActivity extends AppCompatActivity {

    // This String constant is used for logging
    public final static String LOG_TAG = BookListActivity.class.getSimpleName();

    // This database helper class helps crating, opening, modifying the existing databases.
    private BookDbHelper mBookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mBookDbHelper = new BookDbHelper(this);

        // Create a Fab instanceand store it in a variable
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Fab is clicked an intent is sent to open the BookManagingActivity
                startActivity(new Intent(BookListActivity.this, BookManagingActivity.class));
            }
        });
        // When onCreate is called the number of rows (number of books) in the books table is
        // displayed on the screen.
        displayDatabaseInfo();
    }

    // When the activity restarts the count of the rows updates.
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books table of the bookshop.db database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM books"
        // to get a Cursor that contains all rows from the books table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + BookEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).
            TextView displayView = (TextView) findViewById(R.id.book_TextView);
            displayView.setText("Number of rows in books database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * This method, used for debugging purpose insert hardcoded sample data about
     * one book into the database.
     */
    private void insertBook() {

        // Create and/or open a database in write mode
        SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

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
        // Update the text displayed on the screen in the sample TextView
        insertBook();
        displayDatabaseInfo();

        //Toast.makeText(BookListActivity.this,
        //"Clicking this will add a sample record about a book", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
