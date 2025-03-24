package com.stepup.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ProductCardAdapter;
import com.stepup.databinding.ActivityMainBinding;
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


//        initBanner();
        getBanner();
        getProducts();

    }

    private void getBanner(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Banner>> callBanners = apiService.getBannerAll();
        callBanners.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Khi có dữ liệu, ẩn ProgressBar
                    binding.progressBarBanner.setVisibility(View.GONE);

                    List<Banner> banners = response.body();
                    // Cập nhật dữ liệu lên ViewPager
                    BannerAdapter bannerAdapter = new BannerAdapter(banners);
                    binding.viewpagerslider.setAdapter(bannerAdapter);

                    // Không cắt padding và children (để tạo hiệu ứng hiển thị nhiều ảnh cạnh nhau)
                    binding.viewpagerslider.setClipToPadding(false);
                    binding.viewpagerslider.setClipChildren(false);

                    // Tải sẵn 3 trang bên trái/phải để cuộn mượt hơn
                    binding.viewpagerslider.setOffscreenPageLimit(3);

                    // Tắt hiệu ứng overscroll (kéo dẻo khi vuốt tới đầu/cuối)
                    ((RecyclerView) binding.viewpagerslider.getChildAt(0)).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                    // Tạo hiệu ứng chuyển trang bằng cách thêm khoảng cách giữa các slide
                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40)); // khoảng cách 40px giữa các slide

                    // Gán hiệu ứng chuyển trang vào ViewPager2
                    binding.viewpagerslider.setPageTransformer(compositePageTransformer);

                    binding.viewpagerslider.setPageTransformer(new ZoomOutPageTransformer());

                    // Nếu có nhiều hơn 1 ảnh, hiển thị chỉ báo (dot indicator)
                    if (banners.size() > 1) {
                        binding.dotIndicator.setVisibility(View.VISIBLE);
                        binding.dotIndicator.attachTo(binding.viewpagerslider); // gắn indicator với ViewPager2
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    private void getProducts(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<ProductCard>> callBanners = apiService.getProductAll();
        callBanners.enqueue(new Callback<List<ProductCard>>() {
            @Override
            public void onResponse(Call<List<ProductCard>> call, Response<List<ProductCard>> response) {
                List<ProductCard> items = response.body();
                binding.viewPopular.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                binding.viewPopular.setAdapter(new ProductCardAdapter(items));
                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<ProductCard>> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }
}