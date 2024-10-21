package com.mobdeve.s19.mobdev;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of products and set the adapter
        productList = new ArrayList<>();
        productList.add(new Product("WOW", 101, "2024-10-20", ""));
        productList.add(new Product("Amazing Product", 50, "2024-10-18", ""));
        productList.add(new Product("Another Item", 75, "2024-10-15", ""));

        productAdapter = new ProductAdapter(this, productList);
        recyclerViewProducts.setAdapter(productAdapter);
    }
}
