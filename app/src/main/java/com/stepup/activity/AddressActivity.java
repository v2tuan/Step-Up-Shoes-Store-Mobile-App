package com.stepup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    private ActivityAddressBinding binding;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;

    private TextView btnAddNewAddress;
    private ImageView btnBack;
    private RecyclerView recyclerViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);

        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressList);
        binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAddress.setAdapter(addressAdapter);

        getUserAddresses();

        binding.btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }
    private void getUserAddresses() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Address>> call = apiService.getAddressesByUserId();

        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addressList = response.body();
                    addressAdapter = new AddressAdapter(AddressActivity.this, addressList);
                    binding.recyclerViewAddress.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}