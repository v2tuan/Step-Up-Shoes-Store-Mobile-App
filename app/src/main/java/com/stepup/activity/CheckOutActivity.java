package com.stepup.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stepup.R;
import com.stepup.adapter.OrderItemAdapter;
import com.stepup.databinding.ActivityCheckOutBinding;
import com.stepup.model.CartItem;

import java.util.List;

public class CheckOutActivity extends BaseActivity {
    private ActivityCheckOutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_check_out);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOrderItems();
    }

    private void getOrderItems() {
        try {

            Intent intent = getIntent();
            List<CartItem> receivedList = intent.getParcelableArrayListExtra("orderItems");
            binding.rvOrderItems.setAdapter(new OrderItemAdapter(receivedList));
            binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom)); // drawable divider của bạn
            binding.rvOrderItems.addItemDecoration(dividerItemDecoration);

        }
        catch (Exception e){
            Log.e("lakjsdg;l", e.getMessage());
        }
    }
}