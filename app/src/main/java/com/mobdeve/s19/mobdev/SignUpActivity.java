package com.mobdeve.s19.mobdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you would normally validate and create the account
                // For this prototype, we'll just check if fields are not empty and passwords match
                if (!etUsername.getText().toString().isEmpty()
                        && !etPassword.getText().toString().isEmpty()
                        && etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // This prevents going back to the sign-up screen when pressing back from the home screen
                } else {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields and make sure passwords match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}