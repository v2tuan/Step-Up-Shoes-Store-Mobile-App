package com.stepup.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stepup.model.Banner;
import com.stepup.model.ProductCard;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Banner>> bannerList = new MutableLiveData<>();
    private final MutableLiveData<List<ProductCard>> productList = new MutableLiveData<>();

    public LiveData<List<Banner>> getBannerList() {
        return bannerList;
    }

    public LiveData<List<ProductCard>> getProductList() {
        return productList;
    }

    public void clearData() {
        bannerList.setValue(new ArrayList<>()); // Xóa danh sách banner
        productList.setValue(new ArrayList<>()); // Xóa danh sách sản phẩm

    }


    public void fetchBanners() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Banner>> callBanners = apiService.getBannerAll();
        callBanners.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bannerList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    public void fetchProducts() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<ProductCard>> callProducts = apiService.getProductAll();
        callProducts.enqueue(new Callback<List<ProductCard>>() {
            @Override
            public void onResponse(Call<List<ProductCard>> call, Response<List<ProductCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProductCard>> call, Throwable t) {
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }
}
