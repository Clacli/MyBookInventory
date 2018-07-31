package com.example.claudiabee.mybookinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * In this Activity the user can create a new pet and store it in a database.
 */
public class BookManagingActivity extends AppCompatActivity {

    /** This String constants is for logging */
    public static final String LOG_TAG = BookManagingActivity.class.getSimpleName();

    /** EditText field to enter the name of the new book to insert into a database */
    private EditText mEditBookTitle;

    /** EditText field to enter the price of the new book to insert into a database */
    private EditText mEditBookPrice;

    /** EditText field to enter the quantity of the new book to insert into a database */
    private EditText mEditBookQuantity;

    /** EditText field to enter the name of the supplier of the book to insert into a database */
    private EditText mEditSupplierName;

    /** EditText field to enter the phone number of the suppliers of books to insert into a database */
    private EditText mEditSupplierPhoneNumber;

    /** Spinner from which to chose the option about the book being out of print or not */
    private Spinner mOutOfPrintSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_managing);

        // Instantiate the EditText that we will need to read user input from
        mEditBookTitle = (EditText) findViewById(R.id.edit_book_title);
        mEditBookPrice = (EditText) findViewById(R.id.edit_book_price);
        mEditBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);
        mEditSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mEditSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mOutOfPrintSpinner = (Spinner) findViewById(R.id.out_of_print_spinner);

        // Find the Fab with ID == fab and set an action upon it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookManagingActivity.this, "This will add the book to the db and take back to BookListActivity", Toast.LENGTH_LONG).show();
                // InsertData();
                finish();
            }
        });

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select whether or not the book
     * is out of print.
     */
    private void setupSpinner() {
        // Create the adapter for the spinner. The list options from which the user choose,
        // are stored in the String array in arrays.xml, the spinner will use the default layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.array_out_of_print_options, android.R.layout.simple_spinner_item);

        // Specify the layout style to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mOutOfPrintSpinner.setAdapter(adapter);
    }
}
