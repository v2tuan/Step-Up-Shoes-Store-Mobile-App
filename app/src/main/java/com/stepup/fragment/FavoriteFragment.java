package com.stepup.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepup.adapter.FavoriteAdapter;
import com.stepup.databinding.FragmentFavoriteBinding;
import com.stepup.model.Favorite;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    public FavoriteFragment() {
        // Required empty public constructor
    }


    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container,false);
        showLoading();

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Favorite>> callFavorite =apiService.getAllFavoriteItem();
        callFavorite.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if (!isAdded() || getContext() == null) return; // 🔒 Ngăn lỗi fragment bị detach
                hideLoading();
                if(response.isSuccessful()&& response.body()!= null )
                {
                    List<Favorite> favoriteItems = response.body();
                    System.out.println("Favorite Items: " + favoriteItems);
                    binding.viewFavorite.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                    FavoriteAdapter favoriteAdapter = new FavoriteAdapter(favoriteItems, binding);
                    binding.viewFavorite.setAdapter(favoriteAdapter);

                    if (favoriteItems.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                        binding.scrollView2.setVisibility(View.GONE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                        binding.scrollView2.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                hideLoading();
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
//        binding.btnSendAllToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                APIService apiService1 = RetrofitClient.getRetrofit().create(APIService.class);
//                showLoading();
//
//            }
//        });
        return binding.getRoot();
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