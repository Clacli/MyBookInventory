package com.example.claudiabee.mybookinventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Find the Fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookListActivity.this, "This will open another activity soon", Toast.LENGTH_LONG).show();
                startActivity(new Intent (BookListActivity.this, BookManagingActivity.class));
            }
        });

        // Check if everything is ok by now and the bookshop.db has been created
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + BookEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.book_TextView);
            displayView.setText("Number of rows in books database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
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
        /* Start action related to the menu option clicked by user from overflow menu in the app bar
        switch (item.getItemId()) {
            // Insert sample book data if user click this option menu
            case R.id.action_add_sample_data:
                // Insert sample data in database
                // Update the text displayed on the screen in the sample TextView
                Toast.makeText(BookListActivity.this, "Clicking this will add a sample record about a book", Toast.LENGTH_LONG).show();
                return true;
            // Delete all record about book in the table
            case R.id.action_delete_all_data:
                // Implement later
                Toast.makeText(BookListActivity.this, "This will delete all of the database books table", Toast.LENGTH_LONG).show();
                return true;
        }*/

        // Insert sample data in database
        // Update the text displayed on the screen in the sample TextView
        Toast.makeText(BookListActivity.this, "Clicking this will add a sample record about a book", Toast.LENGTH_LONG).show();


        return super.onOptionsItemSelected(item);
    }
}
