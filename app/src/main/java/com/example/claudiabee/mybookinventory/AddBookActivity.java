package com.example.claudiabee.mybookinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

import java.net.URI;

/**
 * In this Activity the user can create a new book and store it in a database.
 */
public class AddBookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private URI uri = null;

    /** This String constants is for logging */
    public static final String LOG_TAG = AddBookActivity.class.getSimpleName();

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
    private Spinner mProductionInfoSpinner;

    /**
     * Info whether the book is out of print or not. The possible valid values are:
     * {@link BookEntry#CHECK_IF_OUT_OF_PRINT}, {@link BookEntry#NOT_OUT_OF_PRINT},
     * {@link BookEntry#IS_OUT_OF_PRINT}.
     */
    private int mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Instantiate the EditText that we will need to read user input from
        mEditBookTitle = (EditText) findViewById(R.id.add_book_title);
        mEditBookPrice = (EditText) findViewById(R.id.add_book_price);
        mEditBookQuantity = (EditText) findViewById(R.id.add_book_quantity);
        mEditSupplierName = (EditText) findViewById(R.id.add_supplier_name);
        mEditSupplierPhoneNumber = (EditText) findViewById(R.id.add_supplier_phone_number);
        mProductionInfoSpinner = (Spinner) findViewById(R.id.add_info_on_book_production_spinner);

        // Find the Fab with ID == fab and set an action upon it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBook();
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
        ArrayAdapter infoAdapter =
                ArrayAdapter.createFromResource(this,
                R.array.array_production_info_options, android.R.layout.simple_spinner_item);

        // Specify the layout style to use when the list of choices appears
        infoAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the infoAdapter to the spinner
        mProductionInfoSpinner.setAdapter(infoAdapter);

        // Set the integer selected to the constant values
        mProductionInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (selection.equals(getString(R.string.not_out_of_print))) {
                    mProductionInfo = BookEntry.NOT_OUT_OF_PRINT;
                } else if (selection.equals(getString(R.string.yes_out_of_print))) {
                    mProductionInfo = BookEntry.IS_OUT_OF_PRINT;
                } else {
                    mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;
            }
        });
    }

    /**
     * Get the user input from and save it in the books table of database as a new book entry
     */
    private void insertBook() {

        // Get the input entered by the user from the EditText
        String bookTitle = mEditBookTitle.getText().toString().trim();
        double bookPrice = Double.parseDouble(mEditBookPrice.getText().toString().trim());
        int bookQuantity = Integer.parseInt(mEditBookQuantity.getText().toString().trim());
        String supplierName = mEditSupplierName.getText().toString().trim();
        long supplierPhoneNumber = Long.parseLong(mEditSupplierPhoneNumber.getText().toString().trim());

        // Check if all the fields in the editor are blank
        /*if (TextUtils.isEmpty(bookTitle) && TextUtils.isEmpty(String.valueOf(bookPrice)) &&
                TextUtils.isEmpty(String.valueOf(bookQuantity)) && (TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(String.valueOf(supplierPhoneNumber)) &&
                mProductionInfo == BookEntry.CHECK_IF_OUT_OF_PRINT)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.no_input_entered_message,
                    Toast.LENGTH_SHORT).show();
            return;
        }*/

        // Create a ContentValues object. It specifies what data we want to insert
        ContentValues values = new ContentValues();

        // Populate the ContentValues object with
        // key (column name) - values (obtained from the user input) and use it later
        // to insert a new book into the books table of the database.
        values.put(BookEntry.COLUMN_BOOK_TITLE, bookTitle);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_PRODUCTION_INFO, mProductionInfo);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        // Insert ContentValues into the database and store the returned URI of the
        // newly inserted row into variable of type long
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Display in a toast message the ID of the newly row or an error message
        // in case of failed insertion
        if (newUri == null) {
            Toast.makeText(this, R.string.insert_error_text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.successful_insert_text, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
