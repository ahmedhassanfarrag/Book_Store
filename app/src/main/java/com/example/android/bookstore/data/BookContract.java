package com.example.android.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    private BookContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstore";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookstore//is a valid path for
     * looking at book data. content://com.example.android.bookstore/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_Books = "BookStore";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */

    public static abstract class BookEntry implements BaseColumns {

        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_Books);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Books;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Books;


        public static final String TABLE_NAME = "BookStore";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_Book_Name = "Name";
        public static final String COLUMN_Book_Price = "Price";
        public static final String COLUMN_Book_Quantity = "Quantity";
        public static final String COLUMN_Book_SupplierName = "SupplierName";
        public static final String COLUMN_Book_SupplierPhone = "SupplierPhone";
        public static final String COLUMN_Book_PHOTO = "book_photo";
    }
}
