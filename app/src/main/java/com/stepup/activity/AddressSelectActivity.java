package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stepup.R;
import com.stepup.adapter.AddressAdapter;
import com.stepup.adapter.AddressSelectAdapter;
import com.stepup.databinding.ActivityAddressSelectBinding;
import com.stepup.model.Address;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressSelectActivity extends AppCompatActivity {
    private ActivityAddressSelectBinding binding;
    private AddressSelectAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private Long defaultAddressId;
    private Long selectedAddressId;
    private ActivityResultLauncher<Intent> addAddressLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddressSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom));
        binding.recyclerViewAddress.addItemDecoration(dividerItemDecoration);
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        selectedAddressId = getIntent().getLongExtra("selectedAddressId", -1);
        getUserAddresses(); // Lúc này chưa khởi tạo adapter
//        addAddressLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//                            // Reload addresses when AddAddressActivity returns successfully
//                            getUserAddresses();
//                        }
//                    }
//                });
        binding.btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressSelectActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });
    }

    private void getUserAddresses() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.getAddressesByUserId();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) data.get("addresses");

                    addressList.clear();
                    for (Map<String, Object> addressMap : addressMaps) {
                        Address address = new Address();
                        Object idValue = addressMap.get("id");
                        address.setId(Long.valueOf(idValue.toString().split("\\.")[0]));
                        address.setFullName(addressMap.get("fullName").toString());
                        address.setPhone(addressMap.get("phone").toString());
                        address.setAddr(addressMap.get("addr").toString());
                        addressList.add(address);
                    }

                    Object idValue = data.get("defaultAddressId");
                    defaultAddressId = (idValue != null) ? Long.valueOf(idValue.toString().split("\\.")[0]) : null;

                    // Khởi tạo adapter SAU KHI đã có dữ liệu
                    addressAdapter = new AddressSelectAdapter(AddressSelectActivity.this, addressList, defaultAddressId,selectedAddressId);
                    addressAdapter.setOnAddressSelectedListener(address -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedAddress", address);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });

                    binding.recyclerViewAddress.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressSelectActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AddressSelectActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserAddresses();
    }
}