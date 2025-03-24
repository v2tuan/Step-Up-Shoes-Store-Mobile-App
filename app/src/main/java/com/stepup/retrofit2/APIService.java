package com.stepup.retrofit2;

import com.stepup.model.ApiResponse;
import com.stepup.model.Banner;
import com.stepup.model.ProductCard;
import com.stepup.model.User;
import com.stepup.model.VerifyOtpRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @GET("banner")
    Call<List<Banner>> getBannerAll();

    @GET("products")
    Call<List<ProductCard>> getProductAll();

    @POST("users/register")
    Call<ApiResponse> registerUser(@Body User userDTO);

    @POST("users/verify")
    Call<ApiResponse> verifyOtp(@Body VerifyOtpRequest verifyAccountDTO);

    @POST("users/resend-otp")
    Call<ApiResponse> resendOtp(@Query("email") String email);

}
