package com.example.moodify;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class questions extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, phoneEditText, ageEditText, addressEditText;
    RadioGroup genderGroup;
    Button saveButton;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarsMerged();
        setContentView(R.layout.activity_questions);

        firstNameEditText = findViewById(R.id.name);
        lastNameEditText = findViewById(R.id.surname);
        phoneEditText = findViewById(R.id.phone);
        ageEditText = findViewById(R.id.age);
        addressEditText = findViewById(R.id.Address);
        genderGroup = findViewById(R.id.genderGroup);
        saveButton = findViewById(R.id.submitButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });
    }

    private void saveUserData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String address = addressEditText.getText().toString();

            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = selectedGenderButton.getText().toString();

            HashMap<String, Object> userData = new HashMap<>();
            userData.put("firstName", firstName);
            userData.put("lastName", lastName);
            userData.put("phone", phone);
            userData.put("age", age);
            userData.put("address", address);
            userData.put("gender", gender);

            databaseReference.setValue(userData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(questions.this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(questions.this, home.class));
                    finish();
                } else {
                    Toast.makeText(questions.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
}
