package com.example.claudiabee.mybookinventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the BookInventory app.
 * This contract defines the table and the column names for the bookshop.db database and
 * the possible values to pass whether the book is out pf print or it is not or this information is
 * to be checked.
 */
public final class MyBookInventoryContract {

    // Empty private constructor for the MyBookInventoryContract, to prevent it to be instantiated by mistake.
    // The contract is for defining the table and the constants referred to the column
    // of the books database.
    private MyBookInventoryContract() {
    }

    /** This string is the content authority used to identify the Content Provider */
    public static final String CONTENT_AUTHORITY = "com.example.claudiabee.mybookinventory";

    /** This URI will be shared by every URI associated with the ContractClass */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** This constant String stores the path for the books table, which will be appended
     * to the BASE_CONTENT_URI to form a complete CONTENT_URI. In the contract there must be
     * defined a path to each table present in the database. At the moment in this app the database
     * has one table so only one path must be defined in a constant string
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constants values for the books table of the bookshop database.
     * Each entry in the table represent a book.
     * The inner class implements BaseColumns interface inheriting the primary key field _ID.
     */
    public static final class BookEntry implements BaseColumns {

        /** Complete CONTENT URI for the BookEntry class to access data in the books table */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Name of the books table of the bookshop.db
         */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for the book.
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * The title of the book.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_TITLE = "product_name";

        /**
         * The price of the book.
         * <p>
         * Type: REAL
         */
        public static final String COLUMN_BOOK_PRICE = "price";

        /**
         * The quantity of books.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * Information on whether the book {@link #IS_OUT_OF_PRINT}, it is {@link #NOT_OUT_OF_PRINT}
         * or {@link #CHECK_OUT_OF_PRINT}.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_OUT_OF_PRINT = "out_of_print";

        /**
         * The name of the supplier for the bookshop
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        /**
         * The phone number of the supplier of books
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Possible value for the information on the "out of print" state of the book.
         */
        public static final int CHECK_OUT_OF_PRINT = 0;
        public static final int NOT_OUT_OF_PRINT = 1;
        public static final int IS_OUT_OF_PRINT = 2;

    }
}
