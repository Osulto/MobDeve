package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.Supplier;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao implements BaseDao<Supplier> {
    private DatabaseHelper dbHelper;
    private Context context;

    public SupplierDao(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long insert(Supplier supplier) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_BUSINESS_ID,
                SessionManager.getInstance(context).getBusinessId());
        values.put(DatabaseHelper.COLUMN_SUPPLIER_NAME, supplier.getName());
        values.put(DatabaseHelper.COLUMN_SUPPLIER_CONTACT, supplier.getContact());

        long id = db.insert(DatabaseHelper.TABLE_SUPPLIERS, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean update(Supplier supplier) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_SUPPLIER_NAME, supplier.getName());
        values.put(DatabaseHelper.COLUMN_SUPPLIER_CONTACT, supplier.getContact());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date()));

        int rowsAffected = db.update(DatabaseHelper.TABLE_SUPPLIERS, values,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(supplier.getId()),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())});
        db.close();
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DatabaseHelper.TABLE_SUPPLIERS,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())});
        db.close();
        return result > 0;
    }

    @Override
    public Supplier get(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Supplier supplier = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_SUPPLIERS, null,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            supplier = cursorToSupplier(cursor);
            cursor.close();
        }
        db.close();
        return supplier;
    }

    @Override
    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_SUPPLIERS, null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null,
                DatabaseHelper.COLUMN_SUPPLIER_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                suppliers.add(cursorToSupplier(cursor));
            }
            cursor.close();
        }
        db.close();
        return suppliers;
    }

    public Supplier getByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Supplier supplier = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_SUPPLIERS, null,
                DatabaseHelper.COLUMN_SUPPLIER_NAME + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{name,
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            supplier = cursorToSupplier(cursor);
            cursor.close();
        }
        db.close();
        return supplier;
    }

    private Supplier cursorToSupplier(Cursor cursor) {
        return new Supplier(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUSINESS_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_CONTACT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))
        );
    }
}