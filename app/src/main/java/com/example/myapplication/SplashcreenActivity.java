package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;

public class SplashcreenActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 segundos

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashcreen);

        Log.d("SplashcreenActivity", "Creating notification channel...");
        NotificationHelper.createNotificationChannel(this);
        // Retrasar la apertura de LoginActivity durante 2 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Este código se ejecutará después de SPLASH_DELAY milisegundos

                // Abrir LoginActivity
                Intent intent = new Intent(SplashcreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        }, SPLASH_DELAY);
    }
}