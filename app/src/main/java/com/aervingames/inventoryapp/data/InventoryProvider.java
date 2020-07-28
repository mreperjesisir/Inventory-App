package com.aervingames.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InventoryProvider extends ContentProvider {

    private static final String TAG = "InventoryProvider";
    private InventoryDbHelper mInventoryDbHelper;

    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.PATH_INVENTORY, ITEMS);
        sUriMatcher.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.PATH_INVENTORY + "/#", ITEM_ID);
    }


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
        final int match = sUriMatcher.match(uri);

        switch (match){
            case ITEMS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default: throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), InventoryContract.InventoryEntry.CONTENT_URI);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalArgumentException("Unknown uri " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

       final int match = sUriMatcher.match(uri);

        switch (match){
            case ITEMS:
                return verifyThenInsert(uri, values);
            default: throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
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

        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case ITEMS:
                getContext().getContentResolver().notifyChange(InventoryContract.InventoryEntry.CONTENT_URI, null);
                return db.delete(InventoryContract.InventoryEntry.TABLE_NAME, null, null);

            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(InventoryContract.InventoryEntry.CONTENT_URI, null);
                return db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);

        switch(match){
            case ITEMS:
                return verifyAndUpdate(uri, values, selection, selectionArgs);
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return verifyAndUpdate(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    private int verifyAndUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size()==0){
            return 0;
        }

        if (values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME)==null){
            throw new IllegalArgumentException("Item needs a name");
        }

        if (values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER)==null){
            throw new IllegalArgumentException("Supplier must be specified");
        }

        if (values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE)==null){
            throw new IllegalArgumentException("Item needs a price");
        }

        if (values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY)==null){
            throw new IllegalArgumentException("Must set a quantity");
        }

        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(InventoryContract.InventoryEntry.CONTENT_URI, null);
        return db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }

}
