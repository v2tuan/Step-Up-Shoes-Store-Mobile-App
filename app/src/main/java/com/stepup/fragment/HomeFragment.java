package com.stepup.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepup.R;
import com.stepup.activity.MainActivity;
import com.stepup.adapter.BannerAdapter;
import com.stepup.adapter.ProductCardAdapter;
import com.stepup.databinding.ActivityMainBinding;
import com.stepup.databinding.FragmentHomeBinding;
import com.stepup.model.Banner;
import com.stepup.model.ProductCard;
import com.stepup.model.ZoomOutPageTransformer;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (homeViewModel.getBannerList().getValue() == null) {
            homeViewModel.fetchBanners();
        }

        if (homeViewModel.getProductList().getValue() == null) {
            homeViewModel.fetchProducts();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Lắng nghe sự kiện kéo để làm mới
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Xóa dữ liệu cũ
            homeViewModel.clearData();
            // khi clear thì sự  kiện lắng nghe thay đổi dữ liệu sẽ được kích hoạt và ẩn processBar
            // nên ta bật lại ProcessBar để người dùng biết là đang load lại dữ liệu

            binding.progressBarBanner.setVisibility(View.VISIBLE);
            binding.progressBarPopular.setVisibility(View.VISIBLE);

            // Cập nhật giao diện để ẩn dữ liệu cũ
            // binding.viewpagerslider.setAdapter(new BannerAdapter(new ArrayList<>()));
            // binding.viewPopular.setAdapter(new ProductCardAdapter(new ArrayList<>()));

            homeViewModel.fetchBanners();
            homeViewModel.fetchProducts();
        });

        // Theo dõi danh sách Banner
        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null) {
                binding.progressBarBanner.setVisibility(View.GONE);
                binding.viewpagerslider.setAdapter(new BannerAdapter(banners));
            }
            binding.swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng làm mới
        });

        // Theo dõi danh sách ProductCard
        homeViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                binding.progressBarPopular.setVisibility(View.GONE);
                binding.viewPopular.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                binding.viewPopular.setAdapter(new ProductCardAdapter(products));
            }
            binding.swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng làm mới
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding để tránh memory leak
    }
}