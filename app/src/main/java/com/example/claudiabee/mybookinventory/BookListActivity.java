package com.example.claudiabee.mybookinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu option buttons to the app bar
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Start action related to the menu option clicked by user from overflow menu in the app bar
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
        }
        return super.onOptionsItemSelected(item);
    }
}
