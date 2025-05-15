package com.stepup.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.DetailActivity;
import com.stepup.activity.MainActivity;
import com.stepup.adapter.ColorAdapter;
import com.stepup.adapter.SizeAdapter;
import com.stepup.databinding.FragmentMyBottomFavoriteBinding;
import com.stepup.model.AddToCartDTO;
import com.stepup.model.Product;
import com.stepup.model.ProductVariant;
import com.stepup.model.Size;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.FavoriteViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBottomFavoriteFragment extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_COLOR = "color";
    private static final String ARG_PRICE = "price";
    private static final String ARG_IMAGE_URL = "image_url";

    private static final String ARG_COLOR_ID   = "arg_color_id";
    private   List<ProductVariant> variants;
    private FragmentMyBottomFavoriteBinding binding;
    private FavoriteViewModel viewModel;

    public MyBottomFavoriteFragment() {
        // Required empty public constructor
    }

    public static MyBottomFavoriteFragment newInstance(String title, String color,String price, String imageUrl, long id) {
        MyBottomFavoriteFragment fragment = new MyBottomFavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLOR,color);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_PRICE, price);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putLong(ARG_COLOR_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentMyBottomFavoriteBinding.inflate(inflater, container, false);
        // Lấy dữ liệu từ arguments
        showLoading();
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String price = args.getString(ARG_PRICE);
            String imageUrl = args.getString(ARG_IMAGE_URL);
            long colorId = getArguments().getLong(ARG_COLOR_ID);
            String color = args.getString(ARG_COLOR);
            // Hiển thị dữ liệu
            binding.colorTxt.setText(color);
            binding.titleTxt.setText(title);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.priceTxt.setText(format.format(Double.parseDouble(price)));

            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.pic);
            Product cachedProduct = viewModel.getProductByColorId(colorId);
            if (cachedProduct != null) {
                List<Size> sizes = extractSizes(cachedProduct);
                displaySizes(sizes,cachedProduct);
            } else {
                fetchVariants(colorId);
            }
        }
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SizeAdapter.sizeSelected == null){
                    AppUtils.showDialogNotify(requireActivity(), R.drawable.error, "Please select size! ");
                    return;
                }
                Long productVariant_id = null;
                for(ProductVariant variant: variants){
                    if(variant.getColor().getId().equals(getArguments().getLong(ARG_COLOR_ID)) && variant.getSize().equals(SizeAdapter.sizeSelected)){
                        productVariant_id = variant.getId();
                        break;
                    }
                }
                showLoading();
              //  long variantId = SizeAdapter.sizeSelected.getId(); dang o day chua fix xong nut add to cart
                AddToCartDTO addToCartDTO = new AddToCartDTO(productVariant_id, 1);
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<String> callAddCart = apiService.addCart(addToCartDTO);
                callAddCart.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            AppUtils.showDialogNotify(requireActivity(), R.drawable.ic_tick,response.body());
                            Log.d("Add To Cart", "Message: : " + response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        hideLoading();
                        AppUtils.showDialogNotify(requireActivity(), R.drawable.error,"Error: " + t.getMessage());
                        Log.e("RetrofitError", "Error: " + t.getMessage());
                    }
                });
            }
        });
        return binding.getRoot();
    }
    private List<Size> extractSizes(Product product) {
        if (product == null) {
            Log.e("extractSizes", "Product is null");
            return new ArrayList<>();
        }
        variants = product.getProductVariants();
        Set<Size> sizes = new HashSet<>(); // Sử dụng Set để loại bỏ trùng lặp
        for (ProductVariant v : variants) {
            Size s = v.getSize();
            if (s != null) {
                sizes.add(s);
            }
        }
        return new ArrayList<>(sizes);
    }
    private void fetchVariants(long colorId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductByColorId(colorId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!isAdded()) return;
                hideLoading();

                if (response.isSuccessful()) {
                    // Kiểm tra response.body() hoặc response.errorBody() nếu cần
                    Log.d("Response", response.body().toString());
                } else {
                    // Xử lý lỗi, ví dụ như 404, 500
                    Log.e("Error", response.errorBody().toString());
                }
                if(response.isSuccessful() && response.body() != null)
                {
                    Product product = response.body();
                    viewModel.cacheProduct(colorId, product); // Lưu vào ViewModel
                    List<Size> sizes = extractSizes(product);
                    displaySizes(sizes,product);

                }
                else
                        {
                            Toast.makeText(requireContext(),
                                    "Lỗi server: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("API_ERROR", "fetchVariants", t);
                       Toast.makeText(requireContext(),
                               "Không thể kết nối server",
                               Toast.LENGTH_SHORT).show();
            }
        });
    }
    // chua fix xong , con loi phan product khong tim thay, doi ProductVarient thanh Product thu xem
    private void displaySizes(List<Size> sizes,Product product) {
        if (product == null) {
            Log.e("displaySizes", "Product is null");
            return;
        }
        ArrayList<String> sizeNames = new ArrayList<>();
        for (Size s : sizes) {
            sizeNames.add(s.getName());
        }
        // Khởi tạo adapter (giả sử SizeAdapter nhận List<String>)
        SizeAdapter adapter = new SizeAdapter(sizeNames,product );
        // Gán LayoutManager và Adapter
        binding.rvSize.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );
        binding.rvSize.setAdapter(adapter);
    }
    private void showLoading() {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(true);
        binding.btnAddToCart.setVisibility(View.GONE);// Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
        binding.btnAddToCart.setVisibility(View.VISIBLE);
    }

}