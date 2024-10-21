package com.mobdeve.s19.mobdev;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvCategory, tvCurrentStock;
    private EditText etStockChange, etReorderPoint;
    private Button btnDecreaseStock, btnIncreaseStock, btnUpdateStock, btnUpdateReorderPoint;
    private RecyclerView rvRecentUpdates;
    private ImageView ivOptions;

    private Product product;
    private List<Update> recentUpdates;
    private UpdateAdapter updateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initViews();
        setupListeners();

        // Get product ID from intent
        long productId = getIntent().getLongExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch product details
        product = getProductById(productId);

        if (product != null) {
            setProductDetails();
            setupRecentUpdates();
        }
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvCategory = findViewById(R.id.tvCategory);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        etStockChange = findViewById(R.id.etStockChange);
        etReorderPoint = findViewById(R.id.etReorderPoint);
        btnDecreaseStock = findViewById(R.id.btnDecreaseStock);
        btnIncreaseStock = findViewById(R.id.btnIncreaseStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);
        btnUpdateReorderPoint = findViewById(R.id.btnUpdateReorderPoint);
        rvRecentUpdates = findViewById(R.id.rvRecentUpdates);
        ivOptions = findViewById(R.id.ivOptions);
    }

    private void setupListeners() {
        btnDecreaseStock.setOnClickListener(v -> decreaseStock());
        btnIncreaseStock.setOnClickListener(v -> increaseStock());
        btnUpdateStock.setOnClickListener(v -> updateStock());
        btnUpdateReorderPoint.setOnClickListener(v -> updateReorderPoint());
        ivOptions.setOnClickListener(v -> showOptionsMenu());
    }

    private void setProductDetails() {
        tvProductName.setText(product.getName());
        tvCategory.setText("Category: " + product.getCategory());
        tvCurrentStock.setText("Current Stock: " + product.getStockCount());
        etReorderPoint.setText(String.valueOf(product.getReorderPoint()));

        // Load product image
        ivProductImage.setImageResource(R.drawable.shirt);
    }

    private void setupRecentUpdates() {
        recentUpdates = getRecentUpdates(); // Replace with your data fetching logic
        updateAdapter = new UpdateAdapter(recentUpdates);
        rvRecentUpdates.setLayoutManager(new LinearLayoutManager(this));
        rvRecentUpdates.setAdapter(updateAdapter);
    }

    private void decreaseStock() {
        int currentValue = Integer.parseInt(etStockChange.getText().toString());
        if (currentValue > 1) {
            etStockChange.setText(String.valueOf(currentValue - 1));
        }
    }

    private void increaseStock() {
        int currentValue = Integer.parseInt(etStockChange.getText().toString());
        etStockChange.setText(String.valueOf(currentValue + 1));
    }

    private void updateStock() {
        int changeAmount = Integer.parseInt(etStockChange.getText().toString());
        product.setStockCount(product.getStockCount() + changeAmount);
        tvCurrentStock.setText("Current Stock: " + product.getStockCount());

        // Add update to recent updates
        recentUpdates.add(0, new Update("Stock Updated", "Stock changed by " + changeAmount, "Just now"));
        updateAdapter.notifyItemInserted(0);

        Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
    }

    private void updateReorderPoint() {
        int newReorderPoint = Integer.parseInt(etReorderPoint.getText().toString());
        product.setReorderPoint(newReorderPoint);

        // Add update to recent updates
        recentUpdates.add(0, new Update("Reorder Point Updated", "New reorder point: " + newReorderPoint, "Just now"));
        updateAdapter.notifyItemInserted(0);

        Toast.makeText(this, "Reorder point updated", Toast.LENGTH_SHORT).show();
    }

    private void showOptionsMenu() {
        PopupMenu popup = new PopupMenu(this, ivOptions);
        popup.getMenuInflater().inflate(R.menu.product_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_edit) {
                    // Handle edit action
                    Toast.makeText(ProductDetailsActivity.this, "Edit product", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    // Handle delete action
                    Toast.makeText(ProductDetailsActivity.this, "Delete product", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }
    private Product getProductById(long id) {
        return new Product(id, "Sample Product", "Category", 100, "2023-10-21", 20);
    }

    private List<Update> getRecentUpdates() {
        List<Update> updates = new ArrayList<>();
        updates.add(new Update("Stock Added", "50 items added", "2023-10-21"));
        updates.add(new Update("Reorder Point Changed", "Changed to 20", "2023-10-20"));
        return updates;
    }
}