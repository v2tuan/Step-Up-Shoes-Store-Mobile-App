package com.stepup.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.R;
import com.stepup.databinding.ViewholderSizeBinding;
import com.stepup.model.Product;
import com.stepup.model.Size;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
    private List<String> items;
    private Context context;
    public static Size sizeSelected = null;

    private Product product;

    private int selectedPosition = -1; // -1 là mặc định chưa chọn item nào

    public SizeAdapter(List<String> items, Product product) {
        this.items = items;
        this.product = product;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Size size;
        ViewholderSizeBinding binding;

        public ViewHolder(ViewholderSizeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public Size getSize() {
            return size;
        }

        public void setSize(Size size) {
            this.size = size;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderSizeBinding binding = ViewholderSizeBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.sizeTxt.setText(items.get(position));

        holder.setSize(product.getSizes().get(position));

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

            holder.binding.colorLayout.setTag(R.id.bg_selected_tag, R.drawable.black_bg_selected); // Lưu ID vào tag
            Integer currentBgId = (Integer) holder.binding.colorLayout.getTag(R.id.bg_selected_tag);

            holder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.white));
//            holder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.purple));

            sizeSelected = holder.size;
        } else {
            // Nếu không phải → nền xám
            holder.binding.colorLayout.setBackgroundResource(R.drawable.grey_bg);
            holder.binding.colorLayout.setTag(R.id.bg_selected_tag, null); // Lưu ID vào tag
            holder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
