package com.example.endemik.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorit")
public class FavoriteEntity {
    @PrimaryKey
    @NonNull
    public String endemikId;
    public long createdAt;

    public FavoriteEntity(@NonNull String endemikId, long createdAt) {
        this.endemikId = endemikId;
        this.createdAt = createdAt;
    }
}
