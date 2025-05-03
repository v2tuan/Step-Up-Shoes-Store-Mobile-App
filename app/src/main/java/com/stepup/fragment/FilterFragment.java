package com.stepup.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.stepup.R;
import com.stepup.adapter.ColorFilterAdapter;
import com.stepup.databinding.FragmentFilterBinding;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {

    private FragmentFilterBinding binding;
    private ColorFilterAdapter colorAdapter;
    private String selectedColor = null;
    private String selectedRating = "All";
    private String selectedPrice = "All";
    private String query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);

        setupColorRecyclerView();
        loadColorsFromApi();
        if (getArguments() != null) {
            query = getArguments().getString("query");
        }


        // Áp dụng filter khi nhấn nút
        binding.applyBtn.setOnClickListener(view -> {
            int priceId = binding.priceRadioGroup.getCheckedRadioButtonId();
            int ratingId = binding.ratingRadioGroup.getCheckedRadioButtonId();
            if (priceId != -1) {
                RadioButton priceBtn = binding.getRoot().findViewById(priceId);
                if (priceBtn != null) selectedPrice = priceBtn.getText().toString();
            }

            if (ratingId != -1) {
                RadioButton ratingBtn = binding.getRoot().findViewById(ratingId);
                if (ratingBtn != null) selectedRating = ratingBtn.getText().toString();
            }
            String selectedColor = colorAdapter.getSelectedColor();
            // Gửi Bundle sang SearchResultFragment
            Bundle resultBundle = new Bundle();
            resultBundle.putString("query", query);
            resultBundle.putString("selectedColor", selectedColor);
            resultBundle.putString("selectedRating", selectedRating);
            resultBundle.putString("selectedPrice", selectedPrice);
            Log.d("FILTER", "Color: " + selectedColor + ", Price: " + selectedPrice + ", Rating: " + selectedRating);
            getParentFragmentManager().setFragmentResult("filterRequestKey", resultBundle);
            getParentFragmentManager().popBackStackImmediate();

        });

        return binding.getRoot();
    }

    private void setupColorRecyclerView() {
        binding.recyclerViewColors.setLayoutManager(new GridLayoutManager(getContext(), 5)); // 5 item mỗi hàng
        // Adapter sẽ được set sau khi có dữ liệu từ API
    }

    private void loadColorsFromApi() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getColor().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> colorList = response.body();

                    colorAdapter = new ColorFilterAdapter(getContext(), colorList, colorName -> {
                        selectedColor = colorName;
                        Log.d("FILTER", "Color: " + selectedColor );

                    });

                    binding.recyclerViewColors.setAdapter(colorAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // TODO: xử lý lỗi mạng nếu cần
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

