package com.stepup.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stepup.R;


public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageView searchIcon = view.findViewById(R.id.searchView);
        searchIcon.setOnClickListener(v -> {
            // Tạo instance của SearchInputFragment
            SearchInputFragment searchInputFragment = new SearchInputFragment();

            // Thay thế Fragment hiện tại bằng SearchInputFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,  // hiệu ứng fragment mới xuất hiện
                    R.anim.exit_to_left,      // hiệu ứng fragment hiện tại biến mất
                    R.anim.enter_from_left,   // hiệu ứng khi nhấn back (fragment cũ quay lại)
                    R.anim.exit_to_right      // hiệu ứng fragment hiện tại biến mất khi nhấn back
            );
            transaction.replace(R.id.fragment_container, searchInputFragment, SearchInputFragment.class.getSimpleName());
            transaction.addToBackStack(null); // Thêm vào Back Stack để quay lại
            transaction.commit();
        });

        return view;
    }
}