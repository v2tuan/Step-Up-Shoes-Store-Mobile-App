package com.stepup.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.stepup.activity.EditAddressActivity;
import com.stepup.databinding.AddressItemBinding;
import com.stepup.model.Address;
import com.stepup.model.ApiResponse;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<Address> addressList;
    private Long defaultAddressId;
    private static final int REQUEST_CODE_EDIT_ADDRESS = 103;

    public AddressAdapter(Context context, List<Address> addressList, Long defaultAddressId) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding thay vì inflate thông thường
        AddressItemBinding binding = AddressItemBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        // Thiết lập dữ liệu
        holder.binding.tvName.setText("Tên người dùng: "+ address.getFullName());
        holder.binding.tvPhone.setText("Số điện thoại: " + address.getPhone());
        holder.binding.tvAddress.setText("Địa chỉ: "+ address.getAddr());

        int backgroundColor = (defaultAddressId != null && address.getId().equals(defaultAddressId))
                ? ContextCompat.getColor(context, android.R.color.holo_green_light)
                : ContextCompat.getColor(context, android.R.color.transparent);

        int visibility = (defaultAddressId != null && address.getId().equals(defaultAddressId))
                ? View.GONE
                : View.VISIBLE;


        holder.binding.imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditAddressActivity.class);
            intent.putExtra("addressId", address.getId()); // Gửi ID thực tế từ backend
            intent.putExtra("name", address.getFullName());
            intent.putExtra("phoneNumber", address.getPhone());
            intent.putExtra("address", address.getAddr());
            ((AppCompatActivity) holder.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_EDIT_ADDRESS);
        });

        // Xử lý sự kiện maac dinh
        holder.binding.btnSetDefault.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            Address currentAddress = addressList.get(currentPosition);
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Call<ApiResponse> call = apiService.setDefaultAddress(currentAddress.getId());

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse responseObject = response.body();
                        if (responseObject.getMessage().equals("Đặt địa chỉ mặc định thành công")) {
                            Toast.makeText(context, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                            // Có thể làm mới danh sách địa chỉ nếu cần
                        } else {
                            Toast.makeText(context, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.binding.imgDelete.setOnClickListener(v -> {
            // Lấy vị trí hiện tại của mục
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return; // Thoát nếu vị trí không hợp lệ
            }

            Address currentAddress = addressList.get(currentPosition);

            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                        Call<ApiResponse> call = apiService.deleteAddress(currentAddress.getId());
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse responseObject = response.body();
                                    if (responseObject.getMessage().equals("Xóa địa chỉ thành công")) {
                                        Toast.makeText(context, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                                        // Xóa mục khỏi danh sách và cập nhật RecyclerView
                                        addressList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, addressList.size());
                                    } else {
                                        Toast.makeText(context, responseObject.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    // ViewHolder sử dụng View Binding
    static class AddressViewHolder extends RecyclerView.ViewHolder {
        AddressItemBinding binding;

        public AddressViewHolder(@NonNull AddressItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}