package com.example.moodify;

import android.Manifest;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class home1 extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private PreviewView previewView;
    private TextView emotionText;
    private ProgressBar progressBar;
    private ExecutorService cameraExecutor;
    private ImageButton homeButton, settingsButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarsMerged();
        setContentView(R.layout.activity_home1);

        previewView = findViewById(R.id.view_finder);
        emotionText = findViewById(R.id.emotion_text);
        progressBar = findViewById(R.id.progress_bar);

        homeButton = findViewById(R.id.home);
        profileButton = findViewById(R.id.profile);

        settingsButton = findViewById(R.id.setting);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(home1.this, "You are already on the Detection page", Toast.LENGTH_SHORT).show();
            }
        });



        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home1.this, Settings.class));
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home1.this, home.class));
            }
        });


        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::detectFace);

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void detectFace(ImageProxy imageProxy) {
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);
        showLoadingIndicator(true);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    runOnUiThread(() -> showLoadingIndicator(false));
                    if (faces.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast toast = Toast.makeText(this, "No face detected. Please try again.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0); // <-- Center the Toast
                            toast.show();
                        });
                        imageProxy.close();
                        return;
                    }


                    Face face = faces.get(0);
                    float smileProb = (face.getSmilingProbability() != null) ? face.getSmilingProbability() : -1;
                    Float leftEyeOpenProb = face.getLeftEyeOpenProbability();
                    Float rightEyeOpenProb = face.getRightEyeOpenProbability();
                    Float headEulerAngleY = face.getHeadEulerAngleY();

                    final String detectedEmotion;
                    final Class<?> nextActivity;

                    if (headEulerAngleY != null && Math.abs(headEulerAngleY) > 20) {
                        detectedEmotion = "Angry 😡";
                        nextActivity = Angry.class;
                    } else if (smileProb > 0.7) {
                        detectedEmotion = "Happy 😊";
                        nextActivity = Happy.class;
                    } else if (smileProb < 0.3) {
                        if (leftEyeOpenProb != null && rightEyeOpenProb != null) {
                            if (leftEyeOpenProb < 0.3 && rightEyeOpenProb < 0.3) {
                                detectedEmotion = "Sleepy 😴";
                                nextActivity = Sleepy.class;
                            } else {
                                detectedEmotion = "Sad 😔";
                                nextActivity = Sad.class;
                            }
                        } else {
                            detectedEmotion = "Sad 😔";
                            nextActivity = Sad.class;
                        }
                    } else if (smileProb > 0.3 && smileProb < 0.7) {
                        detectedEmotion = "Confused 🤔";
                        nextActivity = Confused.class;
                    } else if (leftEyeOpenProb != null && rightEyeOpenProb != null && leftEyeOpenProb > 0.8 && rightEyeOpenProb > 0.8) {
                        detectedEmotion = "Surprised 😲";
                        nextActivity = Surprised.class;
                    } else {
                        detectedEmotion = "Neutral 😐";
                        nextActivity = Neutral.class;
                    }

                    runOnUiThread(() -> {
                        emotionText.setText(detectedEmotion);
                        showSnackbar("Detected: " + detectedEmotion);
                        new Handler().postDelayed(() -> navigateToNextPage(nextActivity), 1000);
                    });

                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        showLoadingIndicator(false);
                        Log.e("FaceDetection", "Error detecting face", e);
                        showSnackbar("Face detection failed. Try again.");
                    });
                    imageProxy.close();
                });
    }

    private void showSnackbar(String message) {
        runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show());
    }

    private void showLoadingIndicator(boolean show) {
        runOnUiThread(() -> progressBar.setVisibility(show ? View.VISIBLE : View.GONE));
    }

    private void navigateToNextPage(Class<?> activityClass) {
        Intent intent = new Intent(home1.this, activityClass);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
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