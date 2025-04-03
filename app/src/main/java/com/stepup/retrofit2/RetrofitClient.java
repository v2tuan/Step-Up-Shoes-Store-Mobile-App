package com.stepup.retrofit2;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(retrofit==null){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            retrofit = new Retrofit.Builder()
                    // duong dan API
                    .baseUrl("http://10.0.2.2:8089/api/v1/")
                    .client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor()).addInterceptor(loggingInterceptor).build())
                    .addConverterFactory(ScalarsConverterFactory.create()) // Cho phép nhận chuỗi thay vì JSON
                    .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson nếu có API trả về JSON
                    .build();
        }
        return retrofit;
    }
}
