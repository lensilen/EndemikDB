package com.example.endemik.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteEntity favorite);

    @Query("DELETE FROM favorit WHERE endemikId = :endemikId")
    void deleteById(String endemikId);

    @Query("SELECT COUNT(*) FROM favorit WHERE endemikId = :endemikId")
    int isFavorite(String endemikId);

    @Query("SELECT endemik.* FROM endemik INNER JOIN favorit ON endemik.id = favorit.endemikId ORDER BY favorit.createdAt DESC")
    List<EndemikEntity> getFavoriteItems();
}
