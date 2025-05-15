package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.stepup.model.VerifyOtpRequest;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ConfirmOtpActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnConfirmOtp;
    private TextView tvResendOtp;
    private APIService apiService;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_otp);
        email = getIntent().getStringExtra("email");

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        setupOtpInputs();
        btnConfirmOtp = findViewById(R.id.btnConfirmOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);;

        btnConfirmOtp.setOnClickListener(v -> verifyOtp());

        tvResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void verifyOtp() {
        String otp = otp1.getText().toString() + otp2.getText().toString() +
                otp3.getText().toString() + otp4.getText().toString() +
                otp5.getText().toString() + otp6.getText().toString();

        if (otp.length() != 6) {
            Toast.makeText(this, "Nhập mã OTP hợplệ", Toast.LENGTH_SHORT).show();
            return;
        }
        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);
        showLoading();
        apiService.verifyOtp(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    hideLoading();
                   // Toast.makeText(ConfirmOtpActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (message.contains("successful")) {
                        Toast.makeText(ConfirmOtpActivity.this, "Xác nhận thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmOtpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AppUtils.showDialogNotify(ConfirmOtpActivity.this, R.drawable.error,"Lỗi xác thực: " + message);
                        Log.e("OTP Verification", "Lỗi xác thực: " + message);

                    }
                } else {
                    hideLoading();
                    AppUtils.showDialogNotify(ConfirmOtpActivity.this, R.drawable.error,"Xác thực OTP thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(ConfirmOtpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        apiService.resendOtp(email).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    if (message.contains("successful")) {
                        AppUtils.showDialogNotify(ConfirmOtpActivity.this, R.drawable.ic_check,"OTP mới đã được gửi!");
                    } else {
                        Log.e("OTP Resend", "Lỗi xác thực: " + message);
                        AppUtils.showDialogNotify(ConfirmOtpActivity.this, R.drawable.error,"Lỗi : "+ message);

                    }
                } else {
                    AppUtils.showDialogNotify(ConfirmOtpActivity.this, R.drawable.error,"Không thể gửi lại OTP");

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ConfirmOtpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupOtpInputs() {
        EditText[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;

            otpFields[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus(); // Chuyển sang ô tiếp theo
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpFields[index].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (index > 0 && otpFields[index].getText().toString().isEmpty()) {
                        otpFields[index - 1].requestFocus(); // Quay lại ô trước khi xóa
                    }
                }
                return false;
            });
        }
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