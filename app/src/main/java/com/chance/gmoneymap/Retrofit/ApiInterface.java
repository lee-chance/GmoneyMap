package com.chance.gmoneymap.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    //지역명으로 검색
    @GET("RegionMnyFacltStus?")
    Call<GmoneyClass> getSearchCity(
            @Query("KEY") String key,
            @Query("Type") String Type,
            @Query("pIndex") int pIndex,
            @Query("SIGUN_NM") String city
    );
}
