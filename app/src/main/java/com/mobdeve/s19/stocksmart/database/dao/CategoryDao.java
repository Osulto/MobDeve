package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao implements BaseDao<Category> {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public CategoryDao(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long insert(Category category) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BUSINESS_ID,
                SessionManager.getInstance(context).getBusinessId());  // Use business ID instead of user ID
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.getIconPath());

        long id = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean update(Category category) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.getIconPath());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date()));

        int rowsAffected = db.update(DatabaseHelper.TABLE_CATEGORIES, values,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(category.getId()),
                        String.valueOf(SessionManager.getInstance(context).getUserId())});
        db.close();
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(long id) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // Delete products associated with this category
            db.delete(DatabaseHelper.TABLE_PRODUCTS,
                    DatabaseHelper.COLUMN_CATEGORY_ID + " = ? AND " +
                            DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                    new String[]{String.valueOf(id),
                            String.valueOf(SessionManager.getInstance(context).getUserId())});

            // Delete the category
            int rowsAffected = db.delete(DatabaseHelper.TABLE_CATEGORIES,
                    DatabaseHelper.COLUMN_ID + " = ? AND " +
                            DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                    new String[]{String.valueOf(id),
                            String.valueOf(SessionManager.getInstance(context).getUserId())});

            if (rowsAffected > 0) {
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }

    @Override
    public Category get(long id) {
        db = dbHelper.getReadableDatabase();
        Category category = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            category = cursorToCategory(cursor);
            cursor.close();
        }
        db.close();
        return category;
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES,
                null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                categories.add(cursorToCategory(cursor));
            }
            cursor.close();
        }
        db.close();
        return categories;
    }

    public Category getByName(String name) {
        db = dbHelper.getReadableDatabase();
        Category category = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null,
                DatabaseHelper.COLUMN_CATEGORY_NAME + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{name, String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            category = cursorToCategory(cursor);
            cursor.close();
        }
        db.close();
        return category;
    }

    private Category cursorToCategory(Cursor cursor) {
        return new Category(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))
        );
    }
}