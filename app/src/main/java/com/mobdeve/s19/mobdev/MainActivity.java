package com.mobdeve.s19.mobdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageButton homeIcon, addIcon, productsIcon, notificationsIcon, searchIcon, settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeIcon = findViewById(R.id.home_icon);
        addIcon = findViewById(R.id.add_icon);
        productsIcon = findViewById(R.id.products_icon);
        notificationsIcon = findViewById(R.id.notifications_icon);
        searchIcon = findViewById(R.id.search_icon);
        settingsIcon = findViewById(R.id.settings_icon);

        homeIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HomeActivity.class)));
        addIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddActivity.class)));
        productsIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProductsActivity.class)));
        notificationsIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReportActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        settingsIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
}
