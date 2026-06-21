package com.example.endemik.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.endemik.R;
import com.example.endemik.data.EndemikEntity;

import java.util.ArrayList;
import java.util.List;

public class EndemikAdapter extends RecyclerView.Adapter<EndemikAdapter.EndemikViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(EndemikEntity item);
    }

    private final List<EndemikEntity> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public EndemikAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<EndemikEntity> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EndemikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_endemik, parent, false);
        return new EndemikViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EndemikViewHolder holder, int position) {
        EndemikEntity item = items.get(position);
        holder.title.setText(nonEmpty(item.nama, "Judul Gambar"));
        Glide.with(holder.image.getContext())
                .load(item.foto)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    static class EndemikViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        EndemikViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            title = itemView.findViewById(R.id.itemTitle);
        }
    }
}
