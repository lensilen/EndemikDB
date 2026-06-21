package com.example.endemik.network;

import com.example.endemik.data.EndemikEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EndemikApi {
    @GET("data_endemik/endemik.json")
    Call<List<EndemikEntity>> getEndemik();
}
