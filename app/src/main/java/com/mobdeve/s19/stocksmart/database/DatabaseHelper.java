package com.mobdeve.s19.stocksmart.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stocksmart.db";
    private static final int DATABASE_VERSION = 2;  // Increment version for new schema

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_STOCK_MOVEMENTS = "stock_movements";
    public static final String TABLE_IMAGES = "images";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_BUSINESS_ID = "business_id";  // New column

    // Users Table columns
    public static final String COLUMN_BUSINESS_NAME = "business_name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Categories Table columns
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_ICON = "icon";

    // Products Table columns
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_REORDER_POINT = "reorder_point";
    public static final String COLUMN_COST_PRICE = "cost_price";
    public static final String COLUMN_SELLING_PRICE = "selling_price";
    public static final String COLUMN_QR_CODE = "qr_code";
    public static final String COLUMN_DESCRIPTION = "description";

    // Stock Movements Table columns
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_MOVEMENT_TYPE = "movement_type";
    public static final String COLUMN_MOVEMENT_QUANTITY = "quantity";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_SUPPLIER = "supplier";

    // Images Table columns
    public static final String COLUMN_REFERENCE_ID = "reference_id";
    public static final String COLUMN_REFERENCE_TYPE = "reference_type";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Create table statements
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_NAME + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_ICON + " TEXT, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_ID + " INTEGER, " +
                    COLUMN_QUANTITY + " INTEGER DEFAULT 0, " +
                    COLUMN_REORDER_POINT + " INTEGER DEFAULT 0, " +
                    COLUMN_COST_PRICE + " REAL, " +
                    COLUMN_SELLING_PRICE + " REAL, " +
                    COLUMN_QR_CODE + " TEXT UNIQUE, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_STOCK_MOVEMENTS =
            "CREATE TABLE " + TABLE_STOCK_MOVEMENTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_MOVEMENT_TYPE + " TEXT NOT NULL, " +
                    COLUMN_MOVEMENT_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_SUPPLIER + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " +
                    TABLE_PRODUCTS + "(" + COLUMN_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_IMAGES =
            "CREATE TABLE " + TABLE_IMAGES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_REFERENCE_ID + " INTEGER NOT NULL, " +
                    COLUMN_REFERENCE_TYPE + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_STOCK_MOVEMENTS);
        db.execSQL(CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }
}