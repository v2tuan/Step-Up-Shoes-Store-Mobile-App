package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.R;

public class AddressActivity extends AppCompatActivity {

    private TextView btnAddNewAddress;
    private ImageView btnBack;
    private RecyclerView recyclerViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        btnAddNewAddress = findViewById(R.id.btnAddNewAddress);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewAddress = findViewById(R.id.recyclerViewAddress);

        // Sự kiện nút "Thêm Địa Chỉ Mới"
        btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });

        // Sự kiện nút "Quay lại"
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}