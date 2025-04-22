package com.stepup.retrofit2;

import com.stepup.model.AddToCartDTO;
import com.stepup.model.Address;
import com.stepup.model.ApiResponse;
import com.stepup.model.Banner;
import com.stepup.model.CartItem;
import com.stepup.model.ProductVariant;
import com.stepup.model.ResponseObject;
import com.stepup.model.Favorite;
import com.stepup.model.FavoriteDTO;
import com.stepup.model.UserDTO;
import com.stepup.model.location.DistrictResponse;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.location.ProvinceResponse;
import com.stepup.model.User;
import com.stepup.model.VerifyOtpRequest;
import com.stepup.model.location.WardResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
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
    Call<Map<String, String>> login(@Body UserDTO userLoginDTO);

    @GET("users/profile")
    Call<User> profile();

	@GET("/api/check-token")
    Call<String> checkToken(@Header("Authorization") String token);


    @GET("favorite")
    Call<List<Favorite>> getAllFavoriteItem();

    @DELETE("favorite/remove/{id}")
    Call<String> removeFavoriteItem(@Path("id") long favoriteItemId);

    @POST("favorite/add")
    Call<String> addToFavorite(@Body FavoriteDTO favoriteItemDTO);

    @POST("favorite/add1")
    Call<String> addToFavorite1(@Query("ProductId") long productId);

    @DELETE("favorite/remove")
    Call<String> removeToFavorite1(@Query("ProductId") long productId);

    @GET("favorite/productVarient/{id}")
    Call<List<ProductVariant>> getProductVarientByColorId(@Path("id") long colorId);

    @GET("favorite/productByColor/{id}")
    Call<Product> getProductByColorId(@Path("id") long colorId );

    @POST("cart/add")
    Call<String> addCart(@Body AddToCartDTO addToCartDTO);

    @GET("cart")
    Call<List<CartItem>> getAllCartItem();

    @POST("cart/remove/{id}")
    Call<String> removeCartItem(@Path("id") long cartItemId);



//	@GET("/api/v1/address/user/addresses")
//  Call<List<Address>> getAddressesByUserId();

    @GET("/api/v1/address/user/addresses")
    Call<Map<String, Object>> getAddressesByUserId();

    @POST("address")
    Call<ApiResponse> createAddress(@Body Address address);

    @PUT("address/{id}")
    Call<ApiResponse> updateAddress(@Path("id") Long id, @Body Address address);

    @DELETE ("address/{id}")
    Call<ApiResponse> deleteAddress(@Path("id") Long id);

    @PUT("address/set-default/{id}")
    Call<ApiResponse> setDefaultAddress(@Path("id") Long id);

    @GET("address/get-default")
    Call<ResponseObject> getDefaultAddress();

    // Fetch all provinces
    @GET("provinces/getAll")
    Call<ProvinceResponse> getProvinces(
            @Query("limit") int limit
    );

    // Fetch districts for a province
    @GET("districts/getByProvince")
    Call<DistrictResponse> getDistricts(
            @Query("provinceCode") String provinceCode,
            @Query("limit") int limit
    );

    // Fetch wards for a district
    @GET("wards/getByDistrict")
    Call<WardResponse> getWards(
            @Query("districtCode") String districtCode,
            @Query("limit") int limit
    );


    // Coupon
    @GET("coupon")
    Call<ResponseObject> getAllCoupons();

}
