package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stepup.R;
import com.stepup.adapter.LocationAdapter;
import com.stepup.model.location.District;
import com.stepup.model.location.DistrictResponse;
import com.stepup.model.location.Province;
import com.stepup.model.location.ProvinceResponse;
import com.stepup.model.location.Ward;
import com.stepup.model.location.WardResponse;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

public class SelectLocationActivity extends AppCompatActivity {

    private RecyclerView rvLocations;
    private EditText etSearch;
    private ProgressBar progressBar;
    private LocationAdapter adapter;
    private LinearLayout llSelectedLocation;
    private TextView tvSelectedProvince, tvSelectedDistrict, tvSelectedWard;

    private List<String> currentList = new ArrayList<>();
    private String currentLevel = "province";
    private Province selectedProvince;
    private District selectedDistrict;
    private List<Province> provinces1 = new ArrayList<>();
    private List<District> districts1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        llSelectedLocation = findViewById(R.id.llSelectedLocation);
        tvSelectedProvince = findViewById(R.id.tvSelectedProvince);
        tvSelectedDistrict = findViewById(R.id.tvSelectedDistrict);
        tvSelectedWard = findViewById(R.id.tvSelectedWard);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        progressBar = findViewById(R.id.progressBar);

        rvLocations = findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(currentList, location -> {
            switch (currentLevel) {
                case "province":
                    selectedProvince = findProvinceByName(location);
                    currentLevel = "district";
                    etSearch.setText("");
                    updateSelectedLocationDisplay();
                    fetchDistricts(selectedProvince.getCode());
                    break;
                case "district":
                    selectedDistrict = findDistrictByName(location);
                    currentLevel = "ward";
                    etSearch.setText("");
                    updateSelectedLocationDisplay();
                    fetchWards(selectedDistrict.getCode());
                    break;
                case "ward":
                    Intent result = new Intent();
                    result.putExtra("province", selectedProvince.getName_with_type());
                    result.putExtra("district", selectedDistrict.getName_with_type());
                    result.putExtra("ward", location);
                    setResult(RESULT_OK, result);
                    finish();
                    break;
            }
        });
        rvLocations.setAdapter(adapter);

        fetchProvinces();
    }

    private void updateSelectedLocationDisplay() {
        llSelectedLocation.setVisibility(View.VISIBLE);

        if (selectedProvince != null) {
            String provinceName = selectedProvince.getName()
                    .replace("Tỉnh ", "")
                    .replace("Thành phố ", "");
            tvSelectedProvince.setText("Tỉnh/Thành Phố " + provinceName);
            tvSelectedProvince.setVisibility(View.VISIBLE);
        } else {
            tvSelectedProvince.setVisibility(View.GONE);
        }

        if (selectedDistrict != null) {
            String districtName = selectedDistrict.getName()
                    .replace("Huyện ", "")
                    .replace("Quận ", "")
                    .replace("Thị xã ", "");
            tvSelectedDistrict.setText("Huyện/Quận " +  districtName);
            tvSelectedDistrict.setVisibility(View.VISIBLE);
        } else {
            tvSelectedDistrict.setVisibility(View.GONE);
        }

        tvSelectedWard.setOnClickListener(v -> {
            if (currentLevel.equals("ward")) {
                etSearch.setText("");
                fetchWards(selectedDistrict.getCode());
            }
        });
    }

    private void fetchProvinces() {
        showLoading(true);
        APIService apiService = RetrofitClient.getLocationRetrofit().create(APIService.class);
        apiService.getProvinces(-1).enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getExitcode() == 1) {
                    List<Province> provinces = response.body().getData().getData();
                    provinces1 = response.body().getData().getData();
                    currentList.clear();
                    currentList.add("#TỈNH/THÀNH PHỐ");
                    for (Province province : provinces) {
                        currentList.add(province.getName_with_type());
                    }
                    updateList();
                } else {
                    showError("Không thể tải danh sách tỉnh");
                }
            }

            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchDistricts(String provinceCode) {
        showLoading(true);
        APIService apiService = RetrofitClient.getLocationRetrofit().create(APIService.class);
        apiService.getDistricts(provinceCode, -1).enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getExitcode() == 1) {
                    List<District> districts = response.body().getData().getData();
                    districts1 = response.body().getData().getData();
                    currentList.clear();
                    currentList.add("#QUẬN/HUYỆN");
                    for (District district : districts) {
                        currentList.add(district.getName_with_type());
                    }
                    updateList();
                } else {
                    showError("Không thể tải danh sách huyện");
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchWards(String districtCode) {
        showLoading(true);
        APIService apiService = RetrofitClient.getLocationRetrofit().create(APIService.class);
        apiService.getWards(districtCode, -1).enqueue(new Callback<WardResponse>() {
            @Override
            public void onResponse(Call<WardResponse> call, Response<WardResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getExitcode() == 1) {
                    List<Ward> wards = response.body().getData().getData();
                    currentList.clear();
                    currentList.add("#PHƯỜNG/XÃ");
                    for (Ward ward : wards) {
                        currentList.add(ward.getName_with_type());
                    }
                    updateList();
                } else {
                    showError("Không thể tải danh sách xã");
                }
            }

            @Override
            public void onFailure(Call<WardResponse> call, Throwable t) {
                showLoading(false);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private Province findProvinceByName(String name) {
        for (Province province : provinces1) {
            if (province.getName_with_type().equals(name)) {
                return province;
            }
        }
        return null;
    }

    private District findDistrictByName(String name) {
        for (District district : districts1) {
            if (district.getName_with_type().equals(name)) {
                return district;
            }
        }
        return null;
    }

    private void updateList() {
        Collections.sort(currentList, (s1, s2) -> {
            if (s1.startsWith("#") || s2.startsWith("#")) return 0;
            return s1.compareTo(s2);
        });
        adapter.updateList(currentList);
        switch (currentLevel) {
            case "province":
                etSearch.setHint("Tìm kiếm Tỉnh/Thành phố");
                break;
            case "district":
                etSearch.setHint("Tìm kiếm Quận/Huyện");
                break;
            case "ward":
                etSearch.setHint("Tìm kiếm Phường/Xã");
                break;
            default:
                etSearch.setHint("Tìm kiếm");
                break;
        }
        rvLocations.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvLocations.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        switch (currentLevel) {
            case "ward":
                currentLevel = "district";
                etSearch.setText("");
                selectedDistrict = null;
                updateSelectedLocationDisplay();
                fetchDistricts(selectedProvince.getCode());
                break;
            case "district":
                currentLevel = "province";
                selectedProvince = null;
                selectedDistrict = null;
                etSearch.setText("");
                updateSelectedLocationDisplay();
                llSelectedLocation.setVisibility(View.GONE);
                fetchProvinces();
                break;
            default:
                super.onBackPressed();
                break;
        }
    }
}