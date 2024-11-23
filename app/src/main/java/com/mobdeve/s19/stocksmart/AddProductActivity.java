package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
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

        String stockStr = etInitialStock.getText().toString().trim();
        if (stockStr.isEmpty()) {
            tilInitialStock.setError("Initial stock is required");
            isValid = false;
        } else {
            try {
                int stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    tilInitialStock.setError("Stock cannot be negative");
                    isValid = false;
                } else {
                    tilInitialStock.setError(null);
                }
            } catch (NumberFormatException e) {
                tilInitialStock.setError("Invalid number");
                isValid = false;
            }
        }

        String reorderStr = etReorderPoint.getText().toString().trim();
        if (reorderStr.isEmpty()) {
            tilReorderPoint.setError("Reorder point is required");
            isValid = false;
        } else {
            try {
                int reorder = Integer.parseInt(reorderStr);
                if (reorder < 0) {
                    tilReorderPoint.setError("Reorder point cannot be negative");
                    isValid = false;
                } else {
                    tilReorderPoint.setError(null);
                }
            } catch (NumberFormatException e) {
                tilReorderPoint.setError("Invalid number");
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveProduct() {
        // TODO: Implement saving logic
        String name = etProductName.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        int stock = Integer.parseInt(etInitialStock.getText().toString().trim());
        int reorderPoint = Integer.parseInt(etReorderPoint.getText().toString().trim());

        Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ProductsActivity.class));
        finish();
    }
}