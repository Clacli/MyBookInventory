package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    // Variable associated to listener for any touches on a View
    private boolean mBookHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.
    private View.OnTouchListener mTouchListener =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mBookHasChanged = true;
                    return false;
                }
            };

    // The URI referring to the book stored in the database of which we want to
    // see the detail
    private Uri mBookUri;

    /** EditText field to enter the name of the new book to insert into a database */
    @BindView(R.id.edit_book_title) EditText mEditBookTitle;

    /** EditText field to enter the price of the new book to insert into a database */
    @BindView(R.id.edit_book_price) EditText mEditBookPrice;

    /**
     * TextView displaying the quantity of books passed from the BookDetailActivity
     */
    @BindView(R.id.received_book_quantity) TextView mBookQuantityTextView;

    /** EditText field to enter the quantity of the new book to insert into a database */
    @BindView(R.id.edit_book_quantity) EditText mEditBookQuantity;

    /** EditText field to enter the name of the supplier of the book to insert into a database */
    @BindView(R.id.edit_supplier_name) EditText mEditSupplierName;

    /** EditText field to enter the phone number of the suppliers of books to insert into a database */
    @BindView(R.id.edit_supplier_phone_number) EditText mEditSupplierPhoneNumber;

    /** Spinner from which to chose the option about the book being out of print or not */
    @BindView(R.id.edit_info_on_book_production_spinner) Spinner mProductionInfoSpinner;

    /**
     * Info whether the book is out of print or not. The possible valid values are:
     * {@link BookEntry#CHECK_IF_OUT_OF_PRINT}, {@link BookEntry#NOT_OUT_OF_PRINT},
     * {@link BookEntry#IS_OUT_OF_PRINT}.
     */
    private int mProductionInfo = BookEntry.CHECK_IF_OUT_OF_PRINT;

    /**
     * Minus button increase the quantity of books
     */
    @BindView(R.id.minus_button) Button mMinusButton;

    /**
     * Plus button increase the quantity of books
     */
    @BindView(R.id.plus_button) Button mPlusButton;

    /** When this Fab gets pressed book record update is saved */
    @BindView(R.id.edit_fab) FloatingActionButton editFab;



    /**
     * The quantity of book returned by the query
     */
    private int mBookQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        ButterKnife.bind(this);

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
        //mEditBookTitle = (EditText) findViewById(R.id.edit_book_title);
        //mEditBookPrice = (EditText) findViewById(R.id.edit_book_price);
        //mBookQuantityTextView = (TextView) findViewById(R.id.received_book_quantity);
        //mEditSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        //mEditSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);
        //mProductionInfoSpinner = (Spinner) findViewById(R.id.edit_info_on_book_production_spinner);
        //mEditBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);
        //mMinusButton = (Button) findViewById(R.id.minus_button);
        //mPlusButton = (Button) findViewById(R.id.plus_button);


        // Attach the OnTouchListener to the Views and to the Spinner objects to detect
        // any input from the user
        mEditBookTitle.setOnTouchListener(mTouchListener);
        mEditBookPrice.setOnTouchListener(mTouchListener);
        mEditBookQuantity.setOnTouchListener(mTouchListener);
        mEditSupplierName.setOnTouchListener(mTouchListener);
        mEditSupplierPhoneNumber.setOnTouchListener(mTouchListener);
        mProductionInfoSpinner.setOnTouchListener(mTouchListener);
        mEditBookQuantity.setOnTouchListener(mTouchListener);



        //Set onClickListener on minus button
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the int value of mBookQuantity from mBookQuantityTextView
                mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
                // Get the string input from the EditText
                String decreaseByQuantityString = mEditBookQuantity.getText().toString().trim();
                // Check if the user has left an input in the EditText
                if (TextUtils.isEmpty(decreaseByQuantityString)) {
                    if (mBookQuantity == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_negative_values_message, Toast.LENGTH_LONG).show();
                    } else {
                        // Decrease mBookQuantity by one
                        mBookQuantity--;
                        // Set the updated quantity of book in the mBookQuantityTextView
                        mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                    }
                } else {
                    // Get the user input from the mEditBookQuantity
                    int decreaseByQuantity = Integer.parseInt(decreaseByQuantityString);
                    // Subtract the user input quantity from the quantity stored into the database
                    if (decreaseByQuantity > mBookQuantity) {
                        Toast.makeText(getApplicationContext(), R.string.no_negative_values_message, Toast.LENGTH_LONG).show();
                        mEditBookQuantity.setText("");
                    } else {
                        mBookQuantity = mBookQuantity - decreaseByQuantity;
                        // Set the new value of mBookQuantity on the mBookQuantityTextView
                        mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                        // Clear the mEditBookQuantity EditText
                        mEditBookQuantity.setText("");

                    }
                }

            }
        });

        //Set onClickListener on plus button
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the int value of mBookQuantity from mBookQuantityTextView
                mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
                // Get the string input from the EditText
                String increaseByQuantityString = mEditBookQuantity.getText().toString().trim();
                // Check if the user has left an input in the EditText
                if (TextUtils.isEmpty(increaseByQuantityString)) {
                        // Increase mBookQuantity by one
                        mBookQuantity++;
                    // Set the updated quantity of book in the mBookQuantityTextView
                        mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                } else {
                    // Get the int value of mBookQuantity from mBookQuantityTextView
                    mBookQuantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
                    // Get the user input from the mEditBookQuantity
                    int increaseByQuantity = Integer.parseInt(mEditBookQuantity.getText().toString().trim());
                    // Add the user input quantity from the quantity stored into the database
                    mBookQuantity = mBookQuantity + increaseByQuantity;
                    // Set the new value of mBookQuantity on the mBookQuantityTextView
                    mBookQuantityTextView.setText(String.valueOf(mBookQuantity));
                    // Clear the mEditBookQuantity EditText
                    mEditBookQuantity.setText("");

                }
            }
        });

        // Setup spinner
        setupSpinner();

        // Create an instance of the FAB in the edit_book_layout
        // FloatingActionButton editFab = (FloatingActionButton) findViewById(R.id.edit_fab);
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
        String bookPriceString = mEditBookPrice.getText().toString().trim();
        String bookQuantityString = mBookQuantityTextView.getText().toString().trim();
        String supplierName = mEditSupplierName.getText().toString().trim();
        String supplierPhoneNumberString = mEditSupplierPhoneNumber.getText().toString().trim();
        //

        //Check if all the fields in the editor are blank
        if (TextUtils.isEmpty(bookTitle) && TextUtils.isEmpty(bookPriceString) &&
                TextUtils.isEmpty(bookQuantityString) && (TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhoneNumberString) &&
                mProductionInfo == BookEntry.CHECK_IF_OUT_OF_PRINT)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(getApplicationContext(), R.string.ask_for_valid_inputs_message,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        // Create a ContentValues object. It specifies what data we want to insert
        ContentValues values = new ContentValues();

        // Populate the ContentValues object with
        // key (column name) - values (obtained from the user input) and use it later
        // to insert a new book into the books table of the database.
        if (TextUtils.isEmpty(bookTitle)) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_a_valid_title_message,
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            values.put(BookEntry.COLUMN_BOOK_TITLE, bookTitle);
        }
        // If the price of the book is not provided by the user, do not try to parse the String into
        // an integer value.
        if (TextUtils.isEmpty(bookPriceString)) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_valid_price_message,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isEmpty(bookPriceString) && Double.parseDouble(bookPriceString) < 0) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_valid_price_message,
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            double bookPrice = Double.parseDouble(bookPriceString);
            values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        }
        // If the quantity of the book is not provided by the user, do not try to parse the String into
        // an integer value.
        if (TextUtils.isEmpty(bookQuantityString)) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_quantity_message,
                    Toast.LENGTH_SHORT).show();
                    return;
        } else if (!TextUtils.isEmpty(bookQuantityString) && Integer.parseInt(bookQuantityString) < 0) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_quantity_message,
                    Toast.LENGTH_SHORT).show();
                    return;
        } else {
            int mBookQuantity = Integer.parseInt(bookQuantityString);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, mBookQuantity);
        }
        values.put(BookEntry.COLUMN_BOOK_PRODUCTION_INFO, mProductionInfo);
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_supplier_name_message,
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        }
        // If the phone number of the book supplier is not provided by the user, do not try to
        // parse the String into an integer value.
        if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_valid_phone_number_message,
                    Toast.LENGTH_SHORT).show();
                    return;
        } else if (!TextUtils.isEmpty(supplierPhoneNumberString) && Long.parseLong(supplierPhoneNumberString) < 0) {
            Toast.makeText(getApplicationContext(), R.string.ask_for_valid_phone_number_message,
                    Toast.LENGTH_SHORT).show();
                    return;
        } else {
            long supplierPhoneNumber = Long.parseLong(supplierPhoneNumberString);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/edit_book_menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.edit_book_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    finish();
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, navigate to parent activity.
                                // I don\'t call:
                                // NavUtils.navigateUpFromSameTask(EditBookActivity.this);
                                // it makes the app  navigate to the parent activity which
                                // present itself with empty fields. Calling
                                finish();
                                // makes you go back to the previous activity, which is in this case
                                // the {@link DetailActivity}, with all its data untouched, unchanged
                                // and present.
                            }
                        };
                // Show a dialog that notifies the user of unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Hook up the back button to the AlertDialog
    @Override
    public void onBackPressed() {
        // If the book han not changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClicklistener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked "Discard" button, close the current activity
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClicklistener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonclickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners for the
        // negative and positive buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonclickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue
                // editing the book
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
