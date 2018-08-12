package com.example.claudiabee.mybookinventory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

public class EditBookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    /** This String constant is used for logging */
    public final static String LOG_TAG = EditBookActivity.class.getSimpleName();

    // This String constant represent the loader ID
    private static final int BOOK_URI_LOADER = 0;

    // String[] LOADER_PROJECTION represent the columns of the table
    // to be passed into the onCreateLoader methods
    private static final String[] LOADER_PROJECTION = {
            BookEntry._ID,
            BookEntry.COLUMN_BOOK_TITLE,
            BookEntry.COLUMN_BOOK_PRICE,
            BookEntry.COLUMN_BOOK_QUANTITY,
            BookEntry.COLUMN_BOOK_PRODUCTION_INFO,
            BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
            BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER
    };


    // The URI referring to the book stored in the database of which we want to
    // see the detail
    private Uri mBookUri;

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
        setContentView(R.layout.activity_edit_book);

        // Return the Intent that started this activity in edit mode using the getIntent()
        Intent intent = getIntent();
        // Get the URI passed by the intent from the BookDetailActivity
        mBookUri = intent.getData();

        if (mBookUri != null) {
            // Prepare the loader. Either re-connect with an existing one,
            // or start a new one.
            getSupportLoaderManager().initLoader(BOOK_URI_LOADER, null, this);
        }

        // Instantiate the EditText that we will need to read user input from
        mEditBookTitle = (EditText) findViewById(R.id.edit_book_title);
        mEditBookPrice = (EditText) findViewById(R.id.edit_book_price);
        mEditBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);
        mEditSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mEditSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mProductionInfoSpinner = (Spinner) findViewById(R.id.edit_info_on_book_production_spinner);

        // Setup spinner
        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the information whether the book is
     * out of print or not or if this information must be checked
     */
    private void setupSpinner(){
        // Create an adapter for the spinner. The list options from which the user choose,
        // are stored in the String array in arrays.xml, the spinner will use the default layout
        ArrayAdapter infoAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_production_info_options, android.R.layout.simple_spinner_item);

        // Specify the layout style to use when the list of choices appears
        infoAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the infoAdapter to the spinner
        mProductionInfoSpinner.setAdapter(infoAdapter);

        //Set the integer selected to the constant values
        mProductionInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (TextUtils.isEmpty(selection)) {
                    if (TextUtils.isEmpty(selection)) {
                        if (selection.equals(getString(R.string.not_out_of_print))) {
                            mProductionInfo = BookEntry.NOT_OUT_OF_PRINT;
                        } else if (selection.equals(getString(R.string.yes_out_of_print))){
                            mProductionInfo = BookEntry.IS_OUT_OF_PRINT;
                        } else  {
                            mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define the projection for the query
        String[] projection = LOADER_PROJECTION;

        // Takes action based on the ID of the loader that's being created
        // The input to the CursorLoader constructor, looks like the content resolver query method
        return new CursorLoader(
                this,       // Parent activity context
                mBookUri,             // Table to query
                projection,         // Projection to return
                null,      // No selection clause
                null,   // No selection arguments
                null       // Default sort order
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // return early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Move the cursor to the 0th position, the first row of the cursor, and reading data
        // from it. This should be the only row in the Cursor.
        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int productionInfoIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCTION_INFO);
            int supplierNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleIndex);
            Double price = cursor.getDouble(priceIndex);
            int quantity = cursor.getInt(quantityIndex);
            int productionInfo = cursor.getInt(productionInfoIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            long supplierPhoneNumber = cursor.getLong(supplierPhoneNumberIndex);

            // Update the views on the screen with the values from the database
            mEditBookTitle.setText(title);
            mEditBookPrice.setText(Double.toString(price));
            mEditBookQuantity.setText(Integer.toString(quantity));
            mEditSupplierName.setText(supplierName);
            mEditSupplierPhoneNumber.setText(Long.toString(supplierPhoneNumber));

            // Display the information about the current production of the book returned
            // by the cursor
            switch(productionInfo) {
                case BookEntry.NOT_OUT_OF_PRINT:
                    mProductionInfoSpinner.setSelection(1);
                    break;
                case BookEntry.IS_OUT_OF_PRINT:
                    mProductionInfoSpinner.setSelection(2);
                    break;
                default:
                    mProductionInfoSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the textviews.
        mEditBookTitle.setText("");
        mEditBookPrice.setText("");
        mEditBookQuantity.setText("");
        mEditSupplierName.setText("");
        mEditSupplierPhoneNumber.setText("");
        mProductionInfoSpinner.setSelection(0); // Select "Check" option
    }
}
