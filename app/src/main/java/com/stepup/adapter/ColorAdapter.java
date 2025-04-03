package com.stepup.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.databinding.ViewholderColorBinding;
import com.stepup.model.Banner;
import com.stepup.model.Color;
import com.stepup.model.ColorImage;
import com.stepup.model.Product;
import com.stepup.model.ProductVariant;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<Color> colors;
    private Context context;
    private ViewPager2 viewPager2;

    private DotsIndicator dotsIndicator;
    private Product product;

    private int selectedPosition = -1; // -1 là mặc định chưa chọn item nào

    private ArrayList<Banner> sliderItems;

    private BannerAdapter bannerAdapter;

    private RecyclerView recyclerViewSize;
    public static Color colorSelected = null;

    public ColorAdapter(List<Color> items, ViewPager2 viewPager2, DotsIndicator dotsIndicator, Product product, ArrayList<Banner> sliderItems, BannerAdapter bannerAdapter, RecyclerView recyclerViewSize) {
        this.colors = items;
        this.viewPager2 = viewPager2;
        this.dotsIndicator = dotsIndicator;
        this.product = product;
        this.sliderItems = sliderItems;
        this.bannerAdapter = bannerAdapter;
        this.recyclerViewSize = recyclerViewSize;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Color color;
        ViewholderColorBinding binding;

        public ViewHolder(ViewholderColorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
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
                .load(colors.get(position).getColorImages().get(0).getImageUrl()) // Load ảnh từ item (có thể là URL hoặc resource)
                .into(holder.binding.pic);         // Hiển thị ảnh trong ImageView pic

        holder.setColor(colors.get(position));

        // Xử lý sự kiện click vào item
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu lại vị trí trước đó
                int lastSelectedPosition = selectedPosition;

                // Gán vị trí được chọn hiện tại
                selectedPosition = position;

                // Cập nhật lại item cũ và item mới (tạo hiệu ứng refresh)
                // khi gọi hàm này thì onBindViewHolder sẽ đợc gọi lại và set background
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);

                // Cập nhật slider sau layout để tránh requestLayout khi đang layout
                holder.itemView.post(() -> {
                    try {
                        sliderItems.clear();
                        for (ColorImage image : colors.get(position).getColorImages()) {
                            sliderItems.add(new Banner(image.getImageUrl()));
                        }

//                        viewPager2.setAdapter(new BannerAdapter(sliderItems));
                        bannerAdapter.notifyDataSetChanged();
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

                // Cập nhật số lượng sản phẩm ứng với color và size
                for (int i = 0; i < recyclerViewSize.getChildCount(); i++) {
                    View child = recyclerViewSize.getChildAt(i);
                    RecyclerView.ViewHolder holderSize = recyclerViewSize.getChildViewHolder(child);

                    // ép kiểu
                    SizeAdapter.ViewHolder sizeHolder = (SizeAdapter.ViewHolder) holderSize;

                    for (ProductVariant variant : product.getProductVariants()) {
                        if (variant.getColor().getName().equals(holder.getColor().getName()) && variant.getSize().getName().equals(sizeHolder.getSize().getName())) {
                            if(variant.getQuantity() == 0){
                                // xử lý: ví dụ gạch ngang chữ size
                                sizeHolder.binding.sizeTxt.setPaintFlags(
                                        sizeHolder.binding.sizeTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                                );
                                sizeHolder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.lightGrey));
                                sizeHolder.binding.colorLayout.setBackgroundResource(R.drawable.bg_out_of_stock);
                                holder.binding.colorLayout.setTag(R.id.bg_selected_tag, null); // Lưu ID vào tag
                                sizeHolder.binding.getRoot().setEnabled(false);

                                // Nếu size đang chọn đã hết hàng thì set null sizeSelected
                                if(SizeAdapter.sizeSelected == sizeHolder.getSize()){
                                    SizeAdapter.sizeSelected = null;
                                }
                            }
                            else {
                                // xử lý: ví dụ gạch ngang chữ size
                                sizeHolder.binding.sizeTxt.setPaintFlags(
                                        sizeHolder.binding.sizeTxt.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
                                );
                                Drawable currentDrawable = sizeHolder.binding.colorLayout.getBackground();
                                Drawable expectedDrawable = ContextCompat.getDrawable(context, R.drawable.black_bg_selected);


//                                if (currentDrawable != null && expectedDrawable != null && !currentDrawable.getConstantState().equals(expectedDrawable.getConstantState())) {
//                                    sizeHolder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.black));
//                                    sizeHolder.binding.colorLayout.setBackgroundResource(R.drawable.grey_bg);
//                                }
                                // Lấy giá trị tag đã lưu và so sánh
                                Integer currentBgId = (Integer) sizeHolder.binding.colorLayout.getTag(R.id.bg_selected_tag);
                                if (currentBgId == null || !(currentBgId == R.drawable.black_bg_selected)) {
                                    sizeHolder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.black));
                                    sizeHolder.binding.colorLayout.setBackgroundResource(R.drawable.grey_bg);
                                }

                                sizeHolder.binding.getRoot().setEnabled(true);
                            }
                        }
                    }
                }

            }
        });

        // Kiểm tra nếu item này đang được chọn thì đổi màu nền
        if (selectedPosition == position) {
            // Nếu item này là item đang được chọn → nền tím
            holder.binding.colorLayout.setBackgroundResource(R.drawable.black_bg_selected);
            colorSelected = holder.color;
        } else {
            // Nếu không phải → nền xám
            holder.binding.colorLayout.setBackgroundResource(R.drawable.grey_bg);
        }

    }

    @Override
    public int getItemCount() {
        return colors.size();
    }
}
