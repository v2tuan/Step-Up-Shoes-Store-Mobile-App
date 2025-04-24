package com.stepup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.stepup.R;


public class SearchInputFragment extends Fragment {

    private SearchView searchView;
    private ImageView clearButton;
    private ListView suggestionsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_input, container, false);

        // Khởi tạo các thành phần
        ImageView backButton = view.findViewById(R.id.back_button);
        searchView = view.findViewById(R.id.search_view_input);
        clearButton = view.findViewById(R.id.clear_button);
        suggestionsList = view.findViewById(R.id.suggestions_list);

        // Thiết lập danh sách gợi ý
        String[] suggestions = {
                "Basketball Shoes",
                "Football Shoes",
                "Tennis Shoes",
                "Shoes",
                "Running Shoes",
                "Basketball Shoes Pink",
                "Shoe"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                suggestions
        );
        suggestionsList.setAdapter(adapter);

        // Xử lý nút quay lại
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(); // Quay lại Fragment trước đó
        });

        // Xử lý sự kiện khi nhập tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn Enter, chuyển đến SearchResultFragment
                SearchResultFragment searchResultFragment = new SearchResultFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                searchResultFragment.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, searchResultFragment, SearchResultFragment.class.getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    clearButton.setVisibility(View.GONE);
                } else {
                    clearButton.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        // Xử lý nút xóa
        clearButton.setOnClickListener(v -> {
            searchView.setQuery("", false);
            clearButton.setVisibility(View.GONE);
            searchView.requestFocus();
        });

        // Tự động mở bàn phím
        searchView.requestFocus();

        return view;
    }
}