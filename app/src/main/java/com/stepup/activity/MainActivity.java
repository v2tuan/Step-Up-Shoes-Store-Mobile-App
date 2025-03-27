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

        // Hiển thị Fragment mặc định
        if (savedInstanceState == null) {
            switchFragment(HomeFragment.class);
        } else {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        setupButtonListeners();

    }

    private void setupButtonListeners() {
        binding.homeButton.setOnClickListener(view -> switchFragment(HomeFragment.class));
        binding.searchButton.setOnClickListener(view -> switchFragment(SearchFragment.class));
        binding.favoriteButton.setOnClickListener(view -> switchFragment(FavoriteFragment.class));
        binding.cartButton.setOnClickListener(view -> switchFragment(CartFragment.class));
        binding.personButton.setOnClickListener(view -> switchFragment(PersonFragment.class));
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

}