package com.stepup.retrofit2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    // duong dan API
                    .baseUrl("http://10.0.2.2:8089/api/v1/")
                    .addConverterFactory(ScalarsConverterFactory.create()) // Cho phép nhận chuỗi thay vì JSON
                    .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson nếu có API trả về JSON
                    .build();
        }
        return retrofit;
    }
}
