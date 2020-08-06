package com.aervingames.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aervingames.inventoryapp.R;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name = view.findViewById(R.id.title_text);
        TextView supplier = view.findViewById(R.id.supplier_text);
        TextView piecesInStock = view.findViewById(R.id.number_in_stock);

        int nameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int supplierIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER);
        int inStockIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);

        String itemNameText = cursor.getString(nameIndex);
        String supplierText = cursor.getString(supplierIndex);
        String inStock = String.valueOf(cursor.getInt(inStockIndex)) + "\nin stock";

        name.setText(itemNameText);
        supplier.setText(supplierText);
        piecesInStock.setText(inStock);
    }
}
