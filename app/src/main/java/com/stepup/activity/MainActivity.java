package com.stepup.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.google.android.material.badge.BadgeDrawable;
import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ProductCardAdapter;
import com.stepup.databinding.ActivityMainBinding;
import com.stepup.fragment.CartFragment;
import com.stepup.fragment.FavoriteFragment;
import com.stepup.fragment.HomeFragment;
import com.stepup.fragment.PersonFragment;
import com.stepup.fragment.SearchFragment;
import com.stepup.model.Banner;
import com.stepup.model.CartItem;
import com.stepup.model.ProductCard;
import com.stepup.model.ZoomOutPageTransformer;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    private Fragment currentFragment; // Lưu Fragment hiện tại để tối ưu bộ nhớ
    Fragment selectedFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setCartItemCount();

        // Hiển thị Fragment mặc định
        if (savedInstanceState == null) {
            switchFragment(HomeFragment.class);
        } else {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

//        setupButtonListeners();

        // Xử lý sự kiện chọn item
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                switchFragment(HomeFragment.class);
            } else if (item.getItemId() == R.id.nav_search) {
                switchFragment(SearchFragment.class);
            } else if (item.getItemId() == R.id.nav_favorite) {
                switchFragment(FavoriteFragment.class);
            } else if (item.getItemId() == R.id.nav_cart) {
                switchFragment(CartFragment.class);
            } else if (item.getItemId() == R.id.nav_profile) {
                switchFragment(PersonFragment.class);
            }
            return true;
        });

        // Badge cho thông báo
        BadgeDrawable badgeNotification = binding.bottomNav.getOrCreateBadge(R.id.nav_favorite);
        badgeNotification.setNumber(1);  // Ví dụ: có 5 thông báo mới
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.green));

        // Lắng nghe sự kiện thay đổi của Fragment
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof HomeFragment) {
                binding.bottomNav.setSelectedItemId(R.id.nav_home);
            } else if (current instanceof SearchFragment) {
                binding.bottomNav.setSelectedItemId(R.id.nav_search);
            } else if (current instanceof FavoriteFragment) {
                binding.bottomNav.setSelectedItemId(R.id.nav_favorite);
            } else if (current instanceof CartFragment) {
                binding.bottomNav.setSelectedItemId(R.id.nav_cart);
            } else if (current instanceof PersonFragment) {
                binding.bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        });
    }

    private void switchFragment(Class<? extends Fragment> fragmentClass) {
        // Lấy FragmentManager để quản lý các Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Kiểm tra xem Fragment đã tồn tại trong FragmentManager chưa (dựa vào tag)
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentClass.getSimpleName());

        // Nếu Fragment hiện tại đang hiển thị đã là Fragment cần chuyển đến, thì không làm gì cả (tránh load lại không cần thiết)
        if (currentFragment != null && currentFragment.getClass().equals(fragmentClass)) {
            return;
        }

        // Nếu Fragment chưa tồn tại, thì tạo một instance mới
        if (existingFragment == null) {
            try {
                existingFragment = fragmentClass.newInstance(); // Khởi tạo Fragment bằng cách dùng reflection
            } catch (Exception e) {
                e.printStackTrace(); // Nếu có lỗi (do constructor không hợp lệ), in lỗi ra console và dừng thực thi
                return;
            }
        }

        // Bắt đầu transaction để thay thế Fragment hiện tại bằng Fragment mới
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, existingFragment, fragmentClass.getSimpleName());

        if (fragmentClass == HomeFragment.class) {
            // Khi về Home, xóa toàn bộ BackStack để nó luôn là đầu tiên
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            transaction.addToBackStack(null); // // Thêm vào BackStack để có thể quay lại Fragment trước đó bằng nút back
        }

        transaction.commit();

        // Cập nhật biến currentFragment để lưu Fragment hiện tại
        currentFragment = existingFragment;
    }

    private void setCartItemCount(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<CartItem>> callCart = apiService.getAllCartItem();
        callCart.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> cartItems = response.body();
                    // Badge cho giỏ hàng
                    BadgeDrawable badgeCart = binding.bottomNav.getOrCreateBadge(R.id.nav_cart);
                    badgeCart.setNumber(cartItems.size());
                    badgeCart.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }
}