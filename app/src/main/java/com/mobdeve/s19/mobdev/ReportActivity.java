package com.mobdeve.s19.mobdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);  // This links to activity_report.xml

        // ImageButton redirection to HomeActivity from ReportActivity
        ImageButton homeIcon = findViewById(R.id.home_icon);
        homeIcon.setOnClickListener(v -> {
            finish();
        });
    }
}
