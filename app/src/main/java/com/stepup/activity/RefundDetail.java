package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stepup.R;
import com.stepup.databinding.ActivityOrderDetailBinding;
import com.stepup.databinding.ActivityRefundDetailBinding;
import com.stepup.model.OrderResponse;
import com.stepup.model.PaymentStatus;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RefundDetail extends AppCompatActivity {
    private ActivityRefundDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
       //setContentView(R.layout.activity_refund_detail);
        binding = ActivityRefundDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        double price = getIntent().getDoubleExtra("price", 0.0);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = format.format(price);
        String status = getIntent().getStringExtra("status");

        binding.tvRefundAmount.setText( formattedPrice);
        String updatedAt = getIntent().getStringExtra("date"); // Chuỗi ngày giờ
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String formattedDate ="2 Th05 2025";
        try {
            Date date = dateFormat.parse(updatedAt); // Chuyển đổi thành đối tượng Date
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formattedDate = outputFormat.format(date); // Định dạng lại ngày giờ
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(status.equals("REFUNDING"))
        {
            binding.tvOrderStatus.setVisibility(View.VISIBLE);
            binding.txtChitiet1.setText("Yêu cầu hoàn tiền của bạn đang được xử lý. Đơn hàng của bạn sẽ được cửa hàng xử lý sớm, vui lòng đợi!.");
            binding.tvFooterNote.setVisibility(View.VISIBLE);
            binding.tvChiTiet.setText("Đang được xử lý");
            binding.tvApprovalDate.setText(formattedDate);

        }
        else{
            binding.tvOrderStatus1.setVisibility(View.VISIBLE);
            binding.tvFooterNote1.setVisibility(View.VISIBLE);
            binding.xuly.setVisibility(View.GONE);
            binding.hoantien.setVisibility(View.VISIBLE);
            binding.tvCompletionDate.setText(formattedDate);
        }
        binding.btnBack.setOnClickListener(v -> finish());

    }
}