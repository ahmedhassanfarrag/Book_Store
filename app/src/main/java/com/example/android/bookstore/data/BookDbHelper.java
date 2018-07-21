package com.example.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    //Database version. If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    //Name of the database file.
    public static final String DATABASE_NAME = "BookInventory.dp";

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app.
     */

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the BookStore table
        String SQL_CREATE_Book_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_Book_Name + " TEXT NOT NULL, "
                + BookEntry.COLUMN_Book_Price + " DECIMAL(2) NOT NULL , "
                + BookEntry.COLUMN_Book_Quantity + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_Book_SupplierName + " TEXT NOT NULL,"
                + BookEntry.COLUMN_Book_SupplierPhone + " TEXT NOT NULL,"
                + BookEntry.COLUMN_Book_PHOTO + " TEXT" + ");";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_Book_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
