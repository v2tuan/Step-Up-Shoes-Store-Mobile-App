package com.stepup.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo views
        webView = binding.webView;
        progressBar = binding.progressBar;

        // Thiết lập WebView
        setupWebView();

        // Lấy dữ liệu thanh toán (từ Intent hoặc từ nguồn khác)
        String paymentURL = getIntent().getStringExtra("paymentURL");

        loadPaymentUrl(paymentURL);
    }

    private void loadPaymentUrl(String url) {
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            AppUtils.showDialogNotify(PaymentActivity.this, R.drawable.error,"URL thanh toán không hợp lệ");
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupWebView() {
        // Cấu hình WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);

        // Thiết lập WebViewClient để xử lý các sự kiện trong WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Kiểm tra nếu URL là deep link của ứng dụng (returnURL từ VNPAY)
                if (url.startsWith("yourapp://")) {
                    // Xử lý deep link - có thể lấy thông tin từ URL và xử lý kết quả
                    handlePaymentCallback(url);
                    return true;
                }
                // Tiếp tục tải URL khác (http, https) trong WebView
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void handlePaymentCallback(String url) {
        // Phân tích URL để lấy thông tin kết quả thanh toán
        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("responseCode");
        String txnRef = uri.getQueryParameter("txnRef");

        // Xử lý kết quả thanh toán
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            // Chuyển hướng người dùng đến màn hình thông báo thành công
            Intent intent = new Intent(PaymentActivity.this, OrderResultActivity.class);
            intent.putExtra("txnRef", txnRef);
            intent.putExtra("responseCode", responseCode);
            startActivity(intent);
        } else {
            // Thanh toán thất bại
            // Chuyển hướng người dùng đến màn hình thông báo thất bại
            Intent intent = new Intent(PaymentActivity.this, OrderResultActivity.class);
            intent.putExtra("txnRef", txnRef);
            intent.putExtra("responseCode", responseCode);
            startActivity(intent);
        }

        finish();  // Đóng màn hình thanh toán hiện tại
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}