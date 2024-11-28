package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.StockMovementDao;
import com.mobdeve.s19.stocksmart.database.dao.UpdateDao;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.StockMovement;
import com.mobdeve.s19.stocksmart.database.models.Update;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rvLowStockItems;
    private BottomNavigationView bottomNavigation;
    private MaterialButton btnScanQr, btnManualAdd;
    private TextView tvStockValue, tvPotentialRevenue, tvTotalItems, tvLowStockCount;
    private UpdateAdapter updateAdapter;
    private List<Update> updatesList;

    private ProductDao productDao;
    private StockMovementDao stockMovementDao;
    private UpdateDao updateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        // Initialize DAOs
        productDao = new ProductDao(this);
        stockMovementDao = new StockMovementDao(this);
        updateDao = new UpdateDao(this);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setupUpdatesRecyclerView();
        updateDashboardData();
    }

    private void initializeViews() {
        rvLowStockItems = findViewById(R.id.rvLowStockItems);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnScanQr = findViewById(R.id.btnScanQr);
        btnManualAdd = findViewById(R.id.btnManualAdd);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvPotentialRevenue = findViewById(R.id.tvPotentialRevenue);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void setupClickListeners() {
        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRScannerActivity.class);
            startActivity(intent);
        });

        btnManualAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStockActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_products) {
                intent = new Intent(this, CategoryActivity.class);
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

    private void setupUpdatesRecyclerView() {
        rvLowStockItems.setLayoutManager(new LinearLayoutManager(this));
        updatesList = new ArrayList<>();
        updateAdapter = new UpdateAdapter(updatesList);
        rvLowStockItems.setAdapter(updateAdapter);
        loadRecentUpdates();
    }

    private void loadRecentUpdates() {
        updatesList.clear();

        // Get recent updates
        List<Update> updates = updateDao.getRecentUpdates(10);
        for (Update update : updates) {
            Product product = productDao.get(update.getProductId());
            if (product != null) {
                update.setProductName(product.getName());
                // For price updates, add the new price to the description
                if (update.getType() == Update.UpdateType.PRICE_UPDATED) {
                    update.setDescription(update.getDescription() +
                            String.format(" New price: %s",
                                    formatCurrency(product.getSellingPrice())));
                }
                updatesList.add(update);
            }
        }

        // Get stock movements to show price changes
        List<StockMovement> movements = stockMovementDao.getAll();
        for (StockMovement movement : movements) {
            Product product = productDao.get(movement.getProductId());
            if (product != null) {
                Update update = new Update(
                        product.getId(),
                        Update.UpdateType.STOCK_ADDED,
                        movement.getQuantity(),
                        movement.getCreatedAt()
                );
                update.setProductName(product.getName());
                update.setDescription(String.format("Added %d units at %s per unit",
                        movement.getQuantity(),
                        formatCurrency(movement.getSupplierPrice())));
                updatesList.add(update);
            }
        }

        // Check for low stock alerts
        List<Product> lowStockProducts = productDao.getLowStockProducts();
        for (Product product : lowStockProducts) {
            boolean hasRecentAlert = false;
            for (Update update : updatesList) {
                if (update.getProductId() == product.getId() &&
                        update.getType() == Update.UpdateType.LOW_STOCK_ALERT &&
                        isUpdateRecent(update.getDate())) {
                    hasRecentAlert = true;
                    break;
                }
            }

            if (!hasRecentAlert) {
                Update lowStockUpdate = new Update(
                        product.getId(),
                        Update.UpdateType.LOW_STOCK_ALERT,
                        product.getQuantity(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
                );
                lowStockUpdate.setProductName(product.getName());
                updateDao.insert(lowStockUpdate);
                updatesList.add(0, lowStockUpdate);
            }
        }

        // Sort updates by date
        updatesList.sort((u1, u2) -> u2.getDate().compareTo(u1.getDate()));

        // Keep only the most recent 10 updates
        if (updatesList.size() > 10) {
            updatesList = updatesList.subList(0, 10);
        }

        updateAdapter.notifyDataSetChanged();
    }

    private void updateDashboardData() {
        calculateFinancialMetrics();
        updateStockCounts();
    }

    private void calculateFinancialMetrics() {
        double stockValue = stockMovementDao.calculateTotalStockValue();
        double potentialRevenue = 0.0;

        List<Product> products = productDao.getAll();
        for (Product product : products) {
            potentialRevenue += product.getQuantity() * product.getSellingPrice();

            if (product.getQuantity() <= product.getReorderPoint()) {
                boolean hasRecentAlert = false;
                for (Update update : updatesList) {
                    if (update.getProductId() == product.getId() &&
                            update.getType() == Update.UpdateType.LOW_STOCK_ALERT &&
                            isUpdateRecent(update.getDate())) {
                        hasRecentAlert = true;
                        break;
                    }
                }

                if (!hasRecentAlert) {
                    addNewUpdate(product.getId(), Update.UpdateType.LOW_STOCK_ALERT, product.getQuantity());
                }
            }
        }

        tvStockValue.setText(formatCurrency(stockValue));
        tvPotentialRevenue.setText(formatCurrency(potentialRevenue));
    }

    private void updateStockCounts() {
        List<Product> allProducts = productDao.getAll();
        List<Product> lowStockProducts = productDao.getLowStockProducts();

        int totalItems = 0;
        for (Product product : allProducts) {
            totalItems += product.getQuantity();
        }

        tvTotalItems.setText(String.valueOf(totalItems));
        tvLowStockCount.setText(String.valueOf(lowStockProducts.size()));
    }

    private boolean isUpdateRecent(String updateDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(updateDate);
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - date.getTime());
            long diffInHours = diffInMillies / (60 * 60 * 1000);
            return diffInHours < 24;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(amount);
    }

    public void addNewUpdate(long productId, Update.UpdateType type, int quantity) {
        Update update = new Update(
                productId,
                type,
                quantity,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())
        );

        long updateId = updateDao.insert(update);
        if (updateId > 0) {
            Product product = productDao.get(productId);
            if (product != null) {
                update.setProductName(product.getName());
                updatesList.add(0, update);
                updateAdapter.notifyItemInserted(0);
                rvLowStockItems.scrollToPosition(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardData();
        loadRecentUpdates();
    }
}