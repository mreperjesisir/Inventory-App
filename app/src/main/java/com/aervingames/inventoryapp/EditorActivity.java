package com.aervingames.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aervingames.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private ImageButton mPricePlus;
    private ImageButton mPriceMinus;
    private ImageButton mQuantityPlus;
    private ImageButton mQuantityMinus;
    private Uri mEditViewUri = null;
    private static final int LOADER_ID = 0;
    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameField = findViewById(R.id.name_field);
        mPriceField = findViewById(R.id.price_field);
        mQuantityField = findViewById(R.id.quantity_field);
        mSupplierField = findViewById(R.id.supplier_field);
        mEditViewUri = getIntent().getData();

        //TODO: Fix issue with buttons: if I bring the counter to 1
        // and disable the minus button,
        // then I edit the text to a higher number,
        // the minus button is still disabled.
        //
        // Also, when I enter 1 to the EditTextField manually,
        // the minus button won't be disabled yet

        mPricePlus = findViewById(R.id.item_price_plus);
        mPricePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mPriceField.getText())){
                    int price = Integer.parseInt(mPriceField.getText().toString());
                    mPriceField.setText(String.valueOf(price+1));
                    if (!mPriceMinus.isClickable()){
                        mPriceMinus.setClickable(true);
                    }
                } else {
                    mPriceField.setText("1");
                    mPriceMinus.setClickable(false);
                }
            }
        });

        mQuantityPlus = findViewById(R.id.quantity_plus);
        mQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityField.getText())){
                    int quantity = Integer.parseInt(mQuantityField.getText().toString());
                    mQuantityField.setText(String.valueOf(quantity+1));
                    if (!mQuantityMinus.isClickable()){
                        mQuantityMinus.setClickable(true);
                    }
                } else {
                    mQuantityField.setText("1");
                    mQuantityMinus.setClickable(false);
                }
            }
        });

        mPriceMinus = findViewById(R.id.item_price_minus);
        mPriceMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mPriceField.getText()) &&
                Integer.parseInt(mPriceField.getText().toString()) > 1){
                    int price = Integer.parseInt(mPriceField.getText().toString());
                    mPriceField.setText(String.valueOf(price-1));
                } else {
                    mPriceField.setText("1");
                    mPriceMinus.setClickable(false);
                }
            }
        });

        mQuantityMinus = findViewById(R.id.quantity_minus);
        mQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityField.getText()) &&
                Integer.parseInt(mQuantityField.getText().toString()) > 1){
                    int quantity = Integer.parseInt(mQuantityField.getText().toString());
                    mQuantityField.setText(String.valueOf(quantity-1));
                } else {
                    mQuantityField.setText("1");
                    mQuantityMinus.setClickable(false);
                }
            }
        });

        mNameField.setOnTouchListener(mTouchListener);
        mPriceField.setOnTouchListener(mTouchListener);
        mQuantityField.setOnTouchListener(mTouchListener);
        mSupplierField.setOnTouchListener(mTouchListener);

        if (mEditViewUri == null) {
            this.setTitle("Add new item");
            invalidateOptionsMenu();
        } else {
            this.setTitle("Edit item details");
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
        //TODO: Review the design and get rid of the Add new item button

    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (mEditViewUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.accept_button:
                if (mEditViewUri == null) {
                    boolean success = verifyInput();
                    if (success) {
                        String returnedUri = insert();
                        Toast.makeText(this, "Item added successfully at " + returnedUri, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Please fill in Name, Quantity, and Price fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (mItemHasChanged) {
                        boolean success = verifyInput();
                        if (success) {
                            int updatedRows = update();
                            Toast.makeText(this, "Number of items updated successfully: " + updatedRows, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Please fill in Name, Quantity, and Price fields", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        finish();
                    }
                }
                return true;
            case R.id.delete_item:
                if (mEditViewUri != null){
                    showDeleteConfirmationDialog();
                }
                return true;
            case android.R.id.home:
                if (!mItemHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    });
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**

     *
     * @return Uri of inserted row as a String
     */

    private String insert() {

        //TODO: Create separate helper method to create the ContentValues object

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, mNameField.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, Integer.parseInt(mPriceField.getText().toString()));
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityField.getText().toString()));
        if (TextUtils.isEmpty(mSupplierField.getText().toString())){
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, "Unknown supplier");
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, mSupplierField.getText().toString());
        }

        return getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values).toString();
    }

    private int update() {

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, mNameField.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, Integer.parseInt(mPriceField.getText().toString()));
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityField.getText().toString()));
        if (TextUtils.isEmpty(mSupplierField.getText().toString())){
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, "Unknown supplier");
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, mSupplierField.getText().toString());
        }

        return getContentResolver().update(mEditViewUri, values, null, null);
    }

    /**
     * Checks EditText fields for invalid input, used before insert and update.
     *
     * @return true if all fields are valid.
     */

    private boolean verifyInput() {
        boolean success = false;

        String name = mNameField.getText().toString().trim();
        String quantity = mQuantityField.getText().toString().trim();
        String price = mPriceField.getText().toString().trim();

        if ((!name.isEmpty() && name != null) &&
                (!quantity.isEmpty() && quantity != null) &&
                (!price.isEmpty() && price != null)) {
            success = true;
        }
        return success;
    }

    private void deleteItem(){
        if (mEditViewUri != null){
            getContentResolver().delete(mEditViewUri, null, null);
            Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to discard unsaved changes?");
        builder.setPositiveButton("Discard", discardButtonListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY};

        CursorLoader loader = new CursorLoader(this, mEditViewUri, projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {

            int nameColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER);

            mNameField.setText(data.getString(nameColumnIndex));
            mPriceField.setText(data.getString(priceColumnIndex));
            mQuantityField.setText(data.getString(quantityColumnIndex));
            mSupplierField.setText(data.getString(supplierColumnIndex));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader.reset();
    }
}