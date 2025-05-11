package com.example.moodify;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Surprised extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprised);
        setSystemBarsMerged();
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://music.youtube.com/search?q=surprised+songs+hindi+english");
    }

    @Override
    public void onBackPressed() {
        // Always go to HomePage when back is pressed
        Intent intent = new Intent(Surprised.this, home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
