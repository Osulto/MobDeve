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

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private UserDao userDao;
    private SessionManager sessionManager;
    private FirebaseAuthManager firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        userDao = ((StockSmartApp) getApplication()).getUserDao();
        firebaseAuth = new FirebaseAuthManager(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        firebaseAuth.loginUser(username, password, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String firebaseUserId) {
                // Get local user data
                User user = userDao.findByUsername(username);
                if (user == null) {
                    // First time login on this device
                    user = new User(username, username, password);  // Using username as business name temporarily
                    long localUserId = userDao.insert(user);
                    user.setId(localUserId);
                }

                // Create session
                sessionManager.createLoginSession(firebaseUserId, username);
                sessionManager.setBusinessId(user.getId());

                // Initialize sample data for new business
                ((StockSmartApp) getApplication()).initializeSampleData();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                });
            }
        });
    }
}