package com.example.claudiabee.mybookinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

public class BookDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * This String constants is for logging
     */
    public static final String LOG_TAG = BookDetailActivity.class.getSimpleName();

    // This String constant represent the loader ID
    private static final int BOOK_URI_LOADER = 0;

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

    /**
     * TextView displaying the title of the book
     */
    private TextView mTitleTextView;

    /**
     * TextView displaying the price of the book
     */
    private TextView mPriceTextView;

    /**
     * TextView displaying the quantity of the book
     */
    private TextView mQuantityTextView;

    /**
     * TextView displaying the info on the production of the book
     */
    private TextView mInfoOnProduction;

    /**
     * TextView displaying the info on the supplier's name of the book
     */
    private TextView mSupplierNameTextView;

    /**
     * TextView displaying the info on the supplier's telephone number of the book
     */
    private TextView mSupplierPhoneNumberTextView;

    /**
     * Suppliers phone number
     */
    private long mSupplierPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Return the Intent that started this activity in edit mode using the getIntent()
        Intent intent = getIntent();
        // Get the URI passed by the intent
        mBookUri = intent.getData();

        if (mBookUri != null) {
            // Prepare the loader. Either re-connect with an existing one,
            // or start a new one.
            getSupportLoaderManager().initLoader(BOOK_URI_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleTextView = (TextView) findViewById(R.id.detail_book_title);
        mPriceTextView = (TextView) findViewById(R.id.detail_book_price);
        mQuantityTextView = (TextView) findViewById(R.id.detail_book_quantity);
        mInfoOnProduction = (TextView) findViewById(R.id.detail_info_on_book_production);
        mSupplierNameTextView = (TextView) findViewById(R.id.detail_supplier_name);
        mSupplierPhoneNumberTextView = (TextView) findViewById(R.id.detail_supplier_phone_number);

        // Set onClickListener on the editButton so that when the button gets clicked
        // an intent is sent to open the EditBookActivity
        Button editButton = (Button) findViewById(R.id.edit_book_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
                // Pass the data of the selected book to the new activity
                intent.setData(mBookUri);
                // Launch the {@link EditBookActivity} to edit the data for the current book.
                startActivity(intent);
            }
        });

        // Set onClickListener on the deleteButton so that when the button gets clicked
        // the current record gets deleted, going back to the parent activity
        Button deleteButton = (Button) findViewById(R.id.delete_book_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
                // Close the activity
            }
        });

        // Set onClickListener on the editButton so that when the button gets clicked
        // an intent is sent to open the phone to call the supplier's phone number
        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * This method gets the user's input from the editor and update the information about books
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mBookUri
            // content URI already identifies the book that we want.
            int rowDeleted = getContentResolver().delete(
                    mBookUri,            // the current URI of the book we want to remove from the database.
                    null,       // the column to select on
                    null); // the value to compare to

            // Show a toast message depending on whether or not the delete was successful.
            if (rowDeleted == 0) {
                // if no row were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.deletion_error_message, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.successful_deletion_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Creates the CursorLoader and defines the data to query from the content provider
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
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
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
            mSupplierPhoneNumber = cursor.getLong(supplierPhoneNumberIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(title);
            mPriceTextView.setText(Double.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneNumberTextView.setText(Long.toString(mSupplierPhoneNumber));

            // Display the information about the current production of the book returned
            // by the cursor
            switch(productionInfo) {
                case BookEntry.NOT_OUT_OF_PRINT:
                    mInfoOnProduction.setText(R.string.not_out_of_print);
                    break;
                case BookEntry.IS_OUT_OF_PRINT:
                    mInfoOnProduction.setText(R.string.yes_out_of_print);
                    break;
                default:
                    mInfoOnProduction.setText(R.string.check);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the textviews.
        mTitleTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mInfoOnProduction.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneNumberTextView.setText("");
    }
}
