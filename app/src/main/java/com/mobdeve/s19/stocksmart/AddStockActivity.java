package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class AddStockActivity extends AppCompatActivity {
    private Spinner productSpinner;
    private TextInputLayout tilSupplier, tilNotes, tilStockChange;
    private EditText etSupplier, etNotes, etStockChange;
    private ImageButton btnIncrease, btnDecrease;
    private Button btnSave, btnCancel;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        initializeViews();
        setupProductSpinner();
        setupListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        productSpinner = findViewById(R.id.productSpinner);
        tilSupplier = findViewById(R.id.tilSupplier);
        etSupplier = findViewById(R.id.etSupplier);
        tilNotes = findViewById(R.id.tilNotes);
        etNotes = findViewById(R.id.etNotes);
        tilStockChange = findViewById(R.id.tilStockChange);
        etStockChange = findViewById(R.id.etStockChange);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupProductSpinner() {
        List<String> products = Arrays.asList("Select Product", "T-Shirt", "Jeans", "Sneakers");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                products
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);
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
                overridePendingTransition(0, 0); // Disable animation
                finish();
                return true;
            }
            return false;
        });
    }


    private boolean validateInput() {
        if (productSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
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
        String productName = productSpinner.getSelectedItem().toString();
        int stockChange = Integer.parseInt(etStockChange.getText().toString().trim());
        String supplier = etSupplier.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // TODO: Implement actual stock update logic
        Toast.makeText(this,
                "Added " + stockChange + " items to " + productName + " (Supplier: " + supplier + ", Notes: " + notes + ")",
                Toast.LENGTH_SHORT).show();
        finish();
    }

}