package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.button.MaterialButton;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;
import com.mobdeve.s19.stocksmart.database.models.User;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private MaterialCardView cardBusinessProfile;
    private MaterialCardView cardCurrency;
    private MaterialCardView cardExportData;
    private MaterialCardView cardSyncStatus;
    private MaterialButton btnSyncNow;
    private TextView tvSyncStatus;
    private TextView tvLastSync;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchOfflineMode;
    private TextView tvBusinessName;
    private TextView tvCurrency;
    private UserDao userDao;
    private FirebaseAuthManager firebaseAuth;
    private String currentBusinessName;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userDao = ((StockSmartApp) getApplication()).getUserDao();
        firebaseAuth = new FirebaseAuthManager(this);
        syncManager = new SyncManager(this);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        loadSettings();
    }

    private void initializeViews() {
        // Existing views
        bottomNavigation = findViewById(R.id.bottomNavigation);
        cardBusinessProfile = findViewById(R.id.cardBusinessProfile);
        cardCurrency = findViewById(R.id.cardCurrency);
        cardExportData = findViewById(R.id.cardExportData);
        cardSyncStatus = findViewById(R.id.cardSyncStatus);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchOfflineMode = findViewById(R.id.switchOfflineMode);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvCurrency = findViewById(R.id.tvCurrency);

        // Sync related views
        btnSyncNow = findViewById(R.id.btnSyncNow);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvLastSync = findViewById(R.id.tvLastSync);
    }

    private void setupClickListeners() {
        // Existing click listeners
        cardBusinessProfile.setOnClickListener(v -> showBusinessProfileDialog());
        cardCurrency.setOnClickListener(v -> showCurrencySelectionDialog());
        cardExportData.setOnClickListener(v -> showNotImplementedMessage("Export Data"));

        // Sync button
        btnSyncNow.setOnClickListener(v -> syncData());

        // Offline mode switch
        switchOfflineMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.setOfflineMode(isChecked);
            if (isChecked) {
                Toast.makeText(this, "Offline mode enabled", Toast.LENGTH_SHORT).show();
            } else {
                syncData();
            }
        });

        // Sign Out
        findViewById(R.id.btnSignOut).setOnClickListener(v -> showSignOutDialog());
    }

    private void syncData() {
        if (!switchOfflineMode.isChecked()) {
            tvSyncStatus.setText("Syncing...");
            btnSyncNow.setEnabled(false);

            syncManager.syncToCloud(new SyncManager.SyncCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        tvSyncStatus.setText("Up to date");
                        tvSyncStatus.setTextColor(getResources().getColor(R.color.success));
                        tvLastSync.setText("Last synced: Just now");
                        btnSyncNow.setEnabled(true);
                        Toast.makeText(SettingsActivity.this, "Data synced successfully", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        tvSyncStatus.setText("Sync failed");
                        tvSyncStatus.setTextColor(getResources().getColor(R.color.error));
                        btnSyncNow.setEnabled(true);
                        Toast.makeText(SettingsActivity.this, "Sync failed: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Toast.makeText(this, "Please disable offline mode to sync", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSettings() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        User currentUser = userDao.findByUsername(sessionManager.getUsername());

        if (currentUser != null) {
            currentBusinessName = currentUser.getBusinessName();
            tvBusinessName.setText(currentBusinessName);
        }

        tvCurrency.setText("PHP (₱)");
        switchNotifications.setChecked(true);
        tvLastSync.setText("Last synced: " + getFormattedDateTime());
        switchOfflineMode.setChecked(sessionManager.isOfflineMode());

    }

    private void showBusinessProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_business_profile, null);
        TextView tvCurrentBusiness = dialogView.findViewById(R.id.tvCurrentBusiness);
        TextView tvBusinessUsers = dialogView.findViewById(R.id.tvBusinessUsers);

        tvCurrentBusiness.setText("Business Name: " + currentBusinessName);

        // Get and display all users for this business
        List<User> businessUsers = userDao.getAllUsersForBusiness(currentBusinessName);
        StringBuilder usersText = new StringBuilder();
        for (User user : businessUsers) {
            usersText.append("• ").append(user.getUsername()).append("\n");
        }
        tvBusinessUsers.setText(usersText.toString());

        new MaterialAlertDialogBuilder(this)
                .setTitle("Business Profile")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .show();
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
                    // Clear session and Firebase auth
                    SessionManager.getInstance(this).logout();
                    firebaseAuth.logoutUser();

                    // Clear activity stack and go to login
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
                return true; // Already on settings page
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

    private void showNotImplementedMessage(String feature) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Coming Soon")
                .setMessage(feature + " will be available in a future update.")
                .setPositiveButton("OK", null)
                .show();
    }

    private String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}