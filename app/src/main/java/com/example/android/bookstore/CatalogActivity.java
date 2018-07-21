package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,BookCursorAdapter.OnSaleBtnClickListener {
    private static final int BookLOADER_ID = 0;
    BookCursorAdapter mCursorAdapter;
    private Uri uri = BookEntry.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the pet data
        ListView BookListView = (ListView) findViewById(R.id.book_list);

        mCursorAdapter = new BookCursorAdapter(this, null);
        BookListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(BookLOADER_ID, null, this);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        BookListView.setEmptyView(emptyView);

        BookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, InfoActivity.class);
                Uri uriForDetail = ContentUris.withAppendedId(uri, id);
                intent.setData(uriForDetail);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Delete all entries" menu option
            case R.id.catalog_delete:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_Book_Name,
                BookEntry.COLUMN_Book_Price,
                BookEntry.COLUMN_Book_Quantity};
        switch (id) {
            case BookLOADER_ID:
                return new CursorLoader(this, uri, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSaleBtnClick(int rowId) {
        Uri uriToUpdate = ContentUris.withAppendedId(uri, rowId);
        String[] projection = new String[]{BookEntry.COLUMN_Book_Quantity};
        Cursor cursor = getContentResolver().query(uriToUpdate, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_Book_Quantity));
            if (quantity > 0) {
                quantity -= 1;
            } else {
                Toast.makeText(this, getString(R.string.Out_of_stock), Toast.LENGTH_SHORT).show();
            }
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_Book_Quantity, quantity);
            getContentResolver().update(uriToUpdate, values, null, null);
        }
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        if (BookEntry.CONTENT_URI != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_All_message);
            builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        int rowDeletedId = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                        if (rowDeletedId > 0) {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_All_successful_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_unsuccessful_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
