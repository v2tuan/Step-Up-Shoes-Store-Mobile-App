package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ColorAdapter;
import com.stepup.databinding.ActivityDetailBinding;
import com.stepup.model.Banner;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.ProductImage;
import com.stepup.model.ZoomOutPageTransformer;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ProductCard item;
    private int numberOrder = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getBundle();
        getBanner();
    }

    private void getBanner(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Product> callProduct = apiService.getProductById(item.getId());
        callProduct.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();

                    // Tạo danh sách sliderItems chứa các hình ảnh để hiển thị
                    ArrayList<Banner> sliderItems = new ArrayList<>();

                    // Duyệt qua danh sách đường dẫn ảnh (picUrl) của item và thêm vào sliderItems
                    for (ProductImage image : product.getProductImages()) {
                        sliderItems.add(new Banner(image.getImageUrl()));
                    }

                    // Gán adapter cho ViewPager2 (slider) để hiển thị danh sách hình ảnh
                    binding.slider.setAdapter(new BannerAdapter(sliderItems));

                    // Cấu hình ViewPager2:
                    // Cắt padding ngoài
                    binding.slider.setClipToPadding(true);

                    // Cắt phần con (clipChildren)
                    binding.slider.setClipChildren(true);

                    // Hiển thị trước 1 ảnh gần nhất (giúp tạo hiệu ứng mượt hơn)
                    binding.slider.setOffscreenPageLimit(1);

                    // Tắt hiệu ứng kéo quá đà (overscroll) của RecyclerView bên trong ViewPager2
//        if (binding.slider.getChildAt(0) instanceof RecyclerView) {
//            ((RecyclerView) binding.slider.getChildAt(0)).setOverScrollMode(View.OVER_SCROLL_NEVER);
//        }



                    // Nếu có nhiều hơn 1 ảnh thì hiển thị dotIndicator
                    if (sliderItems.size() > 1) {
                        binding.dotIndicator.setVisibility(View.VISIBLE);
                        // Gắn dotIndicator với ViewPager2
                        binding.dotIndicator.attachTo(binding.slider);
                    }





                    // Tạo danh sách colorList để lưu các ảnh đại diện màu sắc (hoặc màu)
                    ArrayList<String> colorList = new ArrayList<>();

                    // Duyệt qua danh sách đường dẫn ảnh (có thể là hình ảnh màu)
                    for (ProductImage imageUrl : product.getProductImages()) {
                        colorList.add(imageUrl.getImageUrl());
                    }

                    // Gán adapter cho RecyclerView hiển thị màu sắc (hoặc ảnh đại diện màu)
                    binding.colorList.setAdapter(new ColorAdapter(colorList));

                    // Gán layoutManager cho RecyclerView màu, cũng theo chiều ngang
                    binding.colorList.setLayoutManager(
                            new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false)
                    );
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    private void getBundle() {
        // Lấy đối tượng 'item' được truyền từ Intent (ở Activity trước) với key là "object"
        item = getIntent().getParcelableExtra("object");
    }
}