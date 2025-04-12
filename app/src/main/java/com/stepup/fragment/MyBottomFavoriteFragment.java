package com.stepup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stepup.databinding.FragmentMyBottomFavoriteBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class MyBottomFavoriteFragment extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_PRICE = "price";
    private static final String ARG_IMAGE_URL = "image_url";

    private FragmentMyBottomFavoriteBinding binding;

    public MyBottomFavoriteFragment() {
        // Required empty public constructor
    }

    public static MyBottomFavoriteFragment newInstance(String title, String price, String imageUrl) {
        MyBottomFavoriteFragment fragment = new MyBottomFavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_PRICE, price);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBottomFavoriteBinding.inflate(inflater, container, false);

        // Lấy dữ liệu từ arguments
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String price = args.getString(ARG_PRICE);
            String imageUrl = args.getString(ARG_IMAGE_URL);

            // Hiển thị dữ liệu
            binding.titleTxt.setText(title);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.priceTxt.setText(format.format(Double.parseDouble(price)));
            Glide.with(this)
                    .load(imageUrl)
                    .into(binding.pic);
        }

        return binding.getRoot();
    }
}