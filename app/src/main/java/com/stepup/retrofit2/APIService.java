package com.stepup.retrofit2;

import com.stepup.model.AddToCartDTO;
import com.stepup.model.Address;
import com.stepup.model.ApiResponse;
import com.stepup.model.Banner;
import com.stepup.model.CartItem;
import com.stepup.model.Color;
import com.stepup.model.ConversationDTO;
import com.stepup.model.MessageDTO;
import com.stepup.model.OrderDTO;
import com.stepup.model.ProductVariant;
import com.stepup.model.ResponseObject;
import com.stepup.model.Favorite;
import com.stepup.model.FavoriteDTO;
import com.stepup.model.ReviewResponse;
import com.stepup.model.UserDTO;
import com.stepup.model.location.AddressRequest;
import com.stepup.model.location.DistrictResponse;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.location.ProvinceResponse;
import com.stepup.model.User;
import com.stepup.model.VerifyOtpRequest;
import com.stepup.model.location.WardResponse;
import com.stepup.model.payment.PaymentDTO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    @DELETE("favorite/remove1")
    Call<String> removeToFavorite2(@Query("colorId") Long colorId);
    @DELETE("favorite/remove")
    Call<String> removeToFavorite1(@Query("ProductId") long productId);

    @GET("favorite/productVarient/{id}")
    Call<List<ProductVariant>> getProductVarientByColorId(@Path("id") long colorId);

    @GET("favorite/productByColor/{id}")
    Call<Product> getProductByColorId(@Path("id") long colorId );

    @GET("favorite/check")
    Call<Boolean> checkFavorite(@Query("colorId") long colorId);

    @POST("cart/add")
    Call<String> addCart(@Body AddToCartDTO addToCartDTO);

    @GET("cart")
    Call<List<CartItem>> getAllCartItem();

    @POST("cart/remove/{id}")
    Call<String> removeCartItem(@Path("id") long cartItemId);

    @POST("cart/cartFromOrder")
    Call<String> addToCartOrder(@Body List<AddToCartDTO> addToCartDTOList);

//	@GET("/api/v1/address/user/addresses")
//  Call<List<Address>> getAddressesByUserId();

    @GET("/api/v1/address/user/addresses")
    Call<Map<String, Object>> getAddressesByUserId();

    @POST("address")
    Call<ApiResponse> createAddress(@Body AddressRequest request);

    @PUT("address/{id}")
    Call<ApiResponse> updateAddress(@Path("id") Long id, @Body AddressRequest address);

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

//search
    @GET("search")
    Call<List<ProductCard>> searchProducts(@Query("query") String query);
    @GET("search/suggestions")
    Call<List<String>> getSearchSuggestions();
    @GET("search/colors")
    Call<List<String>> getColor();
    // Order
    @POST("orders")
    Call<ResponseObject> createOrder(@Body OrderDTO orderDTO);

    @GET("orders/{orderStatus}")
    Call<ResponseObject> getOrdersByStatus(@Path("orderStatus") String orderStatus);

    @GET("orders")
    Call<ResponseObject> getOrder(@Query("orderId") Long orderId);

    @POST("orders/cancelOrder")
    Call<ResponseObject> cancelOrder(@Query("orderId") Long orderId);


    @POST("orders/returnOrder")
    Call<ResponseObject> returnOrder(@Query("orderId") Long orderId);
    // Payment
    @POST("payments/create_payment_url")
    Call<ResponseObject> createPayment(@Body PaymentDTO paymentDTO);
    @POST("payments/payment")
    Call<ResponseObject> payment(@Query("orderId") Long orderId);

    @Multipart
    @POST("/api/v1/reviews/add")
    Call<String> submitReview(
            @Part("productVariantId") RequestBody productVariantId,
            @Part("orderId") RequestBody orderId,
            @Part("content") RequestBody content,
            @Part("rating") RequestBody rating,
            @Part List<MultipartBody.Part> images
    );
    @GET("/api/v1/reviews")
    Call<List<ReviewResponse>> getReviewsByProductId(@Query("id") Long productId);


    @GET("chat/conversations")
    Call<ConversationDTO> getMyConversations();

    @GET("chat/messages")
    Call<List<MessageDTO>> getMessages(@Query("conversationId") long conversationId);
}
