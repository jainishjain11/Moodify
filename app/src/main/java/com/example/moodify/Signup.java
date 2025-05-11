package com.example.moodify;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private EditText email, password, confirmPassword;
    private Button btnSignup;
    private TextView login;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarsMerged();
        FirebaseApp.initializeApp(this); // ✅ Initialize Firebase
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Investor"); // ✅ Save data under "Investor"

        email = findViewById(R.id.email);
        password = findViewById(R.id.otp_verification);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Login.class));
                finish();
            }
        });
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
    private void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserData(userId, userEmail);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Signup.this, "Signup Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", userId);
        userData.put("email", email);

        databaseReference.child(userId).setValue(userData)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup.this, questions.class)); // ✅ Redirect to "invQuestions.java"
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
