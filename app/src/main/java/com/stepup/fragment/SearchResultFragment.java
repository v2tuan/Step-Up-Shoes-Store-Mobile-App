package com.stepup.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.stepup.R;
import com.stepup.adapter.ProductCardAdapter;
import com.stepup.databinding.FragmentSearchResultBinding;
import com.stepup.model.ProductCard;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.FavoriteViewModel;
import com.stepup.viewModel.ProductViewModel;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultFragment extends Fragment {

    private FragmentSearchResultBinding binding;
    private String query;
    private ProductViewModel viewModel;
    private List<ProductCard> originalProductList;
   // private FavoriteViewModel viewModel1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false);
        query = getArguments() != null ? getArguments().getString("query") : "";
        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        binding.querySearch.setText(query);
        binding.backbtn.setOnClickListener(v -> {
            viewModel.setProductList(new ArrayList<>());
            getParentFragmentManager().popBackStackImmediate();
        });

        binding.searchBtn.setOnClickListener(v -> {
            viewModel.setProductList(new ArrayList<>());
            getParentFragmentManager().popBackStackImmediate();
        });
        binding.fabAction.setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment();
            Bundle args = new Bundle();
            args.putString("query", query); // Truyền query vào Bundle
            filterFragment.setArguments(args);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,  // hiệu ứng fragment mới xuất hiện
                    R.anim.exit_to_left,      // hiệu ứng fragment hiện tại biến mất
                    R.anim.enter_from_left,   // hiệu ứng khi nhấn back (fragment cũ quay lại)
                    R.anim.exit_to_right      // hiệu ứng fragment hiện tại biến mất khi nhấn back
            );
            transaction.replace(R.id.fragment_container, filterFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        getParentFragmentManager().setFragmentResultListener("filterRequestKey", getViewLifecycleOwner(), (requestKey, result) -> {
            setArguments(result);
            query = result.getString("query", "");
            applyFiltersFromBundle();
        });

        setupRecyclerView();
        viewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            originalProductList = products;
            applyFiltersFromBundle();
        });
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.itemView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Chỉ gọi API nếu danh sách sản phẩm chưa được tải
        if (viewModel.getProductList().getValue() == null || viewModel.getProductList().getValue().isEmpty()) {
            showLoading();
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            apiService.searchProducts(query).enqueue(new Callback<List<ProductCard>>() {
                @Override
                public void onResponse(@NonNull Call<List<ProductCard>> call, @NonNull Response<List<ProductCard>> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        viewModel.setProductList(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ProductCard>> call, @NonNull Throwable t) {
                    hideLoading();
                    Log.e("RetrofitError", "Lỗi: " + t.getMessage());
                }
            });
        }
    }

    private void applyFiltersFromBundle() {
        Bundle args = getArguments();
        List<ProductCard> filteredList = new ArrayList<>(originalProductList);

        if (args != null) {
            String selectedColor = args.getString("selectedColor", null);
            String selectedRating = args.getString("selectedRating", "All");
            String selectedPrice = args.getString("selectedPrice", "All");

            // Lọc theo màu sắc
            if (selectedColor != null && !selectedColor.isEmpty() && !"All".equalsIgnoreCase(selectedColor)) {
                filteredList = filteredList.stream()
                        .filter(product -> product.getColor() != null &&
                                product.getColor().stream()
                                        .anyMatch(color -> selectedColor.equalsIgnoreCase(color.getName())))
                        .collect(Collectors.toList());
            }

            // Lọc theo đánh giá
            if (!"All".equalsIgnoreCase(selectedRating)) {
                try {
                    // Xử lý định dạng: bỏ chữ "stars"
                    selectedRating = selectedRating.replace("stars", "").trim();

                    if (selectedRating.startsWith(">")) {
                        double minRating = Double.parseDouble(selectedRating.substring(1).trim());
                        filteredList = filteredList.stream()
                                .filter(product -> product.getRating() > minRating)
                                .collect(Collectors.toList());
                    } else {
                        double rating = Double.parseDouble(selectedRating.trim());
                        filteredList = filteredList.stream()
                                .filter(product -> product.getRating() == rating)
                                .collect(Collectors.toList());
                    }
                } catch (NumberFormatException e) {
                    Log.e("FilterError", "Định dạng đánh giá không hợp lệ: " + selectedRating);
                }
            }

            // Lọc theo giá
            if (!"All".equalsIgnoreCase(selectedPrice)) {
                try {
                    // Loại bỏ tất cả các ký tự không phải số, dấu "<", ">", "-", và "."
                    selectedPrice = selectedPrice.replaceAll("[^0-9><\\-\\.]", "");

                    if (selectedPrice.contains("-")) {
                        // Trường hợp khoảng giá: "2.000.000 - 4.000.000"
                        String[] range = selectedPrice.split("-");
                        double minPrice = Double.parseDouble(range[0].trim().replace(".", ""));
                        double maxPrice = Double.parseDouble(range[1].trim().replace(".", ""));

                        filteredList = filteredList.stream()
                                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                                .collect(Collectors.toList());
                    } else if (selectedPrice.contains("2")) {
                        // Trường hợp giá lớn hơn: "> 2.000.000"
                        String numberOnly = selectedPrice.replaceAll("[^\\d]", ""); // Lấy toàn bộ chữ số
                        double minPrice = Double.parseDouble(numberOnly);
                        filteredList = filteredList.stream()
                                .filter(product -> product.getPrice() < minPrice)
                                .collect(Collectors.toList());
                    } else if (selectedPrice.contains("4")) {
                        // Trường hợp giá bé hơn: "< 4.000.000"
                        String numberOnly = selectedPrice.replaceAll("[^\\d]", ""); // Lấy toàn bộ chữ số
                        double maxPrice = Double.parseDouble(numberOnly);
                        filteredList = filteredList.stream()
                                .filter(product -> product.getPrice() >= maxPrice)
                                .collect(Collectors.toList());
                    } else {
                        // Trường hợp giá cố định: "2.000.000"
                        double price = Double.parseDouble(selectedPrice.replace(".", "").trim());
                        filteredList = filteredList.stream()
                                .filter(product -> product.getPrice() == price)
                                .collect(Collectors.toList());
                    }
                } catch (NumberFormatException e) {
                    Log.e("FilterError", "Định dạng giá không hợp lệ: " + selectedPrice);
                }
            }

        }

        // Cập nhật RecyclerView với danh sách đã lọc
        ProductCardAdapter adapter = new ProductCardAdapter(filteredList);
        binding.itemView.setAdapter(adapter);
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
