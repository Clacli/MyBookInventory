package com.example.claudiabee.mybookinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.claudiabee.mybookinventory.data.MyBookInventoryContract.BookEntry;

/**
 * {@link MyBookInventoryCursorAdapter} is an adapter that knows how to create list items for a
 * list of books held by a {@link Cursor}.
 */
public class MyBookInventoryCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {

        // Find views to populate in inflated view
        TextView bookTitleTextView = (TextView) view.findViewById(R.id.product_name_textview);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.price_textview);
        TextView bookQuantityTextView = (TextView) view.findViewById(R.id.quantity_textview); // make global

        // Extract properties from cursor
        String bookTitle = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE));
        Double bookPrice = cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
        int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)); // make global

        // Populate textviews with data extracted
        bookTitleTextView.setText(bookTitle);
        bookPriceTextView.setText(String.valueOf(bookPrice));
        bookQuantityTextView.setText(String.valueOf(bookQuantity));


    }
}
