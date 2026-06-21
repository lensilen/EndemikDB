package com.example.endemik;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.endemik.data.AppDatabase;
import com.example.endemik.data.EndemikEntity;
import com.example.endemik.ui.EndemikAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EndemikAdapter adapter;
    private EditText searchEditText;
    private TextView emptyText;
    private String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        selectedType = getString(R.string.all_types);
        searchEditText = findViewById(R.id.searchEditText);
        emptyText = findViewById(R.id.emptyText);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton clearButton = findViewById(R.id.clearButton);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new EndemikAdapter(item -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ID, item.id);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
        clearButton.setOnClickListener(v -> searchEditText.setText(""));
        favoriteButton.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));

        List<String> types = Arrays.asList(getString(R.string.all_types), "Hewan", "Tumbuhan");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = types.get(position);
                loadResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadResults();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadResults();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void loadResults() {
        String query = searchEditText == null ? "" : searchEditText.getText().toString();
        String type = selectedType;
        String pattern = "%" + query.trim() + "%";
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<EndemikEntity> items;
            if (getString(R.string.all_types).equals(type)) {
                items = db.endemikDao().search(pattern);
            } else {
                items = db.endemikDao().searchByType(type, pattern);
            }
            runOnUiThread(() -> {
                adapter.submitList(items);
                emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
