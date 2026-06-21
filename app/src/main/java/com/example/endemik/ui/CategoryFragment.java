package com.example.endemik.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.endemik.DetailActivity;
import com.example.endemik.R;
import com.example.endemik.data.AppDatabase;
import com.example.endemik.data.EndemikEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CategoryFragment extends Fragment {
    private static final String ALL_REGIONS = "Semua region";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EndemikAdapter adapter;
    private TextView emptyText;
    private String region = ALL_REGIONS;

    protected abstract String getType();

    public void setRegion(String region) {
        this.region = region;
        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        emptyText = view.findViewById(R.id.emptyText);
        adapter = new EndemikAdapter(item -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ID, item.id);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);
        loadData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void loadData() {
        if (!isAdded() || adapter == null) {
            return;
        }

        String selectedRegion = region;
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            List<EndemikEntity> items;
            if (selectedRegion == null || ALL_REGIONS.equals(selectedRegion)) {
                items = db.endemikDao().getByType(getType());
            } else {
                items = db.endemikDao().getByTypeAndRegion(getType(), selectedRegion);
            }

            if (!isAdded()) {
                return;
            }
            requireActivity().runOnUiThread(() -> {
                adapter.submitList(items);
                emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
