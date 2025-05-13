package com.stepup.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.databinding.ActivityAddAddressBinding;
import com.stepup.model.ApiResponse;
import com.stepup.model.location.AddressRequest;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityAddAddressBinding binding;

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private String firstAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Sự kiện quay lại
        binding.btnBack.setOnClickListener(v -> finish());

        // Xử lý khi mất focus khỏi etHouseNumber
        binding.etHouseNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String houseNumber = binding.etHouseNumber.getText().toString().trim();
                if (!houseNumber.isEmpty() && firstAddress != null && !firstAddress.isEmpty()) {
                    // Kết hợp houseNumber và firstAddress để tạo địa chỉ đầy đủ
                    String fullAddress = houseNumber + "," + firstAddress;
                    binding.etAddress.setText(fullAddress); // Hiển thị địa chỉ đầy đủ trong etAddress

                    // Lấy tọa độ từ địa chỉ đầy đủ và hiển thị trên bản đồ
                    selectedLatLng = getLatLngFromAddress(fullAddress);
                    if (selectedLatLng != null) {
                        showMapLocation(selectedLatLng, fullAddress);
                    } else {
                        AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Không tìm thấy tọa độ cho địa chỉ này"  );
                    }
                } else {
                    if (houseNumber.isEmpty()) {
                        AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Vui lòng nhập số nhà"  );
                    } else if (firstAddress == null || firstAddress.isEmpty()) {
                        AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Vui lòng chọn tỉnh, huyện, xã"  );
                    }
                }
            }
        });

        // Khởi tạo Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        mapFragment.getMapAsync(this);

        // Chọn vị trí từ MapsActivity
//        binding.btnSelectFromMap.setOnClickListener(v -> {
//            Intent mapIntent = new Intent(AddAddressActivity.this, MapsActivity.class);
//            startActivityForResult(mapIntent, REQUEST_LOCATION);
//        });

        binding.btnComplete.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Vui lòng nhập đầy đủ thông tin"  );
                return;
            }
            showLoading();
            com.stepup.model.Address addressDTO = new com.stepup.model.Address(name, phoneNumber, address);
            boolean isDefault = binding.setDefaut.isChecked();
            AddressRequest addressRequest = new AddressRequest(addressDTO, isDefault);
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            apiService.createAddress(addressRequest).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse responseObject = response.body();
                        if (responseObject.getMessage().equals("Created new address successfully")) {
                           // AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,responseObject.getMessage()  );
                            hideLoading();
                            finish();
                        } else {
                            hideLoading();
                            AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,responseObject.getMessage()  );
                        }
                    } else {
                        hideLoading();
                        AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Lỗi: " + response.message()  );
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    hideLoading();
                    AppUtils.showDialogNotify(AddAddressActivity.this, R.drawable.error,"Lỗi kết nối: " + t.getMessage() );

                }
            });
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String source = data.getStringExtra("source");
            if ("SelectLocation".equals(source)) {
                String province = data.getStringExtra("province");
                String district = data.getStringExtra("district");
                String ward = data.getStringExtra("ward");
                firstAddress = ward + ", " + district + ", " + province;
                String firstAddress1 = province + ", " + district + ", " + ward;
                binding.etProvinceDistrictWard.setText(firstAddress1);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (selectedLatLng != null) {
            showMapLocation(selectedLatLng, binding.etAddress.getText().toString());
        }
    }
    // Hàm lấy tọa độ từ địa chỉ
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Hàm hiển thị vị trí trên bản đồ
    private void showMapLocation(LatLng latLng, String title) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
    public void onProvinceDistrictWardClick(View view) {
        Intent intent = new Intent(this, SelectLocationActivity.class);
        intent.putExtra("source", "SelectLocation");
        startActivityForResult(intent, 0);
    }
    private void showLoading() {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
    }
}