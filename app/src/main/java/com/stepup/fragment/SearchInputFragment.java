package com.stepup.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.ArrayAdapter;

import com.stepup.R;
import com.stepup.databinding.FragmentSearchInputBinding;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchInputFragment extends Fragment {

    private FragmentSearchInputBinding binding;
    private ArrayAdapter<String> suggestionsAdapter;
    private List<String> allSuggestions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchInputBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Back button
        binding.backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        // Gọi API để lấy toàn bộ suggestions ngay khi load
        loadAllSuggestions();

        // Xử lý khi người dùng nhập vào search input
        binding.searchViewInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                navigateToSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    binding.suggestionsList.setVisibility(View.GONE);
                } else {
                    filterSuggestions(newText);
                }
                return true;
            }
        });

        // Xử lý khi người dùng chọn một gợi ý
        binding.suggestionsList.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = suggestionsAdapter.getItem(position);
            if (selectedItem != null) {
                binding.searchViewInput.setQuery(selectedItem, false);
                navigateToSearchResults(selectedItem);
            }
        });

        binding.searchViewInput.requestFocus();
        binding.searchViewInput.setIconified(false);

        return view;
    }

    private void loadAllSuggestions() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getSearchSuggestions().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSuggestions.clear();
                    allSuggestions.addAll(response.body());
                    filterSuggestions(""); // Hiển thị 5 gợi ý đầu tiên
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("SuggestionError", "Error loading suggestions: " + t.getMessage());
            }
        });
    }

    private void filterSuggestions(String query) {
        List<String> filtered = new ArrayList<>();

        // Tìm những suggestion chứa từ khóa (không phân biệt hoa thường)
        for (String suggestion : allSuggestions) {
            if (suggestion.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(suggestion);
            }
        }

        // Ưu tiên suggestion gần giống hơn ở đầu
        Collections.sort(filtered, Comparator.comparingInt(s -> s.toLowerCase().indexOf(query.toLowerCase())));

        // Giới hạn tối đa 5 kết quả
        if (filtered.size() > 5) {
            filtered = filtered.subList(0, 5);
        }

        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filtered);
        binding.suggestionsList.setAdapter(suggestionsAdapter);
        binding.suggestionsList.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void navigateToSearchResults(String query) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        searchResultFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchResultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
