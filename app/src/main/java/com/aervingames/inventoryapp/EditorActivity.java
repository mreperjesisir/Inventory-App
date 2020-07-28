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
import android.widget.Toast;

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
            if (verifyAndInsert()==1){
                Toast.makeText(this,"Item added successfully",Toast.LENGTH_SHORT);
                finish();
            } else {
                Toast.makeText(this, "Please fill in Name, Quantity, and Price fields", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Verifies data and notifies user if required fields are empty.
     * @return 1 if all data was valid and the item was inserted to the
     * database. Returns 0 if a required field is empty.
     */

    private int verifyAndInsert() {

        int successOrFail = 0;

        String name = mNameField.getText().toString().trim();
        String quantity = mQuantityField.getText().toString().trim();
        String price = mPriceField.getText().toString().trim();

        if ((!name.isEmpty() && name!=null) &&
                (!quantity.isEmpty() && quantity!=null) &&
                (!price.isEmpty() && price != null)){

            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, mNameField.getText().toString());
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, Integer.parseInt(mPriceField.getText().toString()));
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityField.getText().toString()));
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, mSupplierField.getText().toString());

            successOrFail = 1;
            getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
        }
        return successOrFail;
    }
}