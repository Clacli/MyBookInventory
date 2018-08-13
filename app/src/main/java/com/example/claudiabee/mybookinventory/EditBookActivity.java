package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * Minus button increase the quantity of books
     */
    Button mMinusButton;

    /**
     * Plus button increase the quantity of books
     */
    Button mPlusButton;

    /**
     * TextView displaying the quantity of books passed from the BookDetailActivity
     */
    private TextView mBookQuantityTextView;

    /**
     * The quantity of book returned by the query
     */
    private int mBookQuantity;

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
        mBookQuantityTextView = (TextView) findViewById(R.id.received_book_quantity);
        mEditSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mEditSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mProductionInfoSpinner = (Spinner) findViewById(R.id.edit_info_on_book_production_spinner);

        mEditBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);

        mMinusButton = (Button) findViewById(R.id.minus_button);
        mPlusButton = (Button) findViewById(R.id.plus_button);

        //Set onClickListener on minus button
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the int value of mBookQuantity from mBookQuantityTextView
                mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
                // Get the user input from the mEditBookQuantity
                int decreaseBy = Integer.parseInt(mEditBookQuantity.getText().toString().trim());
                // Subtract the user input quantity from the quantity stored into the database
                if (decreaseBy > mBookQuantity) {
                    Toast.makeText(getApplicationContext(), R.string.no_negative_values_message, Toast.LENGTH_LONG).show();
                    mEditBookQuantity.setText("");
                    return;
                } else {
                    mBookQuantity = mBookQuantity - decreaseBy;
                    // Set the new value of mBookQuantity on the mBookQuantityTextView
                    mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                    // Clear the mEditBookQuantity EditText
                    mEditBookQuantity.setText("");
                }

            }
        });

        //Set onClickListener on plus button
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the int value of mBookQuantity from mBookQuantityTextView
                mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
                // Get the user input from the mEditBookQuantity
                int increaseBy = Integer.parseInt(mEditBookQuantity.getText().toString().trim());
                // Add the user input quantity from the quantity stored into the database
                mBookQuantity = mBookQuantity + increaseBy;
                // Set the new value of mBookQuantity on the mBookQuantityTextView
                mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                // Clear the mEditBookQuantity EditText
                mEditBookQuantity.setText("");
            }
        });

        // Setup spinner
        setupSpinner();

        // Create an instance of the FAB in the edit_book_layout
        FloatingActionButton editFab = (FloatingActionButton) findViewById(R.id.edit_fab);
        // Set an onClickListener on the fab
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the FAB gets clicked the book gets updated and the activity finishes going
                // going back to the BookDetailActivity
                updateBook();
                finish();
            }
        });
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
                        if (selection.equals(getString(R.string.not_out_of_print))) {
                            mProductionInfo = BookEntry.NOT_OUT_OF_PRINT;
                        } else if (selection.equals(getString(R.string.yes_out_of_print))){
                            mProductionInfo = BookEntry.IS_OUT_OF_PRINT;
                        } else  {
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
     * This method gets the user's input from the editor and update the information about books
     */
    private void updateBook() {
        // Read from the input fields and get the values to be passed into the ContentValue Object
        String bookTitle = mEditBookTitle.getText().toString().trim();
        double bookPrice = Double.parseDouble(mEditBookPrice.getText().toString().trim());
        mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString().trim());
        String supplierName = mEditSupplierName.getText().toString().trim();
        long supplierPhoneNumber = Long.parseLong(mEditSupplierPhoneNumber.getText().toString().trim());

        /*// Check if all the fields in the editor are blank
        if (TextUtils.isEmpty(bookTitle) && TextUtils.isEmpty(String.valueOf(bookPrice)) &&
                TextUtils.isEmpty(String.valueOf(mBookQuantity)) && (TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(String.valueOf(supplierPhoneNumber)) &&
                mProductionInfo == BookEntry.CHECK_IF_OUT_OF_PRINT)) {
            // Since no fields were modified, we can return early without updating a new book.
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
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mBookQuantity);
        values.put(BookEntry.COLUMN_BOOK_PRODUCTION_INFO, mProductionInfo);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        // Update the book already existing in the database, returning the number of rows affected
        // by the update
        int updatedRowNumber = getContentResolver().update(
                mBookUri, values, null, null);

        // Show a toast message whether the book was updated or if the update was successful
        if (updatedRowNumber == 0) {
            // No rows were updated
            Toast.makeText(getApplicationContext(), R.string.error_update_message, Toast.LENGTH_SHORT).show();
        } else {
            // The book was updated
            Toast.makeText(
                    getApplicationContext(), R.string.successful_update_message, Toast.LENGTH_SHORT).show();
        }

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
            mBookQuantityTextView.setText(Integer.toString(quantity));
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
        mBookQuantityTextView.setText("");
        mEditSupplierName.setText("");
        mEditSupplierPhoneNumber.setText("");
        mProductionInfoSpinner.setSelection(0); // Select "Check" option
    }
}
