package com.stepup.retrofit2;

import com.stepup.model.Banner;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("banner")
    Call<List<Banner>> getBannerAll();
}
