package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.Update;

import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class UpdateDao implements BaseDao<Update> {
    private DatabaseHelper dbHelper;
    private Context context;

    public UpdateDao(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long insert(Update update) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_BUSINESS_ID, SessionManager.getInstance(context).getBusinessId());
        values.put(DatabaseHelper.COLUMN_PRODUCT_ID, update.getProductId());
        values.put(DatabaseHelper.COLUMN_UPDATE_TYPE, update.getType().name());
        values.put(DatabaseHelper.COLUMN_UPDATE_DESCRIPTION, update.getDescription());
        values.put(DatabaseHelper.COLUMN_QUANTITY_CHANGED, update.getQuantityChanged());

        long id = db.insert(DatabaseHelper.TABLE_UPDATES, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean update(Update update) {
        // Updates are typically not modified after creation
        return false;
    }

    @Override
    public boolean delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DatabaseHelper.TABLE_UPDATES,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())});
        db.close();
        return result > 0;
    }

    @Override
    public Update get(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Update update = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_UPDATES, null,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            update = cursorToUpdate(cursor);
            cursor.close();
        }
        db.close();
        return update;
    }

    @Override
    public List<Update> getAll() {
        List<Update> updates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_UPDATES, null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                updates.add(cursorToUpdate(cursor));
            }
            cursor.close();
        }
        db.close();
        return updates;
    }

    public List<Update> getRecentUpdates(int limit) {
        List<Update> updates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_UPDATES, null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC",
                String.valueOf(limit));

        if (cursor != null) {
            while (cursor.moveToNext()) {
                updates.add(cursorToUpdate(cursor));
            }
            cursor.close();
        }
        db.close();
        return updates;
    }

    private Update cursorToUpdate(Cursor cursor) {
        return new Update(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                Update.UpdateType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATE_TYPE))),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY_CHANGED)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
        );
    }
}