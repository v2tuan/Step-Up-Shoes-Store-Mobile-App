package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.model.ApiResponse;
import com.stepup.model.User;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameTxt, emailTxt, passwordTxt, confirmPasswordTxt;
    private Button signUpBtn;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        nameTxt = findViewById(R.id.nameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        confirmPasswordTxt = findViewById(R.id.confirmPasswordTxt);
        signUpBtn = findViewById(R.id.signUpBtn);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);;

        signUpBtn.setOnClickListener(v -> registerUser());

    }
    private void registerUser() {
        String name = nameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String confirmPassword = confirmPasswordTxt.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            AppUtils.showDialogNotify(RegisterActivity.this, R.drawable.error,"Mật khẩu không khớp");
            return;
        }
        if (password.length() < 8) {
            AppUtils.showDialogNotify(RegisterActivity.this, R.drawable.error,"Mật khẩu phải có ít nhất 8 ký tự");
            return;
        }
        User userDTO = new User(name, email, password, confirmPassword);
        showLoading();
        apiService.registerUser(userDTO).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hideLoading();
                    String message = response.body().getMessage();
                    Log.d("API Response", "Code: " + response.code() + ", Body: " + new Gson().toJson(response.body()));
                    // Kiểm tra nội dung phản hồi từ API
                    if (message.contains("successful")) {
                        Toast.makeText(RegisterActivity.this, "OTP đã được gửi đến email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, ConfirmOtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideLoading();
                    AppUtils.showDialogNotify(RegisterActivity.this, R.drawable.error,"Đăng ký thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoading();
                AppUtils.showDialogNotify(RegisterActivity.this, R.drawable.error,"Lỗi: " + t.getMessage());
            }
        });
    }

    public void goToLogin(View view) {
        Intent intent = new Intent( RegisterActivity.this,  LoginActivity.class);
        startActivity(intent);
    }
    private void showLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);
        overlay.setClickable(false);
    }
}