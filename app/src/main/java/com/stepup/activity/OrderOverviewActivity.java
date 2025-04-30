package com.stepup.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.stepup.R;
import com.stepup.adapter.OrderPagerAdapter;
import com.stepup.databinding.ActivityOrderOverviewBinding;
import com.stepup.model.OrderShippingStatus;

public class OrderOverviewActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrderPagerAdapter orderPagerAdapter;
    ActivityOrderOverviewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;
        orderPagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(orderPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (OrderShippingStatus.values()[position]) {
                case PENDING:
                    tab.setText("Pending");
                    break;
                case PREPARING:
                    tab.setText("Preparing");
                    break;
                case DELIVERING:
                    tab.setText("Delivering");
                    break;
                case DELIVERED:
                    tab.setText("Delivered");
                    break;
                case CANCELLED:
                    tab.setText("Cancelled");
                    break;
                case RETURNED:
                    tab.setText("Return");
                    break;
            }
        }).attach();

        // Lấy chỉ số tab từ Intent (default là PENDING)
        int tabPosition = getIntent().getIntExtra("tab_position", OrderShippingStatus.PENDING.ordinal());

        // Chỉ định mở tab theo vị trí
        viewPager.setCurrentItem(tabPosition);

    }
}