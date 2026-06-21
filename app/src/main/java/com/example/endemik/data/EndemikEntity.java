package com.example.endemik.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "endemik")
public class EndemikEntity {
    @PrimaryKey
    @NonNull
    public String id = "";

    public String tipe;
    public String nama;

    @SerializedName("nama_latin")
    public String namaLatin;

    public String famili;
    public String genus;
    public String deskripsi;
    public String asal;
    public String sebaran;
    public String foto;

    @SerializedName("sumber_foto")
    public String sumberFoto;

    public String vidio;

    @SerializedName("sumber_vidio")
    public String sumberVidio;

    public String status;
}
