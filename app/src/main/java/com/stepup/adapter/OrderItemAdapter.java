package com.stepup.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.R;
import com.stepup.activity.RatingActivity;
import com.stepup.databinding.FragmentCartBinding;
import com.stepup.databinding.ViewholderCartBinding;
import com.stepup.databinding.ViewholderOrderItem2Binding;
import com.stepup.databinding.ViewholderOrderItemBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.OrderItem;
import com.stepup.model.OrderItemResponse;
import com.stepup.model.OrderShippingStatus;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<T> tList;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public static final int TYPE_CART_ITEM = 0;
    public static final int TYPE_ORDER_ITEM = 1;

    // Danh cho orderItem
    private OnToggleExpandListener onToggleExpandListener;

    // Trạng thái mở rộng (true) hoặc thu gọn (false)
    private boolean isExpanded = false;

    // Số lượng item hiển thị ban đầu khi thu gọn
    private final int collapsedItemCount = 1;
    private String orderStatus;
    private long orderid;

    // Interface để callback khi trạng thái thay đổi
    public interface OnToggleExpandListener {
        void onToggleExpand(boolean isExpanded);
    }

    public OrderItemAdapter(List<T> tList, ChangeNumberItemsListener changeNumberItemsListener) {
        this.tList = tList;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    public OrderItemAdapter(List<T> tList, OnToggleExpandListener onToggleExpandListener) {
        this.tList = tList;
        this.onToggleExpandListener = onToggleExpandListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CART_ITEM) {
            View view = inflater.inflate(R.layout.viewholder_order_item, parent, false);
            return new ViewHolderOrderItem1(view);
        } else {
            View view = inflater.inflate(R.layout.viewholder_order_item2, parent, false);
            return new ViewHolderOrderItem2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_CART_ITEM) {
            bindCartItem((ViewHolderOrderItem1) holder, (CartItem) tList.get(position));
        } else {
            bindOrderItem((ViewHolderOrderItem2) holder, (OrderItemResponse) tList.get(position));
        }
    }

    // Phương thức để chuyển đổi trạng thái mở rộng/thu gọn
    public void toggleExpand() {
        isExpanded = !isExpanded;
        notifyDataSetChanged();
        if (onToggleExpandListener != null) {
            onToggleExpandListener.onToggleExpand(isExpanded);
        }
    }

    // Phương thức kiểm tra xem danh sách có thể mở rộng không
    public boolean canExpand() {
        return tList.size() > collapsedItemCount;
    }

    /**
     * Xác định loại view cho mỗi item dựa trên kiểu đối tượng.
     */
    @Override
    public int getItemViewType(int position) {
        T item = tList.get(position);
        return (item instanceof CartItem) ? TYPE_CART_ITEM : TYPE_ORDER_ITEM;
    }

    @Override
    public int getItemCount() {
        T item = tList.get(0);

        if (item instanceof OrderItemResponse) {
            // Nếu danh sách đang mở rộng, hiển thị tất cả item
            // Nếu đang thu gọn, chỉ hiển thị collapsedItemCount item (hoặc ít hơn nếu danh sách nhỏ hơn)
            return isExpanded ? tList.size() : Math.min(collapsedItemCount, tList.size());
        }
        return tList.size();

    }
    public void setOrderStatus(String orderStatus, Long orderid) {
        this.orderStatus = orderStatus;
        this.orderid = orderid;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    /**
     * Binding dữ liệu cho CartItem (giỏ hàng) và gắn sự kiện tăng giảm số lượng.
     */
    private void bindCartItem(ViewHolderOrderItem1 holder, CartItem item) {
        holder.setCartItem(item);
        ProductVariant variant = item.getProductVariant();

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(variant.getColor().getName() + "/" + variant.getSize().getName());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = variant.getPromotionPrice() != null
                ? format.format(variant.getPromotionPrice())
                : "0";
        holder.binding.totalEachItem.setText(priceText);
        holder.binding.numberItemTxt.setText(String.valueOf(item.getCount()));

        // Hiển thị hình ảnh sản phẩm
        Glide.with(holder.itemView.getContext())
                .load(variant.getColor().getColorImages().get(0).getImageUrl())
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.pic);

        // Nút tăng số lượng
        holder.binding.plusCartBtn.setOnClickListener(v -> {
            int count = item.getCount() + 1;
            item.setCount(count);
            notifyDataSetChanged();
            if (changeNumberItemsListener != null) {
                changeNumberItemsListener.onChanged();
            }
        });

        // Nút giảm số lượng
        holder.binding.minusCartBtn.setOnClickListener(v -> {
            int count = item.getCount() - 1;
            if (count == 0) return; // Không cho giảm xuống 0
            item.setCount(count);
            notifyDataSetChanged();
            if (changeNumberItemsListener != null) {
                changeNumberItemsListener.onChanged();
            }
        });
    }

    /**
     * Binding dữ liệu cho OrderItem (đơn hàng đã đặt), không cho sửa số lượng.
     */
    private void bindOrderItem(ViewHolderOrderItem2 holder, OrderItemResponse item) {
        holder.orderItem = item;
        ProductVariant variant = item.getProductVariant();

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(variant.getColor().getName() + "/" + variant.getSize().getName());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String promoPrice = item.getPromotionPrice() != null
                ? format.format(item.getPromotionPrice())
                : "0";
        holder.binding.txtPromotionPrice.setText(promoPrice);
        if("DELIVERED".equals(orderStatus))
        {
            holder.binding.btnRating.setVisibility(View.VISIBLE);
        }
        holder.binding.btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, RatingActivity.class);
                intent.putExtra("orderItem", item);
                intent.putExtra("orderid",orderid);
                context.startActivity(intent);
            }
        });
        // Nếu có giảm giá -> hiển thị giá gốc có gạch ngang
        if (!item.getPromotionPrice().equals(item.getPrice())) {
            String originalPrice = item.getPrice() != null
                    ? format.format(item.getPrice())
                    : "0";
            holder.binding.txtPrice.setPaintFlags(
                    holder.binding.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.binding.txtPrice.setText(originalPrice);
        }

        holder.binding.numberItemTxt.setText("x" + item.getCount());

        Glide.with(holder.itemView.getContext())
                .load(variant.getColor().getColorImages().get(0).getImageUrl())
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.pic);
    }

    // ViewHolder cho giỏ hàng (CartItem)
    public static class ViewHolderOrderItem1 extends RecyclerView.ViewHolder {
        ViewholderOrderItemBinding binding;
        private CartItem cartItem;

        public ViewHolderOrderItem1(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderOrderItemBinding.bind(itemView);
        }

        public void setCartItem(CartItem cartItem) {
            this.cartItem = cartItem;
        }

        public CartItem getCartItem() {
            return cartItem;
        }
    }

    // ViewHolder cho đơn hàng đã đặt (OrderItem)
    public static class ViewHolderOrderItem2 extends RecyclerView.ViewHolder {
        ViewholderOrderItem2Binding binding;
        OrderItemResponse orderItem;

        public ViewHolderOrderItem2(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderOrderItem2Binding.bind(itemView);
        }
    }
}