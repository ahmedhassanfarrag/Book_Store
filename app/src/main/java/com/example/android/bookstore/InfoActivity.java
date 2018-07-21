package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ImageView mInfoPhotoV;
    TextView mInfoNameV, mInfoPriceV, mInfoQuantityV, mInfoSupplierV, mInfoSupplierPhoneV;
    ImageButton mcallBtn;
    private static final int BookLOADER_ID = 1;
    Uri uriForInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        uriForInfo = intent.getData();
        mInfoPhotoV = (ImageView) findViewById(R.id.book_detail_photo);
        mInfoNameV = (TextView) findViewById(R.id.book_detail_name);
        mInfoPriceV = (TextView) findViewById(R.id.book_detail_price);
        mInfoQuantityV = (TextView) findViewById(R.id.book_detail_quantity);
        mInfoSupplierV = (TextView) findViewById(R.id.book_detail_supplier);
        mInfoSupplierPhoneV = (TextView) findViewById(R.id.book_detail_supplier_phone);
        mcallBtn = (ImageButton) findViewById(R.id.book_detail_phone_btn);
        mcallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel", mInfoSupplierPhoneV.getText().toString(), null));
                startActivity(intent);
            }
        });
        if (uriForInfo != null) {
            getLoaderManager().initLoader(BookLOADER_ID, null, this);
        } else {
            throw new IllegalArgumentException("invalid url");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_Book_Name,
                BookEntry.COLUMN_Book_Price,
                BookEntry.COLUMN_Book_Quantity,
                BookEntry.COLUMN_Book_SupplierName,
                BookEntry.COLUMN_Book_SupplierPhone,
                BookEntry.COLUMN_Book_PHOTO};
        switch (id) {
            case BookLOADER_ID:
                return new CursorLoader(this, uriForInfo, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Name);
                int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Price);
                int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Quantity);
                int supplierColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierName);
                int supplierPhoneColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierPhone);
                int photoColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_PHOTO);

                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);
                String photoPath = data.getString(photoColumnIndex);

                if (photoPath == null || photoPath.isEmpty()) {
                    //mDetailPhotoV.setImageBitmap(null);
                    mInfoPhotoV.setBackgroundResource(R.drawable.add_book);
                } else {
                    PhotoUtility.setPic(photoPath, mInfoPhotoV);
                }

                mInfoNameV.setText(name);
                mInfoPriceV.setText(String.valueOf(price));
                mInfoQuantityV.setText(String.valueOf(quantity));
                mInfoSupplierV.setText(supplier);
                mInfoSupplierPhoneV.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInfoPhotoV.setImageBitmap(null);
        mInfoNameV.setText(null);
        mInfoPriceV.setText(null);
        mInfoQuantityV.setText(null);
        mInfoSupplierV.setText(null);
        mInfoSupplierPhoneV.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_action:
                Intent intent = new Intent(InfoActivity.this, EditorActivity.class);
                intent.setData(uriForInfo);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
