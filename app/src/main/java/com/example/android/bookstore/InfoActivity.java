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
    ImageView mDetailPhotoV;
    TextView mDetailNameV,mDetailPriceV,mDetailQuantityV,mDetailSupplierV,mDetailSupplierPhoneV;
    ImageButton mcallBtn;
    private static final int BookLOADER_ID = 1;
    Uri uriForDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        uriForDetail = intent.getData();
        mDetailPhotoV = (ImageView) findViewById(R.id.book_detail_photo);
        mDetailNameV = (TextView) findViewById(R.id.book_detail_name);
        mDetailPriceV = (TextView) findViewById(R.id.book_detail_price);
        mDetailQuantityV = (TextView) findViewById(R.id.book_detail_quantity);
        mDetailSupplierV = (TextView) findViewById(R.id.book_detail_supplier);
        mDetailSupplierPhoneV = (TextView) findViewById(R.id.book_detail_supplier_phone);
        mcallBtn = (ImageButton) findViewById(R.id.book_detail_phone_btn);
        mcallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel",mDetailSupplierPhoneV.getText().toString(),null));
                startActivity(intent);
            }
        });
        if (uriForDetail != null){
            getLoaderManager().initLoader(BookLOADER_ID,null,this);
        }else{
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
                BookEntry.TABLE_COLUMN_PHOTO};
        switch (id){
            case BookLOADER_ID:
                return new CursorLoader(this,uriForDetail,projection,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null){
            if (data.moveToFirst()){
                int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Name);
                int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Price);
                int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Quantity);
                int supplierColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierName);
                int supplierPhoneColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierPhone);
                int photoColumnIndex = data.getColumnIndex(BookEntry.TABLE_COLUMN_PHOTO);

                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);
                String photoPath = data.getString(photoColumnIndex);

                if (photoPath == null || photoPath.isEmpty()){
                    //mDetailPhotoV.setImageBitmap(null);
                    mDetailPhotoV.setBackgroundResource(R.drawable.add_book);
                }else{
                    PhotoUtility.setPic(photoPath,mDetailPhotoV);
                }

                mDetailNameV.setText(name);
                mDetailPriceV.setText(String.valueOf(price));
                mDetailQuantityV.setText(String.valueOf(quantity));
                mDetailSupplierV.setText(supplier);
                mDetailSupplierPhoneV.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailPhotoV.setImageBitmap(null);
        mDetailNameV.setText(null);
        mDetailPriceV.setText(null);
        mDetailQuantityV.setText(null);
        mDetailSupplierV.setText(null);
        mDetailSupplierPhoneV.setText(null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_action:
                Intent intent = new Intent(InfoActivity.this,EditorActivity.class);
                intent.setData(uriForDetail);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
