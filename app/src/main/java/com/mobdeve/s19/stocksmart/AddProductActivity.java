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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.SupplierDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.Supplier;

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
    private Spinner supplierSpinner;
    private Button btnAddSupplier;
    private TextInputLayout tilSupplierPrice;
    private EditText etSupplierPrice;
    private TextInputLayout tilSellingPrice;
    private EditText etSellingPrice;
    private Button btnSave;
    private Button btnCancel;
    private BottomNavigationView bottomNavigation;

    private Product existingProduct;
    private boolean isEditMode = false;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private SupplierDao supplierDao;
    private List<Category> categories;
    private List<Supplier> suppliers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize DAOs
        categoryDao = new CategoryDao(this);
        productDao = new ProductDao(this);
        supplierDao = new SupplierDao(this);

        initializeViews();
        setupSpinners();

        // Check if we're in edit mode
        long productId = getIntent().getLongExtra("product_id", -1);
        if (productId != -1) {
            isEditMode = true;
            existingProduct = productDao.get(productId);
            if (existingProduct != null) {
                populateFields();
            }
        }

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
        supplierSpinner = findViewById(R.id.supplierSpinner);
        btnAddSupplier = findViewById(R.id.btnAddSupplier);
        tilSupplierPrice = findViewById(R.id.tilSupplierPrice);
        etSupplierPrice = findViewById(R.id.etSupplierPrice);
        tilSellingPrice = findViewById(R.id.tilSellingPrice);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void populateFields() {
        etProductName.setText(existingProduct.getName());
        etInitialStock.setText(String.valueOf(existingProduct.getQuantity()));
        etReorderPoint.setText(String.valueOf(existingProduct.getReorderPoint()));
        etSupplierPrice.setText(String.valueOf(existingProduct.getSupplierPrice()));
        etSellingPrice.setText(String.valueOf(existingProduct.getSellingPrice()));

        // Set the correct category in spinner
        CategoryDao categoryDao = new CategoryDao(this);
        Category category = categoryDao.get(existingProduct.getCategoryId());
        if (category != null) {
            for (int i = 0; i < categorySpinner.getAdapter().getCount(); i++) {
                if (categorySpinner.getItemAtPosition(i).toString().equals(category.getName())) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        // Set the correct supplier in spinner
        if (existingProduct.getSupplierId() != null) {
            Supplier supplier = supplierDao.get(existingProduct.getSupplierId());
            if (supplier != null) {
                for (int i = 0; i < supplierSpinner.getAdapter().getCount(); i++) {
                    if (supplierSpinner.getItemAtPosition(i).toString().equals(supplier.getName())) {
                        supplierSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        // Update UI elements for edit mode
        btnSave.setText("Update");
        setTitle("Edit Product");
    }

    private void setupSpinners() {
        setupCategorySpinner();
        setupSupplierSpinner();
    }

    private void setupCategorySpinner() {
        categories = categoryDao.getAll();
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupSupplierSpinner() {
        refreshSupplierSpinner();
        btnAddSupplier.setOnClickListener(v -> showAddSupplierDialog());
    }

    private void refreshSupplierSpinner() {
        suppliers = supplierDao.getAll();
        List<String> supplierNames = new ArrayList<>();
        supplierNames.add("Select Supplier");
        for (Supplier supplier : suppliers) {
            supplierNames.add(supplier.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                supplierNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supplierSpinner.setAdapter(adapter);
    }

    private void showAddSupplierDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_supplier, null);
        EditText etSupplierName = dialogView.findViewById(R.id.etSupplierName);
        EditText etSupplierContact = dialogView.findViewById(R.id.etSupplierContact);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Supplier")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etSupplierName.getText().toString().trim();
                    String contact = etSupplierContact.getText().toString().trim();

                    if (!name.isEmpty()) {
                        Supplier newSupplier = new Supplier(name, contact);
                        long id = supplierDao.insert(newSupplier);
                        if (id > 0) {
                            Toast.makeText(this, "Supplier added successfully", Toast.LENGTH_SHORT).show();
                            refreshSupplierSpinner();
                            supplierSpinner.setSelection(supplierSpinner.getAdapter().getCount() - 1);
                        } else {
                            Toast.makeText(this, "Failed to add supplier", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

        if (supplierSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a supplier", Toast.LENGTH_SHORT).show();
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

        String supplierPriceStr = etSupplierPrice.getText().toString().trim();
        if (supplierPriceStr.isEmpty()) {
            tilSupplierPrice.setError("Supplier price is required");
            isValid = false;
        } else {
            try {
                double supplierPrice = Double.parseDouble(supplierPriceStr);
                if (supplierPrice <= 0) {
                    tilSupplierPrice.setError("Price must be greater than 0");
                    isValid = false;
                } else {
                    tilSupplierPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilSupplierPrice.setError("Invalid price");
                isValid = false;
            }
        }

        String sellingPriceStr = etSellingPrice.getText().toString().trim();
        if (sellingPriceStr.isEmpty()) {
            tilSellingPrice.setError("Selling price is required");
            isValid = false;
        } else {
            try {
                double sellingPrice = Double.parseDouble(sellingPriceStr);
                if (sellingPrice <= 0) {
                    tilSellingPrice.setError("Price must be greater than 0");
                    isValid = false;
                } else {
                    tilSellingPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilSellingPrice.setError("Invalid price");
                isValid = false;
            }
        }

        return isValid;
    }

    private void saveProduct() {
        // Retrieve user inputs
        String productName = etProductName.getText().toString().trim();
        int initialStock = Integer.parseInt(etInitialStock.getText().toString().trim());
        int reorderPoint = Integer.parseInt(etReorderPoint.getText().toString().trim());
        double supplierPrice = Double.parseDouble(etSupplierPrice.getText().toString().trim());
        double sellingPrice = Double.parseDouble(etSellingPrice.getText().toString().trim());
        String categoryName = categorySpinner.getSelectedItem().toString();
        String supplierName = supplierSpinner.getSelectedItem().toString();

        // Validate category selection
        if (categoryName.equals("Select Category")) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch Category ID from Category Name
        Category category = categoryDao.getByName(categoryName);
        if (category == null) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            return;
        }
        long categoryId = category.getId();

        // Fetch Supplier ID from Supplier Name
        Supplier supplier = supplierDao.getByName(supplierName);
        if (supplier == null) {
            Toast.makeText(this, "Supplier not found", Toast.LENGTH_SHORT).show();
            return;
        }
        Long supplierId = supplier.getId();

        if (isEditMode && existingProduct != null) {
            // Update existing product
            existingProduct.setName(productName);
            existingProduct.setCategoryId(categoryId);
            existingProduct.setQuantity(initialStock);
            existingProduct.setReorderPoint(reorderPoint);
            existingProduct.setSupplierPrice(supplierPrice);
            existingProduct.setSellingPrice(sellingPrice);
            existingProduct.setSupplierId(supplierId);

            if (productDao.update(existingProduct)) {
                Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new product
            Product product = new Product(
                    productName,              // name
                    categoryId,               // categoryId
                    initialStock,             // quantity
                    reorderPoint,             // reorderPoint
                    supplierPrice,            // supplierPrice
                    sellingPrice,             // sellingPrice
                    supplierId,               // supplierId
                    "",                       // qrCode
                    ""                        // description
            );

            long productId = productDao.insert(product);
            if (productId > 0) {
                Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        }
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
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}