package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.UpdateDao;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.Update;



import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private Button btnUploadImage;
    private TextView productName, productCategory, productStock;

    // Stock management fields
    private TextInputLayout tilAddStock, tilRemoveStock;
    private EditText etAddStock, etRemoveStock;
    private Button btnUpdateStock;

    // Reorder point fields
    private TextInputLayout tilReorderPoint;
    private EditText etReorderPoint;
    private Button btnUpdateReorderPoint;

    // Price fields
    private TextInputLayout tilSellingPrice;
    private EditText etSellingPrice;
    private Button btnUpdateSellingPrice;

    // Updates section
    private RecyclerView recentUpdatesRecycler;
    private UpdateAdapter updateAdapter;
    private List<Update> recentUpdates;
    private Button btnViewAllUpdates;

    private BottomNavigationView bottomNavigation;

    // DAOs
    private ProductDao productDao;
    private UpdateDao updateDao;
    private long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productDao = new ProductDao(this);
        updateDao = new UpdateDao(this);

        initializeViews();
        setupBottomNavigation();
        setupRecyclerView();

        productId = getIntent().getLongExtra("product_id", -1);

        if (productId != -1) {
            loadProductDetails(productId);
            setupButtonListeners();
        } else {
            Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        // Basic product info
        productImage = findViewById(R.id.productImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productStock = findViewById(R.id.productStock);

        // Stock management
        tilAddStock = findViewById(R.id.tilAddStock);
        etAddStock = findViewById(R.id.etAddStock);
        tilRemoveStock = findViewById(R.id.tilRemoveStock);
        etRemoveStock = findViewById(R.id.etRemoveStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);

        // Reorder point
        tilReorderPoint = findViewById(R.id.tilReorderPoint);
        etReorderPoint = findViewById(R.id.etReorderPoint);
        btnUpdateReorderPoint = findViewById(R.id.btnUpdateReorderPoint);

        // Price management
        tilSellingPrice = findViewById(R.id.tilSellingPrice);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        btnUpdateSellingPrice = findViewById(R.id.btnUpdateSellingPrice);

        // Updates section
        recentUpdatesRecycler = findViewById(R.id.recentUpdatesRecycler);
        btnViewAllUpdates = findViewById(R.id.btnViewAllUpdates);

        // Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void loadProductDetails(long productId) {
        try {
            Product product = productDao.get(productId);
            if (product != null) {
                // Set basic info
                productName.setText(product.getName());
                productCategory.setText(getCategoryNameById(product.getCategoryId()));

                // Format the stock display text
                updateStockDisplay(product);

                // Set input fields
                etReorderPoint.setText(String.valueOf(product.getReorderPoint()));
                etAddStock.setText("0");
                etRemoveStock.setText("0");

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                etSellingPrice.setText(String.valueOf(product.getSellingPrice()));

                // Check for low stock alert
                checkLowStockAlert(product);
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e("ProductDetailsActivity", "Error loading product details", e);
            Toast.makeText(this, "Error loading product details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStockDisplay(Product product) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        String stockText = String.format("Current Stock: %d",
                product.getQuantity(),
                product.getReorderPoint(),
                currencyFormat.format(product.getSellingPrice()));
        productStock.setText(stockText);
    }

    private String getCategoryNameById(long categoryId) {
        CategoryDao categoryDao = new CategoryDao(this);
        return categoryDao.get(categoryId).getName();
    }

    private void setupButtonListeners() {
        btnUpdateStock.setOnClickListener(v -> updateStock());
        btnUpdateReorderPoint.setOnClickListener(v -> updateReorderPoint());
        btnUpdateSellingPrice.setOnClickListener(v -> updateSellingPrice());
        btnUploadImage.setOnClickListener(v -> {
            // TODO: Implement image upload
            Toast.makeText(this, "Image upload coming soon", Toast.LENGTH_SHORT).show();
        });
        btnViewAllUpdates.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdatesHistoryActivity.class);
            intent.putExtra("product_id", productId); // Optional: to filter for this product
            startActivity(intent);
        });
    }

    private void updateStock() {
        try {
            String addStockStr = etAddStock.getText().toString().trim();
            String removeStockStr = etRemoveStock.getText().toString().trim();

            int addAmount = addStockStr.isEmpty() ? 0 : Integer.parseInt(addStockStr);
            int removeAmount = removeStockStr.isEmpty() ? 0 : Integer.parseInt(removeStockStr);

            if (addAmount == 0 && removeAmount == 0) {
                Toast.makeText(this, "Please enter a quantity to add or remove", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = productDao.get(productId);
            if (product != null) {
                int newQuantity = product.getQuantity() + addAmount - removeAmount;
                if (newQuantity < 0) {
                    Toast.makeText(this, "Cannot remove more stock than available", Toast.LENGTH_SHORT).show();
                    return;
                }

                product.setQuantity(newQuantity);
                boolean success = productDao.update(product);

                if (success) {
                    // Create update records for stock changes
                    if (addAmount > 0) {
                        Update addUpdate = new Update(
                                productId,
                                Update.UpdateType.STOCK_ADDED,
                                addAmount,
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                        );
                        updateDao.insert(addUpdate);
                    }

                    if (removeAmount > 0) {
                        Update removeUpdate = new Update(
                                productId,
                                Update.UpdateType.STOCK_REMOVED,
                                removeAmount,
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                        );
                        updateDao.insert(removeUpdate);
                    }

                    // Reset input fields
                    etAddStock.setText("0");
                    etRemoveStock.setText("0");

                    // Refresh the display
                    loadProductDetails(productId);
                    loadProductUpdates();
                    checkLowStockAlert(product);

                    Toast.makeText(this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update stock", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateReorderPoint() {
        try {
            String reorderPointStr = etReorderPoint.getText().toString().trim();
            if (reorderPointStr.isEmpty()) {
                tilReorderPoint.setError("Please enter a valid reorder point");
                return;
            }

            int reorderPoint = Integer.parseInt(reorderPointStr);
            if (reorderPoint < 0) {
                tilReorderPoint.setError("Reorder point cannot be negative");
                return;
            }

            Product product = productDao.get(productId);
            if (product != null) {
                product.setReorderPoint(reorderPoint);
                boolean success = productDao.update(product);
                if (success) {
                    Toast.makeText(this, "Reorder point updated successfully", Toast.LENGTH_SHORT).show();

                    Update update = new Update(
                            productId,
                            Update.UpdateType.PRICE_UPDATED,
                            reorderPoint,
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                    );

                    updateDao.insert(update);

                    loadProductDetails(productId);
                    loadProductUpdates();
                } else {
                    Toast.makeText(this, "Failed to update reorder point", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input for reorder point", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSellingPrice() {
        try {
            String priceStr = etSellingPrice.getText().toString().trim();
            if (priceStr.isEmpty()) {
                tilSellingPrice.setError("Please enter a valid price");
                return;
            }

            double sellingPrice = Double.parseDouble(priceStr);
            if (sellingPrice < 0) {
                tilSellingPrice.setError("Price cannot be negative");
                return;
            }

            Product product = productDao.get(productId);
            if (product != null) {
                product.setSellingPrice(sellingPrice);
                boolean success = productDao.update(product);
                if (success) {
                    Toast.makeText(this, "Selling price updated successfully", Toast.LENGTH_SHORT).show();

                    // Create update record - store the actual price value
                    Update update = new Update(
                            productId,
                            Update.UpdateType.PRICE_UPDATED,
                            (int)(sellingPrice * 100), // Convert to cents for integer storage
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                    );
                    updateDao.insert(update);

                    loadProductDetails(productId);
                    loadProductUpdates();
                } else {
                    Toast.makeText(this, "Failed to update selling price", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input for selling price", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        recentUpdatesRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentUpdates = new ArrayList<>();
        updateAdapter = new UpdateAdapter(recentUpdates);
        recentUpdatesRecycler.setAdapter(updateAdapter);
        loadProductUpdates();
    }

    private void loadProductUpdates() {
        // Get all updates for this product
        List<Update> allUpdates = updateDao.getAll();
        recentUpdates.clear();

        // Filter updates for this specific product and limit to 5 most recent
        List<Update> filteredUpdates = new ArrayList<>();
        for (Update update : allUpdates) {
            if (update.getProductId() == productId) {
                Product product = productDao.get(update.getProductId());
                if (product != null) {
                    update.setProductName(product.getName());
                    filteredUpdates.add(update);
                }
            }
        }

        // Sort updates by date (newest first) and take only the first 5
        filteredUpdates.sort((u1, u2) -> u2.getTimestamp().compareTo(u1.getTimestamp()));
        int updateLimit = Math.min(filteredUpdates.size(), 5);
        recentUpdates.addAll(filteredUpdates.subList(0, updateLimit));

        updateAdapter.notifyDataSetChanged();
    }

    private void checkLowStockAlert(Product product) {
        if (product.getQuantity() <= product.getReorderPoint()) {
            // Create low stock alert update
            Update update = new Update(
                    product.getId(),
                    Update.UpdateType.LOW_STOCK_ALERT,
                    product.getQuantity(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
            );
            updateDao.insert(update);
            loadProductUpdates();

            // Show alert to user
            Toast.makeText(this,
                    "Low stock alert: Current stock is at or below reorder point",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (itemId == R.id.navigation_products) {
                intent = new Intent(this, ProductsActivity.class);
            } else if (itemId == R.id.navigation_add) {
                intent = new Intent(this, AddStockActivity.class);
            } else if (itemId == R.id.navigation_reports) {
                intent = new Intent(this, ReportsActivity.class);
            } else if (itemId == R.id.navigation_settings) {
                intent = new Intent(this, SettingsActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productId != -1) {
            loadProductDetails(productId);
            loadProductUpdates();
        }
    }

    private void refreshData() {
        Product product = productDao.get(productId);
        if (product != null) {
            updateStockDisplay(product);
            checkLowStockAlert(product);
            loadProductUpdates();
        }
    }
}