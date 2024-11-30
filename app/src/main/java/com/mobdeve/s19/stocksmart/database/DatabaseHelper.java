package com.mobdeve.s19.stocksmart.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stocksmart.db";
    private static final int DATABASE_VERSION = 5;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_STOCK_MOVEMENTS = "stock_movements";
    public static final String TABLE_IMAGES = "images";
    public static final String TABLE_UPDATES = "updates";
    public static final String TABLE_SUPPLIERS = "suppliers";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_BUSINESS_ID = "business_id";

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
    public static final String COLUMN_SELLING_PRICE = "selling_price";
    public static final String COLUMN_QR_CODE = "qr_code";
    public static final String COLUMN_DESCRIPTION = "description";

    // Stock Movements Table columns
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_MOVEMENT_TYPE = "movement_type";
    public static final String COLUMN_MOVEMENT_QUANTITY = "quantity";
    public static final String COLUMN_SUPPLIER_ID = "supplier_id";
    public static final String COLUMN_SUPPLIER_PRICE = "supplier_price";

    // Images Table columns
    public static final String COLUMN_REFERENCE_ID = "reference_id";
    public static final String COLUMN_REFERENCE_TYPE = "reference_type";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Updates Table columns
    public static final String COLUMN_UPDATE_TYPE = "update_type";
    public static final String COLUMN_UPDATE_DESCRIPTION = "description";
    public static final String COLUMN_QUANTITY_CHANGED = "quantity_changed";

    // Suppliers Table columns
    public static final String COLUMN_SUPPLIER_NAME = "name";
    public static final String COLUMN_SUPPLIER_CONTACT = "contact";

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
                    COLUMN_SUPPLIER_PRICE + " REAL, " +
                    COLUMN_SELLING_PRICE + " REAL, " +
                    COLUMN_SUPPLIER_ID + " INTEGER, " +
                    COLUMN_QR_CODE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_SUPPLIERS =
            "CREATE TABLE " + TABLE_SUPPLIERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                    COLUMN_SUPPLIER_CONTACT + " TEXT, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ")";


    private static final String CREATE_TABLE_STOCK_MOVEMENTS =
            "CREATE TABLE " + TABLE_STOCK_MOVEMENTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_MOVEMENT_TYPE + " TEXT NOT NULL, " +
                    COLUMN_MOVEMENT_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, " +
                    COLUMN_SUPPLIER_PRICE + " REAL NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " +
                    TABLE_PRODUCTS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_SUPPLIER_ID + ") REFERENCES " +
                    TABLE_SUPPLIERS + "(" + COLUMN_ID + ")" +
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

    private static final String CREATE_TABLE_UPDATES =
            "CREATE TABLE " + TABLE_UPDATES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BUSINESS_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_UPDATE_TYPE + " TEXT NOT NULL, " +
                    COLUMN_UPDATE_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY_CHANGED + " INTEGER, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_BUSINESS_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " +
                    TABLE_PRODUCTS + "(" + COLUMN_ID + ")" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables in order due to foreign key constraints
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_SUPPLIERS);
        db.execSQL(CREATE_TABLE_STOCK_MOVEMENTS);
        db.execSQL(CREATE_TABLE_IMAGES);
        db.execSQL(CREATE_TABLE_UPDATES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse order due to foreign key constraints
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPPLIERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }
}