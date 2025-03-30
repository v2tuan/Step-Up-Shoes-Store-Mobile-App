package com.stepup.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;

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
//        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
//        editor.remove("token");
 //       editor.apply();
        checkLogin();

        activityLoginBinding.signInBtn.setOnClickListener(v -> {
            String email = activityLoginBinding.emailTxt.getText().toString();
            String password = activityLoginBinding.passwordTxt.getText().toString();
            loginUser(email, password);
        });

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
                    // Truyền token qua màn hình chính
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();
                } else {
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
    // Hàm lưu token vào SharedPreferences
    private void saveToken(String token, boolean rememberMe) {
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
        if (rememberMe) {
            editor.putString("token", token);
        } else {
            editor.remove("token");
        }
        editor.apply();
    }

    // Kiểm tra đăng nhập khi mở ứng dụng
    private void checkLogin() {
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("token", null);
        if (token != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();
        }
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
                showLoading(); // Hiển thị process bar
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
//                    String token = response.body().toString();
                    Log.d("Backend", "Token: " + response.body().toString());
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