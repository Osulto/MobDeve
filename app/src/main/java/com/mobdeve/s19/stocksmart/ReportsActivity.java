package com.mobdeve.s19.stocksmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private AutoCompleteTextView reportTypeSpinner;
    private AutoCompleteTextView categorySpinner;
    private TextInputEditText etFromDate, etToDate;
    private TextInputEditText etMinStock, etMaxStock;
    private MaterialButton btnGenerate, btnExport;
    private CardView reportContainer;
    private TextView tvReportTitle, tvReportContent;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        initializeViews();
        setupDropdowns();
        setupDatePickers();
        setupButtons();
        setupBottomNavigation();
    }

    private void initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        reportTypeSpinner = findViewById(R.id.reportRangeSpinner);
        categorySpinner = findViewById(R.id.productCategorySpinner);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        etMinStock = findViewById(R.id.etMinStockCount);
        etMaxStock = findViewById(R.id.etMaxStockCount);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnExport = findViewById(R.id.btnExport);
        reportContainer = findViewById(R.id.reportContainer);
        tvReportTitle = findViewById(R.id.tvReportTitle);
        tvReportContent = findViewById(R.id.tvReportContent);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        categoryDao = new CategoryDao(this); // Initialize DAO

        // Initially hide the report container and export button
        reportContainer.setVisibility(View.GONE);
        btnExport.setEnabled(false);
    }

    private void setupDropdowns() {
        // Report Types
        String[] reportTypes = {
                "Inventory Summary",
                "Low Stock Report",
                "Stock Movement Report",
                "Category Analysis",
                "Value Report",
                "Trend Analysis"
        };
        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reportTypes
        );
        reportTypeSpinner.setAdapter(reportAdapter);

        // Populate category spinner dynamically
        populateCategorySpinner();

        // Set default selections
        reportTypeSpinner.setText(reportTypes[0], false);
    }

    private void populateCategorySpinner() {
        List<com.mobdeve.s19.stocksmart.database.models.Category> categories = categoryDao.getAll(); // Fetch all categories from the database

        // Convert categories to a list of names for the spinner
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All Categories"); // Default option
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        // Set up the spinner with the category names
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );
        categorySpinner.setAdapter(categoryAdapter);

        // Set default selection
        categorySpinner.setText(categoryNames.get(0), false);
    }


    private void setupDatePickers() {
        etFromDate.setOnClickListener(v -> showDatePicker(etFromDate));
        etToDate.setOnClickListener(v -> showDatePicker(etToDate));

        // Set default dates (current month)
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        etFromDate.setText(dateFormat.format(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        etToDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showDatePicker(TextInputEditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupButtons() {
        btnGenerate.setOnClickListener(v -> generateReport());
        btnExport.setOnClickListener(v -> exportReport());
    }

    private void generateReport() {
        String reportType = reportTypeSpinner.getText().toString();
        String category = categorySpinner.getText().toString();
        String fromDate = etFromDate.getText().toString();
        String toDate = etToDate.getText().toString();
        String minStockStr = etMinStock.getText().toString().trim();
        String maxStockStr = etMaxStock.getText().toString().trim();

        int minStock = minStockStr.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minStockStr);
        int maxStock = maxStockStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxStockStr);

        btnGenerate.setEnabled(false);
        btnGenerate.setText("Generating...");

        String reportContent = generateDynamicReport(reportType, category, fromDate, toDate, minStock, maxStock);

        // Update UI
        tvReportTitle.setText(reportType);
        tvReportContent.setText(reportContent);
        reportContainer.setVisibility(View.VISIBLE);
        btnExport.setEnabled(true);

        btnGenerate.setEnabled(true);
        btnGenerate.setText("Generate Report");
    }

    private String generateDynamicReport(String reportType, String category, String fromDate, String toDate, int minStock, int maxStock) {
        StringBuilder report = new StringBuilder();

        report.append("Report Type: ").append(reportType).append("\n");
        report.append("Category: ").append(category).append("\n");
        report.append("Date Range: ").append(fromDate).append(" to ").append(toDate).append("\n\n");

        ProductDao productDao = new ProductDao(this);

        try {
            List<Product> products;
            if (category.equals("All Categories")) {
                products = productDao.getAll(); // Fetch all products
            } else {
                CategoryDao categoryDao = new CategoryDao(this);
                Category selectedCategory = categoryDao.getByName(category);
                if (selectedCategory == null) {
                    report.append("No products found for this category.\n");
                    return report.toString();
                }
                products = productDao.getByCategory(selectedCategory.getId());
            }

            // Filter products based on stock range and date range
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.getQuantity() >= minStock && product.getQuantity() <= maxStock) {
                    filteredProducts.add(product);
                }
            }

            // Generate report content based on report type
            switch (reportType) {
                case "Inventory Summary":
                    report.append("Total Products: ").append(filteredProducts.size()).append("\n");
                    int totalStock = 0;
                    for (Product product : filteredProducts) {
                        totalStock += product.getQuantity();
                    }
                    report.append("Total Stock: ").append(totalStock).append("\n");
                    break;

                case "Low Stock Report":
                    report.append("Products Below Threshold:\n");
                    for (Product product : filteredProducts) {
                        if (product.getQuantity() <= product.getReorderPoint()) {
                            report.append("- ").append(product.getName()).append(": ").append(product.getQuantity()).append(" left\n");
                        }
                    }
                    break;

                case "Stock Movement Report":
                    // For demonstration purposes, show static data for stock movement
                    report.append("Stock Movement:\n");
                    report.append("- ").append("Basic White T-Shirt (M)").append(": Added 50 on 2023-11-20\n");
                    report.append("- ").append("Denim Jeans (S)").append(": Removed 20 on 2023-11-19\n");
                    break;

                case "Category Analysis":
                    report.append("Products in ").append(category).append(":\n");
                    for (Product product : filteredProducts) {
                        report.append("- ").append(product.getName()).append(": ").append(product.getQuantity()).append(" in stock\n");
                    }
                    break;

                case "Value Report":
                    report.append("Total Value of Products:\n");
                    double totalValue = 0;
                    for (Product product : filteredProducts) {
                        totalValue += product.getQuantity() * product.getSellingPrice();
                    }
                    report.append("Total Value: ₱").append(String.format(Locale.getDefault(), "%.2f", totalValue)).append("\n");
                    break;

                case "Trend Analysis":
                    report.append("Trends (Static Data):\n");
                    report.append("- Most Sold: Basic White T-Shirt\n");
                    report.append("- Least Sold: Summer Dress\n");
                    break;

                default:
                    report.append("Invalid report type.\n");
                    break;
            }
        } catch (Exception e) {
            report.append("Error generating report: ").append(e.getMessage()).append("\n");
        }

        return report.toString();
    }



    private String generateSampleReport(String reportType, String category) {
        StringBuilder report = new StringBuilder();
        report.append("Report Type: ").append(reportType).append("\n");
        report.append("Category: ").append(category).append("\n");
        report.append("Date Range: ").append(etFromDate.getText()).append(" to ")
                .append(etToDate.getText()).append("\n\n");

        // Add sample data based on report type
        switch (reportType) {
            case "Inventory Summary":
                report.append("Total Items: 485\n");
                report.append("Total Categories: 6\n");
                report.append("Total Value: ₱285,000.00\n");
                report.append("Average Items per Category: 80.83");
                break;
            case "Low Stock Report":
                report.append("Items Below Threshold: 12\n\n");
                report.append("- Basic White T-Shirt (M): 5 left\n");
                report.append("- Denim Jeans (S): 3 left\n");
                report.append("- Summer Dress: 4 left");
                break;
            default:
                report.append("Sample report content for ").append(reportType);
                break;
        }

        return report.toString();
    }

    private void exportReport() {
        // TODO: Implement actual CSV export
        btnExport.setEnabled(false);
        btnExport.setText("Exporting...");

        // Simulate export delay
        btnExport.postDelayed(() -> {
            btnExport.setText("Export as CSV");
            btnExport.setEnabled(true);
        }, 1500);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_reports);
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
                return true;
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