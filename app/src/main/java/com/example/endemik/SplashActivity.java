package com.example.endemik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.endemik.data.AppDatabase;
import com.example.endemik.data.EndemikEntity;
import com.example.endemik.network.ApiClient;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView statusText;
    private ProgressBar progressBar;
    private MaterialButton retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        statusText = findViewById(R.id.statusText);
        progressBar = findViewById(R.id.progressBar);
        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> prepareData());

        prepareData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void prepareData() {
        progressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        statusText.setText("Menyiapkan data endemik...");

        executor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                if (db.endemikDao().count() == 0) {
                    runOnUiThread(() -> statusText.setText("Mengambil data dari API..."));
                    Response<List<EndemikEntity>> response = ApiClient.getApi().getEndemik().execute();
                    if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                        throw new IOException("Data API tidak tersedia");
                    }
                    db.endemikDao().insertAll(response.body());
                }
                openHome();
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    retryButton.setVisibility(View.VISIBLE);
                    statusText.setText("Gagal menyiapkan data. Periksa koneksi internet lalu coba lagi.");
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openHome() {
        runOnUiThread(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}
