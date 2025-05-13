package com.stepup.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
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
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.databinding.ActivityAddAddressBinding;
import com.stepup.databinding.ActivityEditAddressBinding;
import com.stepup.model.ApiResponse;
import com.stepup.model.location.AddressRequest;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityEditAddressBinding binding; // Khai báo View Binding
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private String firstAddress;
    Long addressId;
    private Runnable pendingUpdateTask;
    private final long DEBOUNCE_DELAY = 500; // 500ms trì hoãn
    private boolean isMapReady = false;
    private String pendingAddress = null;
    private final Map<String, LatLng> addressCache = new HashMap<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // chạy map dưới background thread
    private void getLatLngFromAddressAsync(String address, OnLatLngReceived callback) {
        if (addressCache.containsKey(address)) {
            mainHandler.post(() -> callback.onReceived(addressCache.get(address)));
            return;
        }
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(EditAddressActivity.this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address location = addressList.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mainHandler.post(() -> callback.onReceived(latLng));
                } else {
                    mainHandler.post(() -> callback.onReceived(null));
                }
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onReceived(null));
            }
        });
    }

    interface OnLatLngReceived {
        void onReceived(LatLng latLng);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo View Binding
        binding = ActivityEditAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        addressId = intent.getLongExtra("addressId", -1);
        String name = intent.getStringExtra("name");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String address = intent.getStringExtra("address");
        boolean isDefaut = intent.getBooleanExtra("default", false);
        if (isDefaut) {
            binding.setDefaut.setChecked(true);
        }
        binding.etName.setText(name);
        binding.etPhoneNumber.setText(phoneNumber);
        binding.etAddress.setText(address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        mapFragment.getMapAsync(this);
        // Tách địa chỉ
        if(address != null )
        {
            splitAddress(address);
            pendingAddress = address;
        }

        // Quay lại
        binding.btnBack.setOnClickListener(v -> finish());

        // Lắng nghe khi mất focus ở ô số nhà
        binding.etHouseNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Hủy các cập nhật đang chờ
                if (pendingUpdateTask != null) {
                    mainHandler.removeCallbacks(pendingUpdateTask);
                }
                // Lên lịch cập nhật mới với giảm tần suất
                pendingUpdateTask = () -> updateFullAddress();
                mainHandler.postDelayed(pendingUpdateTask, DEBOUNCE_DELAY);
            }
        });


        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                apiService.deleteAddress(addressId).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if ("Xóa địa chỉ thành công".equals(response.body().getMessage())) {
                                setResult(RESULT_OK);
                                hideLoading();
                              //  AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.ic_check,"Xóa địa chỉ thành công"  );
                                finish();
                            }
                        } else {
                            hideLoading();
                            AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Lỗi: " + response.message()  );

                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        hideLoading();
                        AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Lỗi kết nối: " + t.getMessage()  );
                    }
                });
            }
        });
        // Gửi cập nhật
        binding.btnComplete.setOnClickListener(v -> updateAddressAPI());
    }

    private void splitAddress(String address) {
        if (address == null || address.isEmpty()) return;

        String[] keywords = {"Phường", "Xã"};
        int splitIndex = -1;

        for (String keyword : keywords) {
            splitIndex = address.indexOf(keyword);
            if (splitIndex != -1) break;
        }

        String houseNumber = "";
        firstAddress = "";

        if (splitIndex != -1) {
            houseNumber = address.substring(0, splitIndex).trim();
            firstAddress = address.substring(splitIndex).trim();
        } else if (address.contains(",")) {
            String[] parts = address.split(", ", 2);
            if (parts.length == 2) {
                houseNumber = parts[0].trim();
                firstAddress = parts[1].trim();
            } else {
                houseNumber = address.trim();
            }
        } else {
            houseNumber = address.trim();
        }

        binding.etHouseNumber.setText(houseNumber);
        binding.etProvinceDistrictWard.setText(firstAddress);
    }

    private void updateFullAddress() {
        String houseNumber = binding.etHouseNumber.getText().toString().trim();

        if (houseNumber.isEmpty()) {
            AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Vui lòng nhập số nhà"  );
            return;
        }

        if (firstAddress == null || firstAddress.isEmpty()) {
            AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Vui lòng chọn tỉnh, huyện, xã"  );
            return;
        }

        String fullAddress = houseNumber + ", " + firstAddress;
        binding.etAddress.setText(fullAddress);

        getLatLngFromAddressAsync(fullAddress, latLng -> {
            if (latLng != null) {
                selectedLatLng = latLng;
                showMapLocation(selectedLatLng, fullAddress);
            } else {
                AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Không tìm thấy tọa độ cho địa chỉ này"  );
            }
        });
    }

    private void updateAddressAPI() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhoneNumber.getText().toString().trim();
        String fullAddress = binding.etAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || fullAddress.isEmpty()) {
            AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Vui lòng nhập đầy đủ thông tin"  );
            return;
        }
        showLoading();
        boolean isDefault = binding.setDefaut.isChecked();
        com.stepup.model.Address addressDTO = new com.stepup.model.Address(name, phone, fullAddress);
        AddressRequest addressRequest = new AddressRequest(addressDTO,isDefault);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateAddress(addressId, addressRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("Cập nhật địa chỉ thành công".equals(response.body().getMessage())) {
                        setResult(RESULT_OK);
                        hideLoading();
                       // AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.ic_check,"Cập nhật địa chỉ thành công"  );
                        finish();
                    }
                } else {
                    hideLoading();
                    AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Lỗi: " + response.message()  );

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoading();
                AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Lỗi kết nối: " + t.getMessage()  );

            }
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

                String fullAddress = province + ", " + district + ", " + ward;
                binding.etProvinceDistrictWard.setText(fullAddress);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (pendingAddress != null) {
            getLatLngFromAddressAsync(pendingAddress, latLng -> {
                if (latLng != null) {
                    selectedLatLng = latLng;
                    showMapLocation(selectedLatLng, pendingAddress);
                } else {
                    AppUtils.showDialogNotify(EditAddressActivity.this, R.drawable.error,"Không tìm thấy tọa độ cho địa chỉ này"  );
                }
            });
        }
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