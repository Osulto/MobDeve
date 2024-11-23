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
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.StockMovement;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rvLowStockItems;
    private BottomNavigationView bottomNavigation;
    private MaterialButton btnScanQr, btnManualAdd;
    private TextView tvStockValue, tvPotentialRevenue, tvTotalItems, tvLowStockCount;
    private UpdateAdapter updateAdapter;
    private List<Update> updatesList;

    private ProductDao productDao;
    private StockMovementDao stockMovementDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize DAOs
        StockSmartApp app = (StockSmartApp) getApplication();
        productDao = app.getProductDao();
        stockMovementDao = app.getStockMovementDao();

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

        // Get recent stock movements
        List<StockMovement> movements = stockMovementDao.getAll();
        for (StockMovement movement : movements) {
            Product product = productDao.get(movement.getProductId());
            if (product != null) {
                Update.UpdateType type;
                if (movement.getMovementType().equals("IN")) {
                    type = Update.UpdateType.STOCK_ADDED;
                } else {
                    type = Update.UpdateType.STOCK_REMOVED;
                }

                updatesList.add(new Update(
                        product.getName(),
                        type,
                        movement.getQuantity(),
                        movement.getCreatedAt()
                ));
            }
        }

        // Get low stock alerts
        List<Product> lowStockProducts = productDao.getLowStockProducts();
        for (Product product : lowStockProducts) {
            updatesList.add(new Update(
                    product.getName(),
                    Update.UpdateType.LOW_STOCK_ALERT,
                    product.getQuantity(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(new Date())
            ));
        }

        updateAdapter.notifyDataSetChanged();
    }

    private void updateDashboardData() {
        calculateFinancialMetrics();
        updateStockCounts();
    }

    private void calculateFinancialMetrics() {
        double stockValue = 0.0;
        double potentialRevenue = 0.0;

        List<Product> products = productDao.getAll();
        for (Product product : products) {
            stockValue += product.getQuantity() * product.getCostPrice();
            potentialRevenue += product.getQuantity() * product.getSellingPrice();
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        tvStockValue.setText(currencyFormat.format(stockValue));
        tvPotentialRevenue.setText(currencyFormat.format(potentialRevenue));
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

    public void addNewUpdate(String productName, Update.UpdateType type, int quantity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        Update newUpdate = new Update(productName, type, quantity, currentTime);
        updatesList.add(0, newUpdate);
        updateAdapter.notifyItemInserted(0);
        rvLowStockItems.scrollToPosition(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardData();
        loadRecentUpdates();
    }
}