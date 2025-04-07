package com.stepup.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.stepup.R;
import com.stepup.adapter.OrderItemAdapter;
import com.stepup.databinding.ActivityCheckOutBinding;
import com.stepup.fragment.MyBottomSheetFragment;
import com.stepup.model.Address;
import com.stepup.model.CartItem;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends BaseActivity {
    private ActivityCheckOutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_check_out);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Address defaultAddress = getDefaultAddress();
        getDefaultAddress();
        getOrderItems();

        binding.btnCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetFragment bottomSheet = new MyBottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

//        SearchBar searchBar = binding.catSearchBar;
//        SearchView searchView = binding.catSearchView;
//        searchView.setupWithSearchBar(searchBar);
    }

    private void getDefaultAddress() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> call = apiService.getDefaultAddress();

        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject responseObject = response.body();
                    if(responseObject.getData() != null){
                        // Kiểm tra xem obj có phải là LinkedTreeMap không
                        if (responseObject.getData() instanceof LinkedTreeMap) {
                            Gson gson = new Gson();
                            String json = gson.toJson(responseObject.getData());  // Chuyển đổi LinkedTreeMap thành chuỗi JSON
                            Address defaultAddress = gson.fromJson(json, Address.class);  // Chuyển chuỗi JSON thành Address

                            // Tiến hành xử lý đối tượng Address
                            binding.txtName.setText(defaultAddress.getFullName());
                            binding.txtPhone.setText(defaultAddress.getPhone());
                            binding.txtAddress.setText(defaultAddress.getAddr());
                        } else {
                            // Xử lý trường hợp obj không phải là LinkedTreeMap hoặc Address
                            Log.e("Error", "Dữ liệu trả về không phải là LinkedTreeMap");
                        }
                    }

             } else {
                    Toast.makeText(CheckOutActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(CheckOutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderItems() {
        try {

            Intent intent = getIntent();
            List<CartItem> receivedList = intent.getParcelableArrayListExtra("orderItems");
            binding.rvOrderItems.setAdapter(new OrderItemAdapter(receivedList));
            binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom)); // drawable divider của bạn
            binding.rvOrderItems.addItemDecoration(dividerItemDecoration);
        }
        catch (Exception e){
            Log.e("Error Get Order Items", e.getMessage());
        }
    }
}