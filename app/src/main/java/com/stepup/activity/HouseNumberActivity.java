package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.stepup.R;
import com.stepup.adapter.SuggestionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HouseNumberActivity extends AppCompatActivity {

    private EditText etHouseNumberInput;
    private ImageView btnBack, btnClear;
    private RecyclerView rvSuggestions;
    private SuggestionAdapter suggestionAdapter;
    private List<String> suggestions;

    private String firstAddress;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_number);

        etHouseNumberInput = findViewById(R.id.etHouseNumberInput);
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        rvSuggestions = findViewById(R.id.rvSuggestions);

        // Nhận chuỗi địa chỉ đầy đủ từ Intent
        firstAddress = getIntent().getStringExtra("firstAddress");

        // Khởi tạo Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB4hfY11JIyUF-6b-hQM2zu0oPowjq7l6Q"); // Thay YOUR_API_KEY bằng API key của bạn
        }
        placesClient = Places.createClient(this);
        sessionToken = AutocompleteSessionToken.newInstance();

        // Khởi tạo danh sách gợi ý
        suggestions = new ArrayList<>();
        suggestionAdapter = new SuggestionAdapter(suggestions, suggestion -> {
            // Khi người dùng chọn một gợi ý, trả kết quả về MainActivity
            Intent result = new Intent();
            result.putExtra("houseNumber", suggestion);
            setResult(RESULT_OK, result);
            finish();
        });
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestions.setAdapter(suggestionAdapter);

        // Xử lý sự kiện nhập liệu
        etHouseNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Nút quay lại
        btnBack.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("houseNumber", etHouseNumberInput.getText().toString());
            setResult(RESULT_OK, result);
            finish();
        });

        // Nút xóa
        btnClear.setOnClickListener(v -> etHouseNumberInput.setText(""));
    }

    private void fetchSuggestions(String query) {
        // Tạo yêu cầu tìm kiếm gợi ý với Places API
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS) // Chỉ tìm kiếm địa chỉ
                .setSessionToken(sessionToken)
                .setQuery(query + ", " + firstAddress) // Kết hợp query với fullAddress để lọc gợi ý
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            List<String> newSuggestions = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                newSuggestions.add(prediction.getFullText(null).toString());
            }
            suggestions.clear();
            suggestions.addAll(newSuggestions);
            suggestionAdapter.updateList(suggestions);
        }).addOnFailureListener(exception -> {
            // Xử lý lỗi (nếu có)
            exception.printStackTrace();
        });
    }
}