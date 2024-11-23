package com.mobdeve.s19.stocksmart;

import android.app.Application;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;
import com.mobdeve.s19.stocksmart.database.dao.StockMovementDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;

public class StockSmartApp extends Application {
    private DatabaseHelper dbHelper;
    private UserDao userDao;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private StockMovementDao stockMovementDao;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize DAOs
        userDao = new UserDao(this);
        categoryDao = new CategoryDao(this);
        productDao = new ProductDao(this);
        stockMovementDao = new StockMovementDao(this);

        // Initialize default categories if none exist
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        if (categoryDao.getAll().isEmpty()) {
            // Save the placeholder image first and use its path
            String defaultImagePath = saveDefaultImage(R.drawable.placeholder_image);
            if (defaultImagePath != null) {
                categoryDao.insert(new Category("Tops", defaultImagePath));
                categoryDao.insert(new Category("Bottoms", defaultImagePath));
                categoryDao.insert(new Category("Dresses", defaultImagePath));
                categoryDao.insert(new Category("Outerwear", defaultImagePath));
                categoryDao.insert(new Category("Accessories", defaultImagePath));
                categoryDao.insert(new Category("Footwear", defaultImagePath));
            }
        }
    }

    private String saveDefaultImage(int resourceId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            String filename = "default_category_icon.jpg";
            File file = new File(getFilesDir(), filename);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public StockMovementDao getStockMovementDao() {
        return stockMovementDao;
    }

    @Override
    public void onTerminate() {
        dbHelper.close();
        super.onTerminate();
    }
}