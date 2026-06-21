package com.example.endemik.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EndemikDao {
    @Query("SELECT COUNT(*) FROM endemik")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EndemikEntity> items);

    @Query("SELECT * FROM endemik WHERE tipe = :type ORDER BY nama")
    List<EndemikEntity> getByType(String type);

    @Query("SELECT * FROM endemik WHERE tipe = :type AND asal = :region ORDER BY nama")
    List<EndemikEntity> getByTypeAndRegion(String type, String region);

    @Query("SELECT * FROM endemik WHERE id = :id LIMIT 1")
    EndemikEntity getById(String id);

    @Query("SELECT DISTINCT asal FROM endemik WHERE asal IS NOT NULL AND asal != '' ORDER BY asal")
    List<String> getRegions();

    @Query("SELECT * FROM endemik WHERE nama LIKE :pattern OR namaLatin LIKE :pattern OR asal LIKE :pattern OR status LIKE :pattern OR deskripsi LIKE :pattern ORDER BY nama")
    List<EndemikEntity> search(String pattern);

    @Query("SELECT * FROM endemik WHERE tipe = :type AND (nama LIKE :pattern OR namaLatin LIKE :pattern OR asal LIKE :pattern OR status LIKE :pattern OR deskripsi LIKE :pattern) ORDER BY nama")
    List<EndemikEntity> searchByType(String type, String pattern);
}
