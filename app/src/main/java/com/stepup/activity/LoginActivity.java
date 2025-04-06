package com.stepup.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.databinding.ActivityLoginBinding;
import com.stepup.databinding.ActivityMainBinding;
import com.stepup.model.Banner;
import com.stepup.model.User;
import com.stepup.model.ZoomOutPageTransformer;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding activityLoginBinding;

    private SharedPreferences sharedPreferences;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
        editor.remove("token");
        editor.remove("remember");
        editor.apply();
        checkLogin();


        // Đăng nhập mặc định
        activityLoginBinding.signInBtn.setOnClickListener(v -> {
            showLoading(); // Hiển thị process bar
            String email = activityLoginBinding.emailTxt.getText().toString();
            String password = activityLoginBinding.passwordTxt.getText().toString();
            loginUser(email, password);
        });

        // Đăng nhập với gogle
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode("94795259707-gesvi3nivrc8mi24pmrq39d45vro2vi6.apps.googleusercontent.com") // Lấy Authorization Code
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        activityLoginBinding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, task -> {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });

            }
        });
    }

    private void loginUser(String email, String password) {
        User userLoginDTO = new User(email, password);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.login(userLoginDTO).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().get("token");
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    boolean rememberMe = activityLoginBinding.checkboxRemember.isChecked();
                    saveToken(token, rememberMe);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();
                } else {
                    hideLoading(); // Ẩn process bar
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("LoginError", t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveToken(String token, boolean rememberMe) {

        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }

    private void checkLogin() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        // Kiểm tra token hợp lệ và Remember Me
        if (token != null && rememberMe) {
            if (checkTokenValid(token)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();
            } else {
                clearToken();
                Toast.makeText(this, "Đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean checkTokenValid(String token) {
        try {
            // Tách phần payload từ token
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Token không hợp lệ");
            }

            // Giải mã payload
            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJson);

            // Lấy thời gian hết hạn từ payload
            long exp = payload.getLong("exp");

            // Lấy thời gian hiện tại (timestamp tính bằng giây)
            long currentTime = System.currentTimeMillis() / 1000;

            // So sánh thời gian hết hạn
            if (currentTime < exp) {
                Log.d("AuthInterceptor", "Token hợp lệ, thời gian hết hạn: " + exp);
                return true;
            } else {
                Log.e("AuthInterceptor", "Token đã hết hạn");
                return false;
            }
        } catch (Exception e) {
            Log.e("AuthInterceptor", "Có lỗi xảy ra khi kiểm tra token: " + e.getMessage());
            return false;
        }
    }
    private void clearToken() {
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
        editor.remove("token");
        editor.remove("rememberMe");
        editor.apply();
    }
    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    public void goToForgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        showLoading(); // Hiển thị process bar
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String authCode = account.getServerAuthCode();
                Log.d("GoogleAuth", "Auth Code: " + authCode);

                // Gửi authCode lên server
//                sendAuthCodeToServer(authCode);
                // Gửi authCode lên server
                sendAuthCodeToServer(authCode);
            } catch (ApiException e) {
                hideLoading(); // Ẩn process bar
                Log.w("GoogleAuth", "Sign-in failed", e);
            }
        }
    }

    private void sendAuthCodeToServer(String authCode) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> callBanners = apiService.callbackBackend(authCode);
        callBanners.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().toString();
                    Log.d("Backend", "Token: " + token);
                    boolean rememberMe = activityLoginBinding.checkboxRemember.isChecked();
                    saveToken(token, rememberMe);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    // Hiển thị process bar
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