package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private EditText searchEditText;
    private FloatingActionButton fabAddProduct;
    private BottomNavigationView bottomNavigation;
    private TextView categoryTitle;
    private ChipGroup filterChipGroup;
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private long categoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        // Initialize DAOs
        productDao = new ProductDao(this);
        categoryDao = new CategoryDao(this);

        // Get category info from intent
        String categoryName = getIntent().getStringExtra("category_name");
        if (categoryName != null) {
            Category category = categoryDao.getByName(categoryName);
            if (category != null) {
                categoryId = category.getId();
            }
        }

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupBottomNavigation();
        setupFab();
        setupFilters();

        // Set category title
        categoryTitle.setText(categoryName != null ? categoryName : "All Products");

        // Load products
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(); // Refresh products when returning to this screen
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.productsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        categoryTitle = findViewById(R.id.categoryTitle);
        filterChipGroup = findViewById(R.id.filterChipGroup);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
    }

    private void setupFilters() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                loadProducts();
                return;
            }

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipInStock) {
                filterInStock();
            } else if (checkedId == R.id.chipLowStock) {
                filterLowStock();
            } else if (checkedId == R.id.chipPrice) {
                sortByPrice();
            } else if (checkedId == R.id.chipNewest) {
                sortByDate();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_products);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (itemId == R.id.navigation_products) {
                return true;
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

    private void setupFab() {
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductActivity.class);
            if (categoryId != -1) {
                intent.putExtra("category_id", categoryId);
            }
            startActivity(intent);
        });
    }

    private void loadProducts() {
        try {
            List<Product> products;
            if (categoryId != -1) {
                products = productDao.getByCategory(categoryId);
            } else {
                products = productDao.getAll();
            }
            adapter.setProducts(products);
        } catch (Exception e) {
            showError("Failed to load products", e);
        }
    }

    private void filterInStock() {
        try {
            List<Product> products = productDao.getByCategory(categoryId);
            adapter.setProducts(products);
            adapter.filterInStock();
        } catch (Exception e) {
            showError("Failed to filter products", e);
        }
    }

    private void filterLowStock() {
        try {
            List<Product> products = productDao.getLowStockProducts();
            if (categoryId != -1) {
                products = products.stream()
                        .filter(p -> p.getCategoryId() == categoryId)
                        .collect(java.util.stream.Collectors.toList());
            }
            adapter.setProducts(products);
        } catch (Exception e) {
            showError("Failed to filter products", e);
        }
    }

    private void sortByPrice() {
        adapter.sortByPrice();
    }

    private void sortByDate() {
        adapter.sortByDate();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("product_id", product.getId());
        if (categoryId != -1) {
            intent.putExtra("category_id", categoryId);
        }
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Product product) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        if (productDao.delete(product.getId())) {
                            adapter.removeProduct(product);
                        } else {
                            showError("Failed to delete product", null);
                        }
                    } catch (Exception e) {
                        showError("Failed to delete product", e);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showError(String message, Exception e) {
        String errorMessage = e != null ? message + ": " + e.getMessage() : message;
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }
}