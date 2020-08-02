package com.aervingames.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aervingames.inventoryapp.data.InventoryContract;
import com.aervingames.inventoryapp.data.InventoryCursorAdapter;
import com.aervingames.inventoryapp.data.InventoryDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    InventoryDbHelper mInventoryDbHelper;
    InventoryCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInventoryDbHelper = new InventoryDbHelper(this);
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView catalog = findViewById(R.id.inventory_list_view);
        mAdapter = new InventoryCursorAdapter(this, null);
        catalog.setAdapter(mAdapter);
        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                Uri itemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                intent.setData(itemUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0,null, (LoaderManager.LoaderCallbacks<Cursor>)this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.delete_all){
            deleteAll();
            return true;
        }

        if (item.getItemId()==R.id.insert_dummy_data){
            addDummyData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addDummyData(){
        ContentValues values = new ContentValues();

        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, "Monitor");
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, 4);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER, "Walmart");
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, 3);

        getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }

    public void deleteAll(){
        getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this,"All items deleted successfully", Toast.LENGTH_SHORT).show();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER
        };

        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mAdapter.swapCursor(null);
    }
}
