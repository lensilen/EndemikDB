package com.example.endemik;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.endemik.data.AppDatabase;
import com.example.endemik.data.EndemikEntity;
import com.example.endemik.data.FavoriteEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "extra_id";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ImageButton favoriteButton;
    private EndemikEntity item;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageButton backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        backButton.setOnClickListener(v -> finish());
        favoriteButton.setOnClickListener(v -> toggleFavorite());

        String id = getIntent().getStringExtra(EXTRA_ID);
        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadDetail(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void loadDetail(String id) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            item = db.endemikDao().getById(id);
            isFavorite = db.favoriteDao().isFavorite(id) > 0;
            runOnUiThread(() -> {
                if (item == null) {
                    Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                bindDetail();
            });
        });
    }

    private void bindDetail() {
        TextView titleText = findViewById(R.id.titleText);
        TextView nameText = findViewById(R.id.nameText);
        TextView latinText = findViewById(R.id.latinText);
        TextView metaText = findViewById(R.id.metaText);
        TextView descriptionText = findViewById(R.id.descriptionText);
        TextView sourceText = findViewById(R.id.sourceText);
        ImageView detailImage = findViewById(R.id.detailImage);

        titleText.setText(nonEmpty(item.nama, "Judul"));
        nameText.setText(nonEmpty(item.nama, "-"));
        latinText.setText(nonEmpty(item.namaLatin, ""));
        metaText.setText(
                "Kategori: " + nonEmpty(item.tipe, "-") +
                        "\nAsal: " + nonEmpty(item.asal, "-") +
                        "\nStatus: " + nonEmpty(item.status, "-") +
                        "\nFamili: " + nonEmpty(item.famili, "-") +
                        "\nGenus: " + nonEmpty(item.genus, "-") +
                        "\nSebaran: " + nonEmpty(item.sebaran, "-")
        );
        descriptionText.setText(nonEmpty(item.deskripsi, "-"));
        sourceText.setText(
                "Sumber foto: " + nonEmpty(item.sumberFoto, "-") +
                        "\nVideo: " + nonEmpty(item.vidio, "-")
        );
        Glide.with(this)
                .load(item.foto)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(detailImage);
        updateFavoriteIcon();
    }

    private void toggleFavorite() {
        if (item == null) {
            return;
        }
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            if (isFavorite) {
                db.favoriteDao().deleteById(item.id);
                isFavorite = false;
            } else {
                db.favoriteDao().insert(new FavoriteEntity(item.id, System.currentTimeMillis()));
                isFavorite = true;
            }
            runOnUiThread(() -> {
                updateFavoriteIcon();
                Toast.makeText(
                        this,
                        isFavorite ? "Disimpan ke favorit" : "Dihapus dari favorit",
                        Toast.LENGTH_SHORT
                ).show();
            });
        });
    }

    private void updateFavoriteIcon() {
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
