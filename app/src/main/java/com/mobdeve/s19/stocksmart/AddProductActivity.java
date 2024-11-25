package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private Button btnUploadImage;
    private TextInputLayout tilProductName;
    private EditText etProductName;
    private Spinner categorySpinner;
    private TextInputLayout tilInitialStock;
    private EditText etInitialStock;
    private TextInputLayout tilReorderPoint;
    private EditText etReorderPoint;
    private Button btnSave;
    private Button btnCancel;
    private BottomNavigationView bottomNavigation;

    private static final String[] CATEGORIES = {
            "Select Category", "Clothing", "Footwear", "Accessories", "Electronics"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initializeViews();
        setupSpinner();
        setupListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategoriesIntoSpinner(); // Refresh the spinner on activity resume
    }


    private void initializeViews() {
        productImage = findViewById(R.id.productImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        tilProductName = findViewById(R.id.tilProductName);
        etProductName = findViewById(R.id.etProductName);
        categorySpinner = findViewById(R.id.categorySpinner);
        tilInitialStock = findViewById(R.id.tilInitialStock);
        etInitialStock = findViewById(R.id.etInitialStock);
        tilReorderPoint = findViewById(R.id.tilReorderPoint);
        etReorderPoint = findViewById(R.id.etReorderPoint);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void loadCategoriesIntoSpinner() {
        // Fetch categories from the database
        CategoryDao categoryDao = new CategoryDao(this);
        List<Category> categories = categoryDao.getAll();

        // Convert to a list of category names
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category"); // Default option
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        // Set up the spinner with the updated list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }


    private void setupListeners() {
        btnUploadImage.setOnClickListener(v -> {
            // TODO: Implement image upload
            Toast.makeText(this, "Image upload coming soon", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveProduct();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_add);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (itemId == R.id.navigation_products) {
                intent = new Intent(this, CategoryActivity.class);
            } else if (itemId == R.id.navigation_add) {
                return true; // Already on add page
            } else if (itemId == R.id.navigation_reports) {
                intent = new Intent(this, ReportsActivity.class);
            } else if (itemId == R.id.navigation_settings) {
                intent = new Intent(this, SettingsActivity.class);
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


    private boolean validateInput() {
        boolean isValid = true;

        if (etProductName.getText().toString().trim().isEmpty()) {
            tilProductName.setError("Product name is required");
            isValid = false;
        } else {
            tilProductName.setError(null);
        }

        if (categorySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        try {
            int initialStock = Integer.parseInt(etInitialStock.getText().toString().trim());
            if (initialStock < 0) {
                tilInitialStock.setError("Stock cannot be negative");
                isValid = false;
            } else {
                tilInitialStock.setError(null);
            }
        } catch (NumberFormatException e) {
            tilInitialStock.setError("Invalid number");
            isValid = false;
        }

        try {
            int reorderPoint = Integer.parseInt(etReorderPoint.getText().toString().trim());
            if (reorderPoint < 0) {
                tilReorderPoint.setError("Reorder point cannot be negative");
                isValid = false;
            } else {
                tilReorderPoint.setError(null);
            }
        } catch (NumberFormatException e) {
            tilReorderPoint.setError("Invalid number");
            isValid = false;
        }

        return isValid;
    }


    private void saveProduct() {
        // Retrieve user inputs
        String productName = etProductName.getText().toString().trim();
        int initialStock = Integer.parseInt(etInitialStock.getText().toString().trim());
        int reorderPoint = Integer.parseInt(etReorderPoint.getText().toString().trim());
        String categoryName = categorySpinner.getSelectedItem().toString();

        // Validate category selection
        if (categoryName.equals("Select Category")) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch Category ID from Category Name
        CategoryDao categoryDao = new CategoryDao(this);
        Category category = categoryDao.getByName(categoryName);
        if (category == null) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            return;
        }
        long categoryId = category.getId();

        // Log inputs
        Log.d("AddProductActivity", "Product Name: " + productName);
        Log.d("AddProductActivity", "Category ID: " + categoryId);
        Log.d("AddProductActivity", "Initial Stock: " + initialStock);
        Log.d("AddProductActivity", "Reorder Point: " + reorderPoint);


        logProductIds();
        // Create Product object
        // Create Product object
        Product product = new Product(
                productName,             // String
                categoryId,              // long
                initialStock,            // int
                reorderPoint,            // int
                0.0,                     // double (cost price)
                0.0,                     // double (selling price)
                null,                    // String (qrCode) - Set to null for no QR code
                ""                       // String (description)
        );


        // Save Product to the database
        ProductDao productDao = new ProductDao(this);
        long productId = productDao.insert(product);

        Log.d("AddProductActivity", "Insert Result: " + productId);

        if (productId > 0) {
            Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after saving
        } else {
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        }

    }

    public void logProductIds() {
        ProductDao productDao = new ProductDao(this);
        List<Product> products = productDao.getAll(); // Fetch all products

        for (Product product : products) {
            Log.d("ProductList", "Product ID: " + product.getId());
        }
    }


}