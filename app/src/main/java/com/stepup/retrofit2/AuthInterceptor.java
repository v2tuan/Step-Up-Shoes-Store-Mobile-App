package com.stepup.retrofit2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

import com.stepup.activity.LoginActivity;

// xử lí tự động chèn token vào đầu, và xử lý khi token hết hạn
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Lấy token từ SharedPreferences
        String token = MyApp.getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("token", null);

        // Nếu không có token thì gửi request gốc
        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Gắn token vào header Authorization
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        Log.d("AuthInterceptor", "Request URL: " + originalRequest.url());
        Log.d("AuthInterceptor", "Headers: " + originalRequest.headers());
        Response response = chain.proceed(newRequest);

        // Xử lý khi token hết hạn (401 Unauthorized)
        if (response.code() == 401) {
            clearToken(); // Xóa token cũ
            navigateToLogin(); // Chuyển hướng về trang đăng nhập
        }

        return response;
    }

    // Hàm xóa token khi hết hạn
    private void clearToken() {
        SharedPreferences.Editor editor = MyApp.getContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit();
        editor.remove("token");
        editor.apply();
    }

    // Hàm chuyển hướng về trang đăng nhập
    private void navigateToLogin() {
        Intent intent = new Intent(MyApp.getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApp.getContext().startActivity(intent);
    }
}

