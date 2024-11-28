package com.mobdeve.s19.stocksmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.dao.UpdateDao;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.database.models.Update;


import java.util.ArrayList;
import java.util.List;

public class UpdatesHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UpdateAdapter adapter;
    private List<Update> updates;
    private UpdateDao updateDao;
    private ProductDao productDao;
    private long productId = -1;  // -1 means show all updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates_history);

        // Get productId if passed
        productId = getIntent().getLongExtra("product_id", -1);

        updateDao = new UpdateDao(this);
        productDao = new ProductDao(this);

        setupRecyclerView();
        loadUpdates();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.updatesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updates = new ArrayList<>();
        adapter = new UpdateAdapter(updates);
        recyclerView.setAdapter(adapter);
    }

    private void loadUpdates() {
        List<Update> allUpdates = updateDao.getAll();
        updates.clear();

        for (Update update : allUpdates) {
            if (productId == -1 || update.getProductId() == productId) {
                Product product = productDao.get(update.getProductId());
                if (product != null) {
                    update.setProductName(product.getName());
                    updates.add(update);
                }
            }
        }

        // Sort updates by date (newest first)
        updates.sort((u1, u2) -> u2.getTimestamp().compareTo(u1.getTimestamp()));
        adapter.notifyDataSetChanged();
    }
}