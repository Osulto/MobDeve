package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.SupplierDao;
import com.mobdeve.s19.stocksmart.database.dao.StockMovementDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.Supplier;
import com.mobdeve.s19.stocksmart.database.models.StockMovement;

import java.util.ArrayList;
import java.util.List;

public class AddStockActivity extends AppCompatActivity {
    private Spinner categorySpinner;
    private Spinner productSpinner;
    private Spinner supplierSpinner;
    private Button btnAddSupplier;
    private TextInputLayout tilSupplierPrice, tilStockChange;
    private EditText etSupplierPrice, etStockChange;
    private ImageButton btnIncrease, btnDecrease;
    private Button btnSave, btnCancel;
    private BottomNavigationView bottomNavigation;

    private CategoryDao categoryDao;
    private ProductDao productDao;
    private SupplierDao supplierDao;
    private StockMovementDao stockMovementDao;

    private List<Category> categories;
    private List<Product> products;
    private List<Supplier> suppliers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        initializeDAOs();
        initializeViews();
        setupSpinners();
        setupListeners();
        setupBottomNavigation();
    }

    private void initializeDAOs() {
        categoryDao = new CategoryDao(this);
        productDao = new ProductDao(this);
        supplierDao = new SupplierDao(this);
        stockMovementDao = new StockMovementDao(this);
    }

    private void initializeViews() {
        categorySpinner = findViewById(R.id.categorySpinner);
        productSpinner = findViewById(R.id.productSpinner);
        supplierSpinner = findViewById(R.id.supplierSpinner);
        btnAddSupplier = findViewById(R.id.btnAddSupplier);
        tilSupplierPrice = findViewById(R.id.tilSupplierPrice);
        etSupplierPrice = findViewById(R.id.etSupplierPrice);
        tilStockChange = findViewById(R.id.tilStockChange);
        etStockChange = findViewById(R.id.etStockChange);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupSpinners() {
        setupCategorySpinner();
        setupProductSpinner(null);  // Initially no category selected
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

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Category selectedCategory = categories.get(position - 1);
                    setupProductSpinner(selectedCategory.getId());
                } else {
                    setupProductSpinner(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setupProductSpinner(null);
            }
        });
    }

    private void setupProductSpinner(Long categoryId) {
        if (categoryId != null) {
            products = productDao.getByCategory(categoryId);
        } else {
            products = new ArrayList<>();
        }

        List<String> productNames = new ArrayList<>();
        productNames.add("Select Product");
        for (Product product : products) {
            productNames.add(product.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                productNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);
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
        btnIncrease.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(etStockChange.getText().toString());
            etStockChange.setText(String.valueOf(currentValue + 1));
        });

        btnDecrease.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(etStockChange.getText().toString());
            if (currentValue > 0) {
                etStockChange.setText(String.valueOf(currentValue - 1));
            }
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateStock();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private boolean validateInput() {
        if (categorySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (productSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (supplierSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a supplier", Toast.LENGTH_SHORT).show();
            return false;
        }

        String priceStr = etSupplierPrice.getText().toString().trim();
        if (priceStr.isEmpty()) {
            tilSupplierPrice.setError("Supplier price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                tilSupplierPrice.setError("Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            tilSupplierPrice.setError("Invalid price");
            return false;
        }

        String stockStr = etStockChange.getText().toString().trim();
        if (stockStr.isEmpty()) {
            tilStockChange.setError("Stock change amount is required");
            return false;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            if (stock <= 0) {
                tilStockChange.setError("Please enter a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            tilStockChange.setError("Invalid number");
            return false;
        }

        return true;
    }

    private void updateStock() {
        Product selectedProduct = products.get(productSpinner.getSelectedItemPosition() - 1);
        Supplier selectedSupplier = suppliers.get(supplierSpinner.getSelectedItemPosition() - 1);
        int stockChange = Integer.parseInt(etStockChange.getText().toString().trim());
        double supplierPrice = Double.parseDouble(etSupplierPrice.getText().toString().trim());

        try {
            // Create stock movement
            StockMovement movement = new StockMovement(
                    selectedProduct.getId(),
                    "IN",
                    stockChange,
                    selectedSupplier.getId(),
                    supplierPrice
            );

            long movementId = stockMovementDao.insert(movement);

            if (movementId > 0) {
                // Update product quantity
                selectedProduct.setQuantity(selectedProduct.getQuantity() + stockChange);
                boolean success = productDao.update(selectedProduct);

                if (success) {
                    Toast.makeText(this,
                            "Added " + stockChange + " items to " + selectedProduct.getName(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update stock", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to record stock movement", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating stock", Toast.LENGTH_SHORT).show();
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
                return true;
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