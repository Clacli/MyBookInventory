package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * In this Activity the user can create a new book and store it in a database.
 */
public class AddBookActivity extends AppCompatActivity {

    /** This String constants is for logging */
    public static final String LOG_TAG = AddBookActivity.class.getSimpleName();

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

        // Attach the OnTouchListener to the Views and to the Spinner objects to detect
        // any input from the user
        mEditBookTitle.setOnTouchListener(mTouchListener);
        mEditBookPrice.setOnTouchListener(mTouchListener);
        mEditBookQuantity.setOnTouchListener(mTouchListener);
        mEditSupplierName.setOnTouchListener(mTouchListener);
        mEditSupplierPhoneNumber.setOnTouchListener(mTouchListener);
        mProductionInfoSpinner.setOnTouchListener(mTouchListener);
        mEditBookQuantity.setOnTouchListener(mTouchListener);

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
        String bookPriceString = mEditBookPrice.getText().toString().trim();
        String bookQuantityString = mEditBookQuantity.getText().toString().trim();
        String supplierName = mEditSupplierName.getText().toString().trim();
        String supplierPhoneNumberString = mEditSupplierPhoneNumber.getText().toString().trim();

        //Check if all the fields in the editor are blank
       if (TextUtils.isEmpty(bookTitle) && TextUtils.isEmpty(bookPriceString) &&
                TextUtils.isEmpty(bookQuantityString) && (TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhoneNumberString) &&
                mProductionInfo == BookEntry.CHECK_IF_OUT_OF_PRINT)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(getApplicationContext(), "No new book saved",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object. It specifies what data we want to insert
        ContentValues values = new ContentValues();

        // Populate the ContentValues object with
        // key (column name) - values (obtained from the user input) and use it later
        // to insert a new book into the books table of the database.
        values.put(BookEntry.COLUMN_BOOK_TITLE, bookTitle);
        // If the price of the book is not provided by the user, do not try to parse the String into
        // an integer value. Use 0.0 by default.
        double bookPrice = 0.0;
        if (!TextUtils.isEmpty(bookPriceString)) {
            bookPrice = Double.parseDouble(bookPriceString);
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        // If the quantity of books is not provided by the user, do not try to parse the String into
        // an integer value. Use 0 by default.
        int bookQuantity = 0;
        if (!TextUtils.isEmpty(bookQuantityString)) {
            bookQuantity = Integer.parseInt(bookQuantityString);
        }
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_PRODUCTION_INFO, mProductionInfo);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        // If the phone number of the book provider is not provided by the user, do not try to
        // parse the String into an integer value. Use 0 by default.
        long supplierPhoneNumber = 0;
        if (!TextUtils.isEmpty(supplierPhoneNumberString)) {
            supplierPhoneNumber = Long.parseLong(supplierPhoneNumberString);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/edit_book_menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.add_book_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddBookActivity.this);
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
        // If the pet han not changed, continue with handling back button press
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

    private void showUnsavedChangesDialog (
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
