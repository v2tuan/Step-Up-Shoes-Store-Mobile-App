package com.stepup.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stepup.R;
import com.stepup.databinding.ActivityOrderResultBinding;

public class OrderResultActivity extends AppCompatActivity {
    ActivityOrderResultBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpBackButton();
        setUpViewOrderButton();

//        // Lấy thông tin từ intent
//        Uri data = getIntent().getData();
//        if (data != null) {
//            String txnRef = data.getQueryParameter("txnRef");
//            String responseCode = data.getQueryParameter("responseCode");
//
//            Log.e(TAG, txnRef + responseCode);
//            // Hiển thị kết quả thanh toán
//            if ("00".equals(responseCode)) {
//                Log.d(TAG, "show Success Message");
////                showSuccessMessage(txnRef);
//            } else {
//                Log.e(TAG, "showFailureMessage");
////                showFailureMessage(responseCode);
//            }
//
//            // Hoặc có thể gọi API để lấy thông tin chi tiết về giao dịch
////            fetchTransactionDetails(txnRef);
//        }

        String a = getIntent().getStringExtra("txnRef");
        String b = getIntent().getStringExtra("responseCode");
        Log.d(TAG, "show Success Message");
        Log.e(TAG, a + b);
    }

    private void setUpViewOrderButton() {
        binding.btnViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderResultActivity.this, OrderOverviewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpBackButton(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}