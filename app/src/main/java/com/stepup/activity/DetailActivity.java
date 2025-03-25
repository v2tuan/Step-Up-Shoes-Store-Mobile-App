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
import androidx.viewpager2.widget.ViewPager2;

import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ColorAdapter;
import com.stepup.adapter.SizeAdapter;
import com.stepup.databinding.ActivityDetailBinding;
import com.stepup.model.Banner;
import com.stepup.model.Color;
import com.stepup.model.ColorImage;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.ProductImage;
import com.stepup.model.Size;
import com.stepup.model.ZoomOutPageTransformer;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ProductCard item;
    private int numberOrder = 1;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
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
                    product = response.body();

                    binding.progressBarProductImage.setVisibility(View.GONE);

                    binding.titleTxt.setText(product.getName());
                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    String priceText = format.format(product.getPrice());
                    binding.priceTxt.setText(priceText);
                    binding.descriptionTxt.setText(product.getDescription());
                    binding.ratingTxt.setText(product.getRating().toString());

                    // Tạo danh sách sliderItems chứa các hình ảnh để hiển thị
                    ArrayList<Banner> sliderItems = new ArrayList<>();
                    BannerAdapter bannerAdapter = null;

                    try {
                        // Duyệt qua danh sách đường dẫn ảnh (picUrl) của item và thêm vào sliderItems
                        for (ColorImage image : product.getColors().get(0).getColorImages()) {
                            sliderItems.add(new Banner(image.getImageUrl()));
                        }

                        // Gán adapter cho ViewPager2 (slider) để hiển thị danh sách hình ảnh
                        bannerAdapter = new BannerAdapter(sliderItems);
                        binding.slider.setAdapter(bannerAdapter);

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
                    }catch (Exception exception){
                        Log.e("TAG", "Lỗi xảy ra: " + exception.getMessage(), exception);
                    }





                    // Tạo danh sách colorList để lưu các ảnh đại diện màu sắc (hoặc màu)
                    ArrayList<String> colorList = new ArrayList<>();

                    // Duyệt qua danh sách đường dẫn ảnh (có thể là hình ảnh màu)
                    for (Color color : product.getColors()) {
                        colorList.add(color.getColorImages().get(0).getImageUrl());
                    }

                    // Gán adapter cho RecyclerView hiển thị màu sắc (hoặc ảnh đại diện màu)
                    ViewPager2 viewPager2 = binding.slider;

                    DotsIndicator dotsIndicator = binding.dotIndicator;
                    binding.colorList.setAdapter(new ColorAdapter(colorList, viewPager2, dotsIndicator, product, sliderItems, bannerAdapter, binding.sizeList));

                    // Gán layoutManager cho RecyclerView màu, cũng theo chiều ngang
                    binding.colorList.setLayoutManager(
                            new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false)
                    );

                    getSize();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    public void getSize(){
        // Tạo danh sách sizeList để lưu các kích thước sản phẩm
        ArrayList<String> sizeList = new ArrayList<>();

        // Duyệt qua danh sách kích thước trong item và thêm vào sizeList
        for (Size size : product.getSizes()) {
            sizeList.add(size.getName());  // chuyển về chuỗi nếu cần
        }

        // Gán adapter cho RecyclerView hiển thị kích thước
        binding.sizeList.setAdapter(new SizeAdapter(sizeList, product));

        // Gán layoutManager cho RecyclerView để hiển thị theo chiều ngang
        binding.sizeList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void getBundle() {
        // Lấy đối tượng 'item' được truyền từ Intent (ở Activity trước) với key là "object"
        item = getIntent().getParcelableExtra("object");
    }
}