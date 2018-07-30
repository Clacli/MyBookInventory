package com.example.claudiabee.mybookinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
}
