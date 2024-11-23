package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initializeViews();
        setupBottomNavigation();
        setupRecyclerView();
        setupSampleData();
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

    private void setupSampleData() {
        // Set sample product details
        productName.setText("Basic White T-Shirt (M)");
        productCategory.setText("Tops");
        productStock.setText("Current Stock: 50");
        etReorderPoint.setText("20");

        // Sample updates
        recentUpdates.add(new Update("Basic White T-Shirt (M)", Update.UpdateType.STOCK_ADDED, 50, "2023-11-20 14:30"));
        recentUpdates.add(new Update("Basic White T-Shirt (M)", Update.UpdateType.STOCK_ADJUSTED, 20, "2023-11-19 09:15"));
        updateAdapter.notifyDataSetChanged();
    }
}