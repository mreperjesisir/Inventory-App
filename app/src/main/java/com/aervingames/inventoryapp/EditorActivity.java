package com.aervingames.inventoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aervingames.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private Button mAddNewItemButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameField = findViewById(R.id.name_field);
        mPriceField = findViewById(R.id.price_field);
        mQuantityField = findViewById(R.id.quantity_field);
        mSupplierField = findViewById(R.id.supplier_field);
        mAddNewItemButton = findViewById(R.id.add_item_button);
        //TODO: Review the design and get rid of the Add new item button

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.accept_button){
            verifyAndInsert();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void verifyAndInsert() {

        //TODO: Verify the data to make sure it's valid and not null

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, mNameField.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, Integer.parseInt(mPriceField.getText().toString()));
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityField.getText().toString()));
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, mSupplierField.getText().toString());

        getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

    }
}