package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

import java.io.File;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnTouchListener {
    /**
     * EditText field to enter the Book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the Book's Price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the Books's Quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the Book's SupplierName
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the Book's SupplierPhoneNo.
     */
    private EditText mSupplierPhoneEditText;
    public ImageView mEditPhotoV;
    private TextView mNameRequiredV, mPriceRequiredV, mQuantityRequiredV, mSupplierRequiredV, mPhoneRequiredV, mAddPhotoV;
    private Button mEditMinusBtn, mEditPlusBtn;
    private String mEditName, mEditSupplier;
    private int mEditQuantity;
    private Double mEditPrice;
    private static final int LOADER_ID = 1;
    private Uri uriToEdit;
    private Uri uri = BookEntry.CONTENT_URI;
    private boolean mIsTouched = false;
    private static final int PHOTO_FROM_ACTION_CODE = 2;
    private Uri selectedImageUri;
    private String mPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Find all relevant views that we will need to read user input from.
        mNameEditText = (EditText) findViewById(R.id.book_edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.book_edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.book_edit_supplier);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.book_edit_supplier_phone);
        mAddPhotoV = (TextView) findViewById(R.id.add_photo_tv);
        mEditPhotoV = (ImageView) findViewById(R.id.book_edit_photo);
        //set touch listener for views to check if any change has been made
        mNameEditText.setOnTouchListener(this);
        mPriceEditText.setOnTouchListener(this);
        mQuantityEditText.setOnTouchListener(this);
        mSupplierNameEditText.setOnTouchListener(this);
        mSupplierPhoneEditText.setOnTouchListener(this);
        mEditPhotoV.setOnTouchListener(this);

        //image btn click listener to choose or capture photo
        mEditPhotoV.setOnClickListener(imageChooseClickListener);

        //init views to remind user to fill in required info
        mNameRequiredV = (TextView) findViewById(R.id.name_required_label);
        mPriceRequiredV = (TextView) findViewById(R.id.price_required_label);
        mQuantityRequiredV = (TextView) findViewById(R.id.quantity_required_label);
        mSupplierRequiredV = (TextView) findViewById(R.id.supplier_required_label);
        mPhoneRequiredV = (TextView) findViewById(R.id.phone_required_label);
        //btn init and touch listener
        mEditMinusBtn = (Button) findViewById(R.id.book_edit_minus_btn);
        mEditPlusBtn = (Button) findViewById(R.id.book_edit_plus_btn);
        mEditMinusBtn.setOnTouchListener(this);
        mEditPlusBtn.setOnTouchListener(this);


        Intent intent = getIntent();
        uriToEdit = intent.getData();
        if (uriToEdit == null) {
            setTitle(getString(R.string.add_title));
            mAddPhotoV.setVisibility(View.VISIBLE);

        } else {
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        mEditMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                if (mEditQuantity > 0) {
                    mEditQuantity -= 1;
                    mQuantityEditText.setText(Integer.toString(mEditQuantity));
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.quantity_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEditPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                mEditQuantity += 1;
                mQuantityEditText.setText(Integer.toString(mEditQuantity));
            }
        });

    }

    //image btn click listener to choose or capture photo
    private View.OnClickListener imageChooseClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try{
                photoFile = PhotoUtility.ImageFile(getBaseContext());
            }catch (IOException e){
                e.printStackTrace();
            }
            if (photoFile!=null){
                selectedImageUri = FileProvider.getUriForFile(getBaseContext(),"com.example.android.photoProvider",photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,selectedImageUri);
            }

            String title = getString(R.string.image_title);
            Intent chooserIntent = Intent.createChooser(pickIntent, title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
            startActivityForResult(chooserIntent, PHOTO_FROM_ACTION_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PHOTO_FROM_ACTION_CODE) {
            Bitmap bitmap = null;
            if (data == null) {
                //from camera
                mPhotoPath = PhotoUtility.mPhotoPath;
                Log.i(EditorActivity.class.getName(),"now the photo path is: "+mPhotoPath);
                PhotoUtility.setPic(mPhotoPath,mEditPhotoV);
            } else {
                //from photo or gallery
                selectedImageUri = data.getData();
                Log.i(EditorActivity.class.getName(), "selected image uri is:" + selectedImageUri + "iscamera is false");
                mPhotoPath = PhotoUtility.getPathFromUrl(getBaseContext(),selectedImageUri);
                Log.i(EditorActivity.class.getName(),"now the photo path is: "+mPhotoPath);
                PhotoUtility.setPic(mPhotoPath,mEditPhotoV);
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mIsTouched = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mIsTouched) {
            discardConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void discardConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_edit_message);
        builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    finish();
                }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deleteBook(uriToEdit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveBook() {

        mEditName = mNameEditText.getText().toString();
        String price = mPriceEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();
        mEditSupplier = mSupplierNameEditText.getText().toString();
        String phoneNumber = mSupplierPhoneEditText.getText().toString();

        boolean isValuesEmpty = isContentValuesEmpty(mEditName, price, quantity, mEditSupplier, phoneNumber);
        if (isValuesEmpty) {
            Toast.makeText(this, getString(R.string.data_empty_message), Toast.LENGTH_SHORT).show();
        } else {
            mEditName = mEditName.trim();
            mEditPrice = Double.parseDouble(price.trim());
            mEditQuantity = Integer.parseInt(quantity.trim());
            mEditSupplier = mEditSupplier.trim();

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_Book_Name, mEditName);
            values.put(BookEntry.COLUMN_Book_Price, mEditPrice);
            values.put(BookEntry.COLUMN_Book_Quantity, mEditQuantity);
            values.put(BookEntry.COLUMN_Book_SupplierName, mEditSupplier);
            values.put(BookEntry.COLUMN_Book_SupplierPhone, phoneNumber);
            if (mPhotoPath != null) {
                values.put(BookEntry.TABLE_COLUMN_PHOTO, mPhotoPath);
            }
            if (uriToEdit == null) {
                getContentResolver().insert(uri, values);
                finish();
            } else {
                getContentResolver().update(uriToEdit, values, null, null);
                finish();
                Intent intent = new Intent();
                intent.setData(uriToEdit);
                NavUtils.navigateUpTo(this, intent);
            }
        }
    }

    private void deleteBook(final Uri uriToEdit) {
        if (uriToEdit != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_message);
            builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        int rowDeletedId = getContentResolver().delete(uriToEdit, null, null);
                        if (rowDeletedId > 0) {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_successful_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_unsuccessful_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
                    NavUtils.navigateUpTo(EditorActivity.this, intent);
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


    //helper method: check if passed-in content values are empty
    private boolean isContentValuesEmpty(String name, String price, String quantity, String supplier, String phoneNumber) {
        boolean isContentValuesEmpty = false;
        if (TextUtils.isEmpty(name)) {
            //name can not be empty
            isContentValuesEmpty = true;
            mNameRequiredV.setText(getString(R.string.name_required));
            mNameRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(price)) {
            //price can't be null
            isContentValuesEmpty = true;
            mPriceRequiredV.setText(getString(R.string.price_required));
            mPriceRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(quantity)) {
            //quantity can't be null
            isContentValuesEmpty = true;
            mQuantityRequiredV.setText(getString(R.string.quantity_required));
            mQuantityRequiredV.setVisibility(View.VISIBLE);

        }
        if (TextUtils.isEmpty(supplier)) {
            //supplier can't be null
            isContentValuesEmpty = true;
            mSupplierRequiredV.setText(getString(R.string.supplier_required));
            mSupplierRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            //phone can't be null
            isContentValuesEmpty = true;
            mPhoneRequiredV.setText(getString(R.string.phone_required));
            mPhoneRequiredV.setVisibility(View.VISIBLE);
        }
        return isContentValuesEmpty;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_Book_Name,
                BookEntry.COLUMN_Book_Price,
                BookEntry.COLUMN_Book_Quantity,
                BookEntry.COLUMN_Book_SupplierName,
                BookEntry.COLUMN_Book_SupplierPhone,
                BookEntry.TABLE_COLUMN_PHOTO};
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, uriToEdit, projection, null, null, null);
            default:
                return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                int photoColumnIndex = data.getColumnIndex(BookEntry.TABLE_COLUMN_PHOTO);
                int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Name);
                int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Price);
                int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_Quantity);
                int supplierColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierName);
                int supplierPhoneColumnIndex = data.getColumnIndex(BookEntry.COLUMN_Book_SupplierPhone);

                String photoPath = data.getString(photoColumnIndex);
                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);

                if (photoPath == null || photoPath.isEmpty()) {
                    mAddPhotoV.setVisibility(View.VISIBLE);
                    mEditPhotoV.setBackgroundResource(R.drawable.add_book);
                } else {
                    PhotoUtility.setPic(photoPath, mEditPhotoV);
                    mAddPhotoV.setVisibility(View.VISIBLE);
                }
                mNameEditText.setText(name);
                mPriceEditText.setText(String.valueOf(price));
                mQuantityEditText.setText(String.valueOf(quantity));
                mSupplierNameEditText.setText(supplier);
                mSupplierPhoneEditText.setText(supplierPhone);
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditPhotoV.setImageBitmap(null);
        mNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQuantityEditText.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierPhoneEditText.setText(null);
    }
}
