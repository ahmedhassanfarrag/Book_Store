package com.example.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoUtility {
    public static String mPhotoPath;

    private PhotoUtility() {
    }

    public static void setPic(String mPhotoPath, ImageView mEditPhotoV) {
        int targetW = mEditPhotoV.getWidth();
        int targetH = mEditPhotoV.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);
        mEditPhotoV.setImageBitmap(bitmap);
    }

    public static File ImageFile(Context mContext) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.d(EditorActivity.class.getName(), "create directory failed");
        }
        File externalFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        mPhotoPath = externalFile.getAbsolutePath();
        return externalFile;
    }

    public static String getPathFromUrl(Context mContext, Uri uri) {
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String realPath = cursor.getString(dataColumnIndex);
            return realPath;
        } else {
            return null;
        }
    }
}
