package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;
import com.mobdeve.s19.stocksmart.database.models.User;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etBusinessName, etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DAO and SessionManager
        userDao = ((StockSmartApp) getApplication()).getUserDao();
        sessionManager = SessionManager.getInstance(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etBusinessName = findViewById(R.id.etBusinessName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String businessName = etBusinessName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Basic validation
        if (businessName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        if (userDao.findByUsername(username) != null) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if business exists and get its first user
        User existingBusinessUser = userDao.getFirstUserByBusinessName(businessName);

        // Create new user
        User newUser = new User(businessName, username, password);
        long userId = userDao.insert(newUser);

        if (userId != -1) {
            newUser.setId(userId);

            // Set session
            sessionManager.createLoginSession(newUser);

            // If business already exists, link to its data
            if (existingBusinessUser != null) {
                sessionManager.setBusinessId(existingBusinessUser.getId());  // Use first user's ID as business ID
                Toast.makeText(this, "Joined existing business: " + businessName, Toast.LENGTH_SHORT).show();
            }

            // Go to Home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}