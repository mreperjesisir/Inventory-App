package com.aervingames.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InventoryProvider extends ContentProvider {

    private static final String TAG = "InventoryProvider";
    InventoryDbHelper mInventoryDbHelper;

    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mInventoryDbHelper.getReadableDatabase();

        Cursor cursor = null;

        cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //TODO: Uri will need matching

        return verifyThenInsert(uri, values);
    }

    public Uri verifyThenInsert(Uri uri, ContentValues values){
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        if (name == null){
            throw new IllegalArgumentException("Items must have a name");
        }

        if (price == null){
            throw new IllegalArgumentException("Items must have a price");
        }

        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (newRowId==-1){
            Log.e(TAG, "Couldn't insert the value at" + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
