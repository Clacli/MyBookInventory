package com.example.claudiabee.mybookinventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BookListActivity extends AppCompatActivity {

    // This String constant is used for logging
    public final static String LOG_TAG = BookListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
    }
}
