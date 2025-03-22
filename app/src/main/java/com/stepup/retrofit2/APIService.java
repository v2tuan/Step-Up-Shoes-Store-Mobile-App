package com.stepup.retrofit2;

import com.stepup.model.Banner;
import com.stepup.model.ProductCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("banner")
    Call<List<Banner>> getBannerAll();

    @GET("products")
    Call<List<ProductCard>> getProductAll();

}
