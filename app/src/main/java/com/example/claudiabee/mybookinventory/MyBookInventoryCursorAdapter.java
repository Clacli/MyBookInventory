package com.example.claudiabee.mybookinventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;


/**
 * {@link MyBookInventoryCursorAdapter} is an adapter that knows how to create list items for a
 * list of books held by a {@link Cursor}.
 */
public class MyBookInventoryCursorAdapter extends CursorAdapter {
    // The quantity of books
    private int mQuantity;

    // The textview holding the quantity of books
    private TextView mBookQuantityTextView;

    /**
     * This is the constructor for a new {@link MyBookInventoryCursorAdapter}.
     *
     * @param context is the context
     * @param cursor is the source of books data.
     */
    public MyBookInventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * This method inflates and return a new list item view.
     *
     * @param context is the context
     * @param cursor is the source of books data.
     * @param parent is the parent view to which the new list item view is attached.
     * @return a new list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }

    /**
     * This methods binds the book data found at the current position of the
     * cursor to a list item view.
     * @param view is a list item view
     * @param context the context of the app
     * @param cursor is the source of book data and is already moved to the correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Find views to populate in inflated view
        TextView bookTitleTextView = (TextView) view.findViewById(R.id.product_name_textview);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.price_textview);
        mBookQuantityTextView = (TextView) view.findViewById(R.id.quantity_textview); // make global

        // Extract properties from cursor
     //   long bookId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)); // The id index
        String bookTitle = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE));
        Double bookPrice = cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
        mQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)); // make global

        // Populate textviews with data extracted
        bookTitleTextView.setText(bookTitle);
        bookPriceTextView.setText(String.valueOf(bookPrice));
        mBookQuantityTextView.setText(String.valueOf(mQuantity));

        // Find the SaleButton
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        // setTag to saleButton (and getTag later in onClick method so that the sale button works
        // in all list items)

        // Set an onClickListener on the sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move cursor to the next position
                if (cursor.moveToFirst()){
                    // Display toast message that negative numbers are not allowed
                    if(mQuantity <= 0){
                        Toast.makeText(v.getContext(), R.string.no_negative_values_message, Toast.LENGTH_LONG).show();
                        return;
                    }
                // Decrease the quantity by one
                mQuantity--;

                // Create a new ContentValues object
                ContentValues values = new ContentValues();
                // Populate the ContentValues with the updated quantity of the current book
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);

                // Update the row with the new quantity of books
                int updatedRow = v.getContext().getContentResolver().update(BookEntry.CONTENT_URI, values, null, null);
                    // Show a toast message whether the book was updated or if the update was successful
                    if (updatedRow == 0) {
                        // No rows were updated
                        Toast.makeText(v.getContext(), R.string.error_update_message, Toast.LENGTH_SHORT).show();
                    } else {
                        // The book was updated
                        Toast.makeText(
                                v.getContext(), R.string.successful_update_message, Toast.LENGTH_SHORT).show();
                    }

                };

            }
        });
    }
}
