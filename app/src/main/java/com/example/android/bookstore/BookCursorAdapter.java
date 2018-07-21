package com.example.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public Context context;
    private OnSaleBtnClickListener saleButtonClick;
    private int id;

    public interface OnSaleBtnClickListener {
        public void onSaleBtnClick(int rowId);
    }

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        try {
            saleButtonClick = (OnSaleBtnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement OnSaleBtnClickListener");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int rowId = cursor.getInt(idColumnIndex);
        Button saleBtn = (Button) view.findViewById(R.id.buy_button);
        saleBtn.setTag(rowId);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.book_name);
        TextView priceText = (TextView) view.findViewById(R.id.book_price);
        TextView quantityText = (TextView) view.findViewById(R.id.book_quantity);
        final Button buy = (Button) view.findViewById(R.id.buy_button);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_Book_Name);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_Book_Price);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_Book_Quantity);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int cursorQuantity = cursor.getInt(quantityColumnIndex);

        nameText.setText(name);
        priceText.setText(String.valueOf(price));
        quantityText.setText(String.valueOf(cursorQuantity));

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = (Integer) buy.getTag();
                saleButtonClick.onSaleBtnClick(id);
            }
        });
    }
}