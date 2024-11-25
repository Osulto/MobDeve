package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private Button btnUploadImage;
    private TextView productName, productCategory, productStock;
    private TextInputEditText etAddStock, etRemoveStock, etReorderPoint;
    private Button btnUpdateStock, btnUpdateReorderPoint;
    private RecyclerView recentUpdatesRecycler;
    private BottomNavigationView bottomNavigation;
    private UpdateAdapter updateAdapter;
    private List<Update> recentUpdates;

    private ProductDao productDao;
    private long productId; // The product ID passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initializeViews();
        setupBottomNavigation();
        setupRecyclerView();

        productDao = new ProductDao(this);

        // Get the product ID from the intent
        productId = getIntent().getLongExtra("product_id", -1);

        if (productId != -1) {
            loadProductDetails(productId); // Load product details if ID is valid
            setupButtonListeners(); // Add listeners to buttons
        } else {
            Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupButtonListeners() {
        btnUpdateStock.setOnClickListener(v -> {
            try {
                int addStock = !etAddStock.getText().toString().trim().isEmpty() ?
                        Integer.parseInt(etAddStock.getText().toString().trim()) : 0;

                int removeStock = !etRemoveStock.getText().toString().trim().isEmpty() ?
                        Integer.parseInt(etRemoveStock.getText().toString().trim()) : 0;

                int netChange = addStock - removeStock;
                if (netChange == 0) {
                    Toast.makeText(this, "No changes made to stock", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product product = productDao.get(productId);
                if (product != null) {
                    int newStock = product.getQuantity() + netChange;
                    if (newStock < 0) {
                        Toast.makeText(this, "Stock cannot be negative", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    product.setQuantity(newStock);
                    boolean success = productDao.update(product);
                    if (success) {
                        Toast.makeText(this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                        productStock.setText("Current Stock: " + newStock);
                        etAddStock.setText("");
                        etRemoveStock.setText("");
                    } else {
                        Toast.makeText(this, "Failed to update stock", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid input for stock update", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdateReorderPoint.setOnClickListener(v -> {
            try {
                String reorderPointStr = etReorderPoint.getText().toString().trim();
                if (reorderPointStr.isEmpty()) {
                    Toast.makeText(this, "Please enter a valid reorder point", Toast.LENGTH_SHORT).show();
                    return;
                }

                int reorderPoint = Integer.parseInt(reorderPointStr);
                if (reorderPoint < 0) {
                    Toast.makeText(this, "Reorder point cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product product = productDao.get(productId);
                if (product != null) {
                    product.setReorderPoint(reorderPoint);
                    boolean success = productDao.update(product);
                    if (success) {
                        Toast.makeText(this, "Reorder point updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update reorder point", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid input for reorder point", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        productImage = findViewById(R.id.productImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productStock = findViewById(R.id.productStock);
        etAddStock = findViewById(R.id.etAddStock);
        etRemoveStock = findViewById(R.id.etRemoveStock);
        etReorderPoint = findViewById(R.id.etReorderPoint);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);
        btnUpdateReorderPoint = findViewById(R.id.btnUpdateReorderPoint);
        recentUpdatesRecycler = findViewById(R.id.recentUpdatesRecycler);
        bottomNavigation = findViewById(R.id.bottomNavigation);
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
                return true;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        recentUpdatesRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentUpdates = new ArrayList<>();
        updateAdapter = new UpdateAdapter(recentUpdates);
        recentUpdatesRecycler.setAdapter(updateAdapter);
    }

    private void loadProductDetails(long productId) {
        try {
            Product product = productDao.get(productId); // Fetch product details from the database
            if (product != null) {
                productName.setText(product.getName());
                productCategory.setText(getCategoryNameById(product.getCategoryId()));
                productStock.setText("Current Stock: " + product.getQuantity());
                etReorderPoint.setText(String.valueOf(product.getReorderPoint()));

                // TODO: Load product image if available (use product.getQrCode() or description)
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

    private String getCategoryNameById(long categoryId) {
        // Fetch category name from database (use CategoryDao or a similar method)
        return new CategoryDao(this).get(categoryId).getName();
    }
}
