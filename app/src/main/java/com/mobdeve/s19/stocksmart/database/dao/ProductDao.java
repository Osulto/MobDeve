package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDao implements BaseDao<Product> {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public ProductDao(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public long insert(Product product) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BUSINESS_ID, SessionManager.getInstance(context).getUserId());
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(DatabaseHelper.COLUMN_QUANTITY, product.getQuantity());
        values.put(DatabaseHelper.COLUMN_REORDER_POINT, product.getReorderPoint());
        values.put(DatabaseHelper.COLUMN_COST_PRICE, product.getCostPrice());
        values.put(DatabaseHelper.COLUMN_SELLING_PRICE, product.getSellingPrice());
        values.put(DatabaseHelper.COLUMN_QR_CODE, product.getQrCode());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, product.getDescription());

        long id = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean update(Product product) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(DatabaseHelper.COLUMN_QUANTITY, product.getQuantity());
        values.put(DatabaseHelper.COLUMN_REORDER_POINT, product.getReorderPoint());
        values.put(DatabaseHelper.COLUMN_COST_PRICE, product.getCostPrice());
        values.put(DatabaseHelper.COLUMN_SELLING_PRICE, product.getSellingPrice());
        values.put(DatabaseHelper.COLUMN_QR_CODE, product.getQrCode());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, product.getDescription());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());

        int rowsAffected = db.update(DatabaseHelper.TABLE_PRODUCTS, values,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(product.getId()),
                        String.valueOf(SessionManager.getInstance(context).getUserId())});
        db.close();
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(long id) {
        db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_PRODUCTS,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getUserId())});
        db.close();
        return rowsAffected > 0;
    }

    @Override
    public Product get(long id) {
        db = dbHelper.getReadableDatabase();
        Product product = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            product = cursorToProduct(cursor);
            cursor.close();
        }
        db.close();
        return product;
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
            cursor.close();
        }
        db.close();
        return products;
    }

    public List<Product> getByCategory(long categoryId) {
        List<Product> products = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null,
                DatabaseHelper.COLUMN_CATEGORY_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(categoryId),
                        String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
            cursor.close();
        }
        db.close();
        return products;
    }

    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS +
                " WHERE " + DatabaseHelper.COLUMN_QUANTITY + " <= " +
                DatabaseHelper.COLUMN_REORDER_POINT +
                " AND " + DatabaseHelper.COLUMN_BUSINESS_ID + " = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(SessionManager.getInstance(context).getUserId())});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
            cursor.close();
        }
        db.close();
        return products;
    }

    public Product findByQRCode(String qrCode) {
        db = dbHelper.getReadableDatabase();
        Product product = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null,
                DatabaseHelper.COLUMN_QR_CODE + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{qrCode,
                        String.valueOf(SessionManager.getInstance(context).getUserId())},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            product = cursorToProduct(cursor);
            cursor.close();
        }
        db.close();
        return product;
    }

    private Product cursorToProduct(Cursor cursor) {
        return new Product(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REORDER_POINT)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COST_PRICE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SELLING_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QR_CODE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))
        );
    }
}