package com.stepup.retrofit2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDateTime;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final int TIMEOUT = 60;
    private static Retrofit locationRetrofit;
    public static Retrofit getRetrofit(){
        if(retrofit==null){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    // duong dan API
                    .baseUrl("http://192.168.208.64:8089/api/v1/")
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Cho phép nhận chuỗi thay vì JSON
                    .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson nếu có API trả về JSON
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getLocationRetrofit() {
        if (locationRetrofit== null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            locationRetrofit= new Retrofit.Builder()
                    .baseUrl("https://vn-public-apis.fpo.vn/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()) // For JSON responses
                    .build();
        }
        return locationRetrofit;
    }
}
