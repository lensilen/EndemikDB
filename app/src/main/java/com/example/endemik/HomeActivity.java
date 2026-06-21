package com.example.endemik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.endemik.data.AppDatabase;
import com.example.endemik.ui.CategoryFragment;
import com.example.endemik.ui.HewanFragment;
import com.example.endemik.ui.TumbuhanFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final String TYPE_HEWAN = "Hewan";
    private static final String TYPE_TUMBUHAN = "Tumbuhan";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentType = TYPE_HEWAN;
    private String currentRegion;
    private CategoryFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentRegion = getString(R.string.all_regions);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton themeButton = findViewById(R.id.themeButton);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        profileButton.setOnClickListener(v -> showProfileDialog());
        themeButton.setOnClickListener(v -> ThemeManager.toggleTheme(this));
        searchButton.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        favoriteButton.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_tumbuhan) {
                currentType = TYPE_TUMBUHAN;
            } else {
                currentType = TYPE_HEWAN;
            }
            showFragment();
            return true;
        });

        setupRegions();
        showFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void setupRegions() {
        Spinner spinner = findViewById(R.id.regionSpinner);
        executor.execute(() -> {
            List<String> regions = new ArrayList<>();
            regions.add(getString(R.string.all_regions));
            regions.addAll(AppDatabase.getInstance(this).endemikDao().getRegions());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        regions
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentRegion = regions.get(position);
                        if (currentFragment != null) {
                            currentFragment.setRegion(currentRegion);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            });
        });
    }

    private void showFragment() {
        currentFragment = TYPE_TUMBUHAN.equals(currentType) ? new TumbuhanFragment() : new HewanFragment();
        currentFragment.setRegion(currentRegion);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, currentFragment)
                .commit();
    }

    private void showProfileDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_profile, null, false);
        new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setPositiveButton("Tutup", null)
                .show();
    }
}
