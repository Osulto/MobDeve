package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.StockMovement;
import com.mobdeve.s19.stocksmart.database.models.Product;
import java.util.ArrayList;
import java.util.List;


public class StockMovementDao implements BaseDao<StockMovement> {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ProductDao productDao;

    public StockMovementDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        productDao = new ProductDao(context);
    }

    @Override
    public long insert(StockMovement movement) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_ID, movement.getProductId());
        values.put(DatabaseHelper.COLUMN_MOVEMENT_TYPE, movement.getMovementType());
        values.put(DatabaseHelper.COLUMN_MOVEMENT_QUANTITY, movement.getQuantity());
        values.put(DatabaseHelper.COLUMN_SUPPLIER, movement.getSupplier());
        values.put(DatabaseHelper.COLUMN_NOTES, movement.getNotes());

        long id = db.insert(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null, values);

        // Update product quantity
        if (id != -1) {
            updateProductQuantity(movement);
        }

        db.close();
        return id;
    }

    private void updateProductQuantity(StockMovement movement) {
        // Get current product
        db = dbHelper.getWritableDatabase();
        Product product = productDao.get(movement.getProductId());

        if (product != null) {
            int newQuantity = product.getQuantity();

            // Adjust quantity based on movement type
            if (movement.getMovementType().equals("IN")) {
                newQuantity += movement.getQuantity();
            } else if (movement.getMovementType().equals("OUT")) {
                newQuantity -= movement.getQuantity();
            }

            // Update product quantity
            product.setQuantity(newQuantity);
            productDao.update(product);
        }
    }

    @Override
    public boolean update(StockMovement movement) {
        // Stock movements should not be updated after creation
        return false;
    }

    @Override
    public boolean delete(long id) {
        // Stock movements should not be deleted
        return false;
    }

    @Override
    public StockMovement get(long id) {
        db = dbHelper.getReadableDatabase();
        StockMovement movement = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            movement = cursorToStockMovement(cursor);
            cursor.close();
        }
        db.close();
        return movement;
    }

    @Override
    public List<StockMovement> getAll() {
        List<StockMovement> movements = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS,
                null, null, null,
                null, null, DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                movements.add(cursorToStockMovement(cursor));
            }
            cursor.close();
        }
        db.close();
        return movements;
    }

    public List<StockMovement> getByProduct(long productId) {
        List<StockMovement> movements = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)},
                null, null, DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                movements.add(cursorToStockMovement(cursor));
            }
            cursor.close();
        }
        db.close();
        return movements;
    }

    private StockMovement cursorToStockMovement(Cursor cursor) {
        return new StockMovement(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOVEMENT_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOVEMENT_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTES)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
        );
    }
}