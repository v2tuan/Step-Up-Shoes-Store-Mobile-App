package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.databinding.ViewholderColorBinding;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<String> items;
    private Context context;

    private int selectedPosition = -1; // -1 là mặc định chưa chọn item nào

    public ColorAdapter(List<String> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderColorBinding binding;

        public ViewHolder(ViewholderColorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderColorBinding binding = ViewholderColorBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
// Sử dụng Glide để tải ảnh vào ImageView
        Glide.with(holder.itemView.getContext()) // Lấy context từ itemView
                .load(items.get(position))          // Load ảnh từ item (có thể là URL hoặc resource)
                .into(holder.binding.pic);         // Hiển thị ảnh trong ImageView pic

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
            holder.binding.colorLayout.setBackgroundResource(R.drawable.black_bg_selected);
        } else {
            // Nếu không phải → nền xám
            holder.binding.colorLayout.setBackgroundResource(R.drawable.grey_bg);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
