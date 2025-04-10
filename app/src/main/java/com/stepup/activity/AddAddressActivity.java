package com.stepup.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
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
import com.stepup.R;
import com.stepup.databinding.ActivityAddAddressBinding;
import com.stepup.model.ApiResponse;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityAddAddressBinding binding; // Khai báo View Binding
    private static final int REQUEST_LOCATION = 100;
    private static final int REQUEST_CODE_SELECT_LOCATION = 101;
    private static final int REQUEST_CODE_HOUSE_NUMBER = 102;
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private String firstAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
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
                    String fullAddress = houseNumber + ", " + firstAddress;
                    binding.etAddress.setText(fullAddress); // Hiển thị địa chỉ đầy đủ trong etAddress

                    // Lấy tọa độ từ địa chỉ đầy đủ và hiển thị trên bản đồ
                    selectedLatLng = getLatLngFromAddress(fullAddress);
                    if (selectedLatLng != null) {
                        showMapLocation(selectedLatLng, fullAddress);
                    } else {
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy tọa độ cho địa chỉ này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (houseNumber.isEmpty()) {
                        Toast.makeText(AddAddressActivity.this, "Vui lòng nhập số nhà", Toast.LENGTH_SHORT).show();
                    } else if (firstAddress == null || firstAddress.isEmpty()) {
                        Toast.makeText(AddAddressActivity.this, "Vui lòng chọn tỉnh, huyện, xã", Toast.LENGTH_SHORT).show();
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

        // Hoàn thành
        binding.btnComplete.setOnClickListener(v -> {
            String address = binding.etAddress.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Địa chỉ đã được lưu!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnComplete.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo AddressDTO để gửi lên API
            com.stepup.model.Address addressDTO = new com.stepup.model.Address(name, phoneNumber, address);
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            // Gửi yêu cầu POST đến API
            apiService.createAddress(addressDTO).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse responseObject = response.body();
                        if (responseObject.getMessage().equals("Created new address successfully")) {
                            Toast.makeText(AddAddressActivity.this, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại màn hình trước sau khi lưu thành công
                        } else {
                            Toast.makeText(AddAddressActivity.this, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddAddressActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(AddAddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                String address = data.getStringExtra("address");
                binding.etAddress.setText(address);

                // Hiển thị địa chỉ trên bản đồ
                selectedLatLng = getLatLngFromAddress(address);
                if (mMap != null && selectedLatLng != null) {
                    showMapLocation(selectedLatLng, address);
                }
            } else if (requestCode == REQUEST_CODE_SELECT_LOCATION) {
                String province = data.getStringExtra("province");
                String district = data.getStringExtra("district");
                String ward = data.getStringExtra("ward");
                firstAddress = ward + ", " + district + ", " + province;
                // Hiển thị lên EditText
                String fullAddress = province + ", " + district + ", " + ward;
                binding.etProvinceDistrictWard.setText(fullAddress);
            } else if (requestCode == REQUEST_CODE_HOUSE_NUMBER) {
                // Nhận kết quả từ HouseNumberActivity
                String houseNumber = data.getStringExtra("houseNumber");
                binding.etHouseNumber.setText(houseNumber);
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
        startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATION);
    }
}