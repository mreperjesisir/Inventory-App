package com.aervingames.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            InventoryContract.InventoryEntry.TABLE_NAME +
            " (" + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryContract.InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
            InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER + " TEXT, " +
            InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, " +
            InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0" + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME;

    //TODO: Update the database to version 2 and add a Column for the picture


    public InventoryDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

