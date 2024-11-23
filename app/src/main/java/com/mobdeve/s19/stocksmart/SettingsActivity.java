package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private MaterialCardView cardBusinessProfile;
    private MaterialCardView cardCurrency;
    private MaterialCardView cardExportData;
    private SwitchMaterial switchNotifications;
    private TextView tvBusinessName;
    private TextView tvCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        loadSettings();
    }

    private void initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        cardBusinessProfile = findViewById(R.id.cardBusinessProfile);
        cardCurrency = findViewById(R.id.cardCurrency);
        cardExportData = findViewById(R.id.cardExportData);
        switchNotifications = findViewById(R.id.switchNotifications);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvCurrency = findViewById(R.id.tvCurrency);
    }

    private void setupClickListeners() {
        // Business Profile
        cardBusinessProfile.setOnClickListener(v -> {
            // TODO: Implement business profile editing
            showNotImplementedMessage("Edit Business Profile");
        });

        // Currency Selection
        cardCurrency.setOnClickListener(v -> {
            showCurrencySelectionDialog();
        });

        // Export Data
        cardExportData.setOnClickListener(v -> {
            // TODO: Implement data export
            showNotImplementedMessage("Export Data");
        });

        // Notifications Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement notifications toggle
            // Save preference
        });

        // Sign Out
        findViewById(R.id.btnSignOut).setOnClickListener(v -> {
            showSignOutDialog();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_settings);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (itemId == R.id.navigation_products) {
                intent = new Intent(this, CategoryActivity.class);
            } else if (itemId == R.id.navigation_add) {
                intent = new Intent(this, AddStockActivity.class);
            } else if (itemId == R.id.navigation_reports) {
                intent = new Intent(this, ReportsActivity.class);
            } else if (itemId == R.id.navigation_settings) {
                return true; // Already on add page
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


    private void loadSettings() {
        // TODO: Load actual settings
        tvBusinessName.setText("My Clothing Store");
        tvCurrency.setText("PHP (₱)");
        switchNotifications.setChecked(true);
    }

    private void showCurrencySelectionDialog() {
        String[] currencies = {"PHP (₱)", "USD ($)", "EUR (€)", "JPY (¥)"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Currency")
                .setItems(currencies, (dialog, which) -> {
                    tvCurrency.setText(currencies[which]);
                    // TODO: Save selected currency
                })
                .show();
    }

    private void showSignOutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    // TODO: Implement sign out logic
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNotImplementedMessage(String feature) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Coming Soon")
                .setMessage(feature + " will be available in a future update.")
                .setPositiveButton("OK", null)
                .show();
    }
}