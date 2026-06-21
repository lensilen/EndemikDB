package com.example.endemik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.endemik.data.AppDatabase;
import com.example.endemik.data.EndemikEntity;
import com.example.endemik.ui.EndemikAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EndemikAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ImageButton backButton = findViewById(R.id.backButton);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);

        adapter = new EndemikAdapter(item -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ID, item.id);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void loadFavorites() {
        executor.execute(() -> {
            List<EndemikEntity> items = AppDatabase.getInstance(this).favoriteDao().getFavoriteItems();
            runOnUiThread(() -> {
                adapter.submitList(items);
                emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
