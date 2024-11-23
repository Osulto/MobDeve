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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        // Categories
        String[] categories = {
                "All Categories",
                "Tops",
                "Bottoms",
                "Dresses",
                "Outerwear",
                "Accessories",
                "Footwear"
        };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        categorySpinner.setAdapter(categoryAdapter);

        // Set default selections
        reportTypeSpinner.setText(reportTypes[0], false);
        categorySpinner.setText(categories[0], false);
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

        // Show loading state
        btnGenerate.setEnabled(false);
        btnGenerate.setText("Generating...");

        // TODO: Generate actual report content
        // For now, show sample data
        String reportContent = generateSampleReport(reportType, category);

        // Update UI
        tvReportTitle.setText(reportType);
        tvReportContent.setText(reportContent);
        reportContainer.setVisibility(View.VISIBLE);
        btnExport.setEnabled(true);

        // Reset button
        btnGenerate.setEnabled(true);
        btnGenerate.setText("Generate Report");
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