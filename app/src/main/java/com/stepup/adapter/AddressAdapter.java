package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.stepup.databinding.AddressItemBinding;
import com.stepup.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<Address> addressList;

    public AddressAdapter(Context context, List<Address> addressList) {
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

        // Xử lý sự kiện
        holder.binding.btnSetDefault.setOnClickListener(v -> {
            Toast.makeText(context, "Đã đặt địa chỉ mặc định", Toast.LENGTH_SHORT).show();
        });
        // Xử lý sự kiện click vào toàn bộ item để mở trang chỉnh sửa
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditAddress(address);
            }
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