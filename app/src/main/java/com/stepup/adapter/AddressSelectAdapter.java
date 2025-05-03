package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.databinding.AddressItemRadioBinding;
import com.stepup.model.Address;

import java.util.List;

public class AddressSelectAdapter extends RecyclerView.Adapter<AddressSelectAdapter.AddressViewHolder> {

    private final Context context;
    private List<Address> addressList;
    private Long defaultAddressId;
    private int selectedPosition = -1;
    private Long selectedAddressId;
    private OnAddressSelectedListener onAddressSelectedListener;

    public AddressSelectAdapter(Context context, List<Address> addressList, Long defaultAddressId,Long selectedAddressId) {
        this.context = context;
        this.addressList = addressList;
        this.defaultAddressId = defaultAddressId;
        this.selectedAddressId = selectedAddressId;

        if (selectedAddressId != -1) {
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).getId().equals(selectedAddressId)) {
                    selectedPosition = i;
                    break;
                }

            }
        }
        else
        {
            if (defaultAddressId != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    if (addressList.get(i).getId().equals(defaultAddressId)) {
                        selectedPosition = i;
                        break;
                    }
                }
            }

        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddressItemRadioBinding binding = AddressItemRadioBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.binding.tvName.setText(address.getFullName());
        holder.binding.tvPhone.setText(address.getPhone());
        holder.binding.tvAddress.setText(address.getAddr());

        holder.binding.radioSelect.setChecked(position == selectedPosition);

        if (address.getId().equals(defaultAddressId)) {
            holder.binding.addressDefault.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.binding.addressDefault.setVisibility(android.view.View.GONE);
        }

        // Khi click chọn địa chỉ
        holder.binding.getRoot().setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);

            if (onAddressSelectedListener != null) {
                onAddressSelectedListener.onAddressSelected(addressList.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.onAddressSelectedListener = listener;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        AddressItemRadioBinding binding;

        public AddressViewHolder(@NonNull AddressItemRadioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnAddressSelectedListener {
        void onAddressSelected(Address address);
    }
}
