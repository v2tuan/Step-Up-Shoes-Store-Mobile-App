package com.stepup.retrofit2;

import com.stepup.model.AddToCartDTO;
import com.stepup.model.Address;
import com.stepup.model.ApiResponse;
import com.stepup.model.Banner;
import com.stepup.model.CartItem;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.User;
import com.stepup.model.VerifyOtpRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @GET("banner")
    Call<List<Banner>> getBannerAll();

    @GET("products")
    Call<List<ProductCard>> getProductAll();

 	@GET("products/{id}")
    Call<Product> getProductById(@Path("id") Long id);

    @POST("users/register")
    Call<ApiResponse> registerUser(@Body User userDTO);

    @POST("users/verify")
    Call<ApiResponse> verifyOtp(@Body VerifyOtpRequest verifyAccountDTO);

    @POST("users/resend-otp")
    Call<ApiResponse> resendOtp(@Query("email") String email);

    @POST("users/auth/social/callback")
    Call<String> callbackBackend(@Query("code") String code);

    @POST("users/login")
    Call<Map<String, String>> login(@Body User userLoginDTO);

    @GET("users/profile")
    Call<User> profile();

	@GET("/api/check-token")
    Call<String> checkToken(@Header("Authorization") String token);
    @POST("cart/add")
    Call<String> addCart(@Body AddToCartDTO addToCartDTO);

    @GET("cart")
    Call<List<CartItem>> getAllCartItem();

    @POST("cart/remove/{id}")
    Call<String> removeCartItem(@Path("id") long cartItemId);
	
	@GET("/api/v1/address/user/addresses")
    Call<List<Address>> getAddressesByUserId();
}
