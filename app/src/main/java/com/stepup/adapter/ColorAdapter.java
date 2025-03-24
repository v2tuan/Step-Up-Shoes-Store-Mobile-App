package com.stepup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.databinding.ViewholderColorBinding;
import com.stepup.model.Banner;
import com.stepup.model.ColorImage;
import com.stepup.model.Product;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<String> items;
    private Context context;
    private ViewPager2 viewPager2;

    private DotsIndicator dotsIndicator;
    private Product product;

    private int selectedPosition = -1; // -1 là mặc định chưa chọn item nào

    ArrayList<Banner> sliderItems = new ArrayList<>();

    public ColorAdapter(List<String> items) {
        this.items = items;
    }

    public ColorAdapter(List<String> items, ViewPager2 viewPager2, DotsIndicator dotsIndicator, Product product) {
        this.items = items;
        this.viewPager2 = viewPager2;
        this.dotsIndicator = dotsIndicator;
        this.product = product;
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

                // Cập nhật slider sau layout để tránh requestLayout khi đang layout
                holder.itemView.post(() -> {
                    try {
                        for (ColorImage image : product.getColors().get(position).getColorImages()) {
                            sliderItems.add(new Banner(image.getImageUrl()));
                        }

                        viewPager2.setAdapter(new BannerAdapter(sliderItems));
                        viewPager2.setClipToPadding(true);
                        viewPager2.setClipChildren(true);
                        viewPager2.setOffscreenPageLimit(1);

                        // Dot Indicator
                        if (sliderItems.size() > 1) {
                            dotsIndicator.setVisibility(View.VISIBLE);
                        } else {
                            dotsIndicator.setVisibility(View.GONE);
                        }

                    } catch (Exception exception) {
                        Log.e("TAG", "Lỗi xảy ra: " + exception.getMessage(), exception);
                    }
                });
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
