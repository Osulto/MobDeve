package com.mobdeve.s19.stocksmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobdeve.s19.stocksmart.database.dao.UserDao;
import com.mobdeve.s19.stocksmart.database.models.User;
import com.mobdeve.s19.stocksmart.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etBusinessName, etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private UserDao userDao;
    private SessionManager sessionManager;
    private FirebaseAuthManager firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = ((StockSmartApp) getApplication()).getUserDao();
        sessionManager = SessionManager.getInstance(this);
        firebaseAuth = new FirebaseAuthManager(this);

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

        if (businessName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDao.findByUsername(username) != null) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        firebaseAuth.registerUser(username, password, businessName, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String firebaseUserId) {
                User existingBusinessUser = userDao.getFirstUserByBusinessName(businessName);

                // Create new user locally
                User newUser = new User(businessName, username, password);
                long localUserId = userDao.insert(newUser);
                newUser.setId(localUserId);

                // Create session
                sessionManager.createLoginSession(firebaseUserId, username);

                if (existingBusinessUser != null) {
                    sessionManager.setBusinessId(existingBusinessUser.getId());
                    Toast.makeText(RegisterActivity.this, "Joined existing business: " + businessName, Toast.LENGTH_SHORT).show();
                } else {
                    sessionManager.setBusinessId(localUserId);
                    // Initialize sample data for new business
                    ((StockSmartApp) getApplication()).initializeSampleData();
                }

                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    btnRegister.setEnabled(true);
                });
            }
        });
    }
    }