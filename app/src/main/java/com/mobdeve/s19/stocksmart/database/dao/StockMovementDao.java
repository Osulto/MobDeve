package com.mobdeve.s19.stocksmart.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.models.StockMovement;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDao implements BaseDao<StockMovement> {
    private DatabaseHelper dbHelper;
    private Context context;

    public StockMovementDao(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long insert(StockMovement movement) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_BUSINESS_ID, SessionManager.getInstance(context).getBusinessId());
        values.put(DatabaseHelper.COLUMN_PRODUCT_ID, movement.getProductId());
        values.put(DatabaseHelper.COLUMN_MOVEMENT_TYPE, movement.getMovementType());
        values.put(DatabaseHelper.COLUMN_MOVEMENT_QUANTITY, movement.getQuantity());
        values.put(DatabaseHelper.COLUMN_SUPPLIER_ID, movement.getSupplierId());
        values.put(DatabaseHelper.COLUMN_SUPPLIER_PRICE, movement.getSupplierPrice());

        long id = db.insert(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean update(StockMovement movement) {
        // Stock movements typically shouldn't be updated after creation
        return false;
    }

    @Override
    public boolean delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DatabaseHelper.TABLE_STOCK_MOVEMENTS,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())});
        db.close();
        return result > 0;
    }

    @Override
    public StockMovement get(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StockMovement movement = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null,
                DatabaseHelper.COLUMN_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(id),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())},
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null,
                DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_STOCK_MOVEMENTS, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_BUSINESS_ID + " = ?",
                new String[]{String.valueOf(productId),
                        String.valueOf(SessionManager.getInstance(context).getBusinessId())},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                movements.add(cursorToStockMovement(cursor));
            }
            cursor.close();
        }
        db.close();
        return movements;
    }

    public double calculateTotalStockValue() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double totalValue = 0;

        // This query calculates stock value based on the most recent supplier prices
        String query = "SELECT p.id, p.quantity, " +
                "(SELECT sm.supplier_price FROM " + DatabaseHelper.TABLE_STOCK_MOVEMENTS + " sm " +
                "WHERE sm.product_id = p.id AND sm.business_id = p.business_id " +
                "ORDER BY sm.created_at DESC LIMIT 1) as latest_price " +
                "FROM " + DatabaseHelper.TABLE_PRODUCTS + " p " +
                "WHERE p.business_id = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(SessionManager.getInstance(context).getBusinessId())});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("latest_price"));
                totalValue += quantity * price;
            }
            cursor.close();
        }
        db.close();
        return totalValue;
    }

    private StockMovement cursorToStockMovement(Cursor cursor) {
        return new StockMovement(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUSINESS_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOVEMENT_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOVEMENT_QUANTITY)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_ID)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_PRICE)),
                null, // Notes removed
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
        );
    }
}