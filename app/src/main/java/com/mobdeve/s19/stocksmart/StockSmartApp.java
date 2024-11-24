package com.mobdeve.s19.stocksmart;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.mobdeve.s19.stocksmart.database.DatabaseHelper;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;
import com.mobdeve.s19.stocksmart.database.dao.StockMovementDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void initializeSampleData() {
        try {
            // Only add sample data if there are no categories
            if (categoryDao.getAll().isEmpty()) {
                // Create sample category with placeholder image
                String iconPath = saveVectorDrawableAsBitmap(R.drawable.placeholder_image);
                if (iconPath != null) {
                    // Create sample category
                    Category sampleCategory = new Category("Sample Category", iconPath);
                    long categoryId = categoryDao.insert(sampleCategory);

                    if (categoryId != -1) {
                        // Create sample product
                        Product sampleProduct = new Product();
                        sampleProduct.setName("Sample Product");
                        sampleProduct.setCategoryId(categoryId);
                        sampleProduct.setQuantity(10);
                        sampleProduct.setReorderPoint(5);
                        sampleProduct.setCostPrice(100.0);
                        sampleProduct.setSellingPrice(150.0);
                        sampleProduct.setDescription("This is a sample product");
                        String timestamp = getCurrentTimestamp();
                        sampleProduct.setCreatedAt(timestamp);
                        sampleProduct.setUpdatedAt(timestamp);

                        productDao.insert(sampleProduct);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveVectorDrawableAsBitmap(int drawableId) {
        try {
            Drawable drawable = ContextCompat.getDrawable(this, drawableId);
            if (drawable == null) return null;

            // Make the bitmap bigger for better quality
            int width = Math.max(drawable.getIntrinsicWidth(), 256);
            int height = Math.max(drawable.getIntrinsicHeight(), 256);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            String filename = "sample_category_icon.jpg";
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