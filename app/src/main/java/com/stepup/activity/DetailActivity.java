package com.stepup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ColorAdapter;
import com.stepup.adapter.ReviewAdapter;
import com.stepup.adapter.SizeAdapter;
import com.stepup.databinding.ActivityDetailBinding;
import com.stepup.model.AddToCartDTO;
import com.stepup.model.Banner;
import com.stepup.model.ColorImage;
import com.stepup.model.FavoriteDTO;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.model.ProductVariant;
import com.stepup.model.ReviewResponse;
import com.stepup.model.Size;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.FavoriteViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ProductCard item;
    private int numberOrder = 1;
    private FavoriteViewModel viewModel;
    private Product product;
    private Map<Long, Boolean> favoriteStatusCache = new HashMap<>();

    private List<ReviewResponse> reviewList;
    private List<ReviewResponse> allReviews;
    private ReviewAdapter reviewAdapter;
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
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        getBundle();
        getBanner();

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewAdapter);

        // Handle "All" button click
        binding.AllReview.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, AllReviewActivity.class);
            intent.putParcelableArrayListExtra("reviews", new ArrayList<>(allReviews));
            startActivity(intent);
        });
        binding.backBtn.setOnClickListener(v -> finish());
        // Xử lý khi nhấn nút "Cart" -> chuyển sang màn hình giỏ hàng
        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý thêm vào giỏ hàng ở đây
                if(ColorAdapter.colorSelected == null){
                    AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Vui lòng chọn color");

                    return;
                }

                if(SizeAdapter.sizeSelected == null){
                    AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Vui lòng chọn size");
                    return;
                }

                Long productVariant_id = null;
                for(ProductVariant variant: product.getProductVariants()){
                    if(variant.getColor().equals(ColorAdapter.colorSelected) && variant.getSize().equals(SizeAdapter.sizeSelected)){
                        productVariant_id = variant.getId();
                        break;
                    }
                }
                showLoading();
                AddToCartDTO addToCartDTO = new AddToCartDTO(productVariant_id, 1);
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<String> callAddCart = apiService.addCart(addToCartDTO);
                callAddCart.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            AppUtils.showDialogNotify(DetailActivity.this, R.drawable.ic_tick,response.body());
                            Log.d("Add To Cart", "Message: : " + response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        hideLoading();
                        Log.e("RetrofitError", "Error: " + t.getMessage());
                    }
                });
            }
        });
        binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ColorAdapter.colorSelected == null){
                    AppUtils.showDialogNotify(DetailActivity.this, R.drawable.ic_tick, "Please Slect Shoes Color! ");
                    return;
                }
                Long colorId = ColorAdapter.colorSelected.getId();
                boolean isCurrentlyFavorite = favoriteStatusCache.getOrDefault(colorId, false);
                binding.favBtn.setIconResource(isCurrentlyFavorite ? R.drawable.favorite : R.drawable.favorite_fill);
                favoriteStatusCache.put(colorId, !isCurrentlyFavorite);
                FavoriteDTO favoriteItemDTO = new FavoriteDTO(ColorAdapter.colorSelected.getId(),item.getPrice());
                showLoading();
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<String> call;
                if (isCurrentlyFavorite) {
                    // Giả định có API xóa yêu thích dựa trên colorId
                    call = apiService.removeToFavorite2(colorId);
                } else {
                    FavoriteDTO favoriteDTO = new FavoriteDTO(colorId, item.getPrice());
                    call = apiService.addToFavorite(favoriteDTO);
                }

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            AppUtils.showDialogNotify(DetailActivity.this, R.drawable.ic_tick,
                                    isCurrentlyFavorite ? "Xóa yêu thích thành công" : "Thêm yêu thích thành công");
                        } else {
                            // Hoàn tác nếu API thất bại
                            favoriteStatusCache.put(colorId, isCurrentlyFavorite);
                            binding.favBtn.setIconResource(isCurrentlyFavorite ? R.drawable.favorite_fill : R.drawable.favorite);
                            AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Lỗi: " + response.message());

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        hideLoading();
                        // Hoàn tác nếu API thất bại
                        favoriteStatusCache.put(colorId, isCurrentlyFavorite);
                        binding.favBtn.setIconResource(isCurrentlyFavorite ? R.drawable.favorite_fill : R.drawable.favorite);
                        AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Lỗi kết nối: " + t.getMessage());

                    }
                });
            }
        });
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
                    String priceText = format.format(product.getProductVariants().get(0).getPrice());
                    binding.priceTxt.setText(priceText);
                    binding.descriptionTxt.setText(product.getDescription());
                    binding.ratingTxt.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));

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

                        // Nếu có nhiều hơn 1 ảnh thì hiển thị dotIndicator
                        if (sliderItems.size() > 1) {
                            binding.dotIndicator.setVisibility(View.VISIBLE);
                            // Gắn dotIndicator với ViewPager2
                            binding.dotIndicator.attachTo(binding.slider);
                        }
                    }catch (Exception exception){
                        Log.e("TAG", "Lỗi xảy ra: " + exception.getMessage(), exception);
                    }

//                    // Tạo danh sách colorList để lưu các ảnh đại diện màu sắc (hoặc màu)
//                    ArrayList<String> colorList = new ArrayList<>();
//
//                    // Duyệt qua danh sách đường dẫn ảnh (có thể là hình ảnh màu)
//                    for (Color color : product.getColors()) {
//                        colorList.add(color.getColorImages().get(0).getImageUrl());
//                    }

                    // Gán adapter cho RecyclerView hiển thị màu sắc (hoặc ảnh đại diện màu)
                    ViewPager2 viewPager2 = binding.slider;

                    DotsIndicator dotsIndicator = binding.dotIndicator;
                    binding.colorList.setAdapter(new ColorAdapter(product.getColors(), viewPager2, dotsIndicator, product, sliderItems, bannerAdapter, binding.sizeList, new ColorAdapter.OnColorSelectedListener() {
                        @Override
                        public void checkFavoriteStatus(Long colorId) {
                            DetailActivity.this.checkFavoriteStatus(colorId);
                        }
                    }));
                    if (!product.getColors().isEmpty()) {
                        showLoading();
                        checkFavoriteStatus(product.getColors().get(0).getId());
                        hideLoading();
                    }
                    // Gán layoutManager cho RecyclerView màu, cũng theo chiều ngang
                    binding.colorList.setLayoutManager(
                            new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false)
                    );

                    getSize();
                    loadReviews(item.getId());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }
    private void loadReviews(Long productId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<ReviewResponse>> call = apiService.getReviewsByProductId(productId);
        call.enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (allReviews == null) {
                        allReviews = new ArrayList<>();
                    }
                    allReviews.clear();
                    allReviews.addAll(response.body());
                    if(allReviews.isEmpty())
                    {
                        binding.tvNoReviews.setVisibility(View.VISIBLE);
                    }
                    reviewList.clear();
                   // reviewList.addAll(response.body());
                    reviewList.addAll(allReviews.subList(0, Math.min(allReviews.size(), 2)));
                    reviewAdapter.notifyDataSetChanged();

                    // Update average rating
//                    if (!reviewList.isEmpty()) {
//                        double averageRating = reviewList.stream()
//                                .mapToInt(ReviewResponse::getRating)
//                                .average()
//                                .orElse(0.0);
//                        binding.ratingTxt.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
//                    } else {
//                        binding.ratingTxt.setText("0");
//                    }
                } else {
                    AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Không thể tải đánh giá");

                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                AppUtils.showDialogNotify(DetailActivity.this, R.drawable.error,"Lỗi khi tải đánh giá: " + t.getMessage());

            }
        });
    }
    private void checkFavoriteStatus(Long colorId) {
        if (favoriteStatusCache.containsKey(colorId)) {
            binding.favBtn.setIconResource(favoriteStatusCache.get(colorId) ? R.drawable.favorite_fill : R.drawable.favorite);
            return;
        }
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Boolean> call = apiService.checkFavorite(colorId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body();
                    favoriteStatusCache.put(colorId, isFavorite);
                    if (isFavorite) {
                        binding.favBtn.setIconResource(R.drawable.favorite_fill);
                    } else {
                        binding.favBtn.setIconResource(R.drawable.favorite);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("CheckFavoriteError", "Lỗi kiểm tra favorite: " + t.getMessage());
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
    private void showLoading()
    {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(false);
    }

    private void hideLoading(){
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
    }
}