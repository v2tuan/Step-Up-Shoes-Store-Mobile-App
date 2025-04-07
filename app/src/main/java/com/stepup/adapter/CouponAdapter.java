package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.databinding.ViewholderColorBinding;
import com.stepup.databinding.ViewholderCouponBinding;
import com.stepup.model.Attribute;
import com.stepup.model.Banner;
import com.stepup.model.Color;
import com.stepup.model.Coupon;
import com.stepup.model.CouponCondition;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    private Context context;
    private List<Coupon> couponList;
    private int selectedPosition = -1; // -1 là mặc định chưa chọn item nào

    public CouponAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCouponBinding binding = ViewholderCouponBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new CouponAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponAdapter.ViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        for(CouponCondition couponCondition: coupon.getCouponConditionList()){
            if(couponCondition.getAttribute().equals(Attribute.DISCOUNT)){
                holder.binding.txtDiscount.setText((couponCondition.getValue()) + "% Discount");
            }
            else if(couponCondition.getAttribute().equals(Attribute.MINIMUM_AMOUNT)){
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Chuyển chuỗi thành số (Double)
                double value = Double.parseDouble(couponCondition.getValue());
                String discount = format.format(value);
                holder.binding.txtMinimum.setText("Minimum " + (discount));
            }
            else if(couponCondition.getAttribute().equals(Attribute.EXPIRY)){
                holder.binding.txtExp.setText("EXP: " + (couponCondition.getValue()));
            }
        }

        // Xử lý sự kiện click vào item
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu lại vị trí trước đó
                int lastSelectedPosition = selectedPosition;

                // Gán vị trí được chọn hiện tại
                selectedPosition = position;

                // Cập nhật lại item cũ và item mới (tạo hiệu ứng refresh)
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);
            }
        });

        // Kiểm tra nếu item này đang được chọn thì đổi màu nền
        if (selectedPosition == position) {
            // Nếu item này là item đang được chọn → nền tím
            holder.binding.radioButton.setImageResource(R.drawable.radio_button_checked);
        } else {
            // Nếu không phải → nền xám
            holder.binding.radioButton.setImageResource(R.drawable.radio_button_unchecked);
        }
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Coupon color;
        ViewholderCouponBinding  binding;

        public ViewHolder(ViewholderCouponBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public Coupon getColor() {
            return color;
        }

        public void setColor(Coupon color) {
            this.color = color;
        }

        public ViewholderCouponBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewholderCouponBinding binding) {
            this.binding = binding;
        }
    }
}
