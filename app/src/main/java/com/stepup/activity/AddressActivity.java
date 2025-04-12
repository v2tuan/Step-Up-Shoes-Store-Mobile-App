package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.R;
import com.stepup.adapter.AddressAdapter;
import com.stepup.databinding.ActivityAddressBinding;
import com.stepup.model.Address;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends BaseActivity  {
    private ActivityAddressBinding binding;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private Long defaultAddressId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressList, defaultAddressId );
        binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAddress.setAdapter(addressAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom)); // drawable divider của bạn
        binding.recyclerViewAddress.addItemDecoration(dividerItemDecoration);

        getUserAddresses();

        binding.btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }
    private void getUserAddresses() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Map<String, Object>> call = apiService.getAddressesByUserId();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();

                    // Lấy danh sách địa chỉ
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

                    // Cập nhật adapter
                    addressAdapter = new AddressAdapter(AddressActivity.this, addressList, defaultAddressId);
                    binding.recyclerViewAddress.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
       // getUserAddresses();
    }
}