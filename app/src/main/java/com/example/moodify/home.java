package com.example.moodify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;


import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class home extends AppCompatActivity {

    ImageButton profileButton;
    TextView stayFitText;
    FirebaseUser currentUser;

    DatabaseReference databaseReference;
    Button checkButton, happyButton, sadButton, angryButton, excitedButton, NeutralButton, sleepyButton, confusedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarsMerged();
        loadUserName();
        setContentView(R.layout.activity_home);

        // Link views with their IDs
        profileButton = findViewById(R.id.profile);
        checkButton = findViewById(R.id.check_button);
        happyButton = findViewById(R.id.Happy_Button);
        sadButton = findViewById(R.id.Sad_Button);
        angryButton = findViewById(R.id.Angry_Button);
        excitedButton = findViewById(R.id.Surprised_button);
        NeutralButton = findViewById(R.id.Neutral_Button);
        confusedButton = findViewById(R.id.Confused_Button);
        sleepyButton = findViewById(R.id.Sleepy_Button);

        ImageView profileIcon = findViewById(R.id.profile);
        ImageView homeIcon = findViewById(R.id.home);
        ImageView eventsIcon = findViewById(R.id.events);
        ImageView settingsIcon = findViewById(R.id.setting);
        stayFitText = findViewById(R.id.name);

        // Profile button click
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Profile.class));
            }
        });

        // Check Mood button click
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, home1.class));
            }
        });

        // Happy Button
        happyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Happy.class));
            }
        });
        confusedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Confused.class));
            }
        });
        sleepyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Sleepy.class));
            }
        });
        NeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Neutral.class));
            }
        });

        // Sad Button
        sadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Sad.class));
            }
        });

        // Angry Button
        angryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Angry.class));
            }
        });

        // Surprised Button (Excited Button)
        excitedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Surprised.class));
            }
        });

        // Bottom Navigation Icons
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(home.this, "You are already on the Home Page", Toast.LENGTH_SHORT).show();
            }
        });


        eventsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, home1.class));
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, Settings.class));
            }
        });
    }

    private void loadUserName() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        if (firstName != null) {
                            stayFitText.setText("  "+ firstName + " !");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(home.this, "Failed to load name", Toast.LENGTH_SHORT).show();
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
