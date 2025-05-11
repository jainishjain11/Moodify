package com.example.moodify;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText email, password;
    private Button btnLogin;
    private TextView signup, forgotPassword;
    private CheckBox showPasswordCheckbox;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarsMerged();
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);
        progressBar = findViewById(R.id.progressBar);

        // Show Password Functionality
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            password.setSelection(password.length()); // Keeps cursor at the end
        });

        // Login Button Click
        btnLogin.setOnClickListener(v -> loginUser());

        // Signup Redirect
        signup.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Signup.class));
        });

        // Forgot Password Redirect

    }
    private void setSystemBarsMerged() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // Extend layout behind status and navigation bars
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );

            // Set the status and navigation bar colors to match the app background
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                // Light theme: white background, black text/icons
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                // Dark theme: black background, white text/icons
                window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
                window.setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));

                // Ensure white text/icons for dark background
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.getDecorView().setSystemUiVisibility(0); // No LIGHT_STATUS_BAR flag
                }
            }
        }
    }
    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, home1.class)); // ✅ Redirect to Investor Dashboard
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Login Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
