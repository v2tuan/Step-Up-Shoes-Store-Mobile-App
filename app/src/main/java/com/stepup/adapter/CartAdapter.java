package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.R;
import com.stepup.databinding.ViewholderCartBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.ProductVariant;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> listCartItem;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private Context context;

    public CartAdapter(List<CartItem> listCartItem, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listCartItem = listCartItem;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = listCartItem.get(position);

        holder.setCartItem(item);
        ProductVariant variant = item.getProductVariant();

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(variant.getColor().getName() + "/" + variant.getSize().getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(variant.getPromotionPrice());
        holder.binding.totalEachItem.setText(priceText);
        holder.binding.numberItemTxt.setText(String.valueOf(item.getCount()));

        Glide.with(holder.itemView.getContext())
                .load(item.getProductVariant().getColor().getColorImages().get(0).getImageUrl()) // Giả định có phương thức getPictureUrl()
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.pic);

        holder.binding.plusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                int count = holder.cartItem.getCount() + 1;
                holder.cartItem.setCount(count);
                changeNumberItemsListener.onChanged();
            }
        });

        holder.binding.minusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                int count = holder.cartItem.getCount() - 1;
                holder.cartItem.setCount(count);
                changeNumberItemsListener.onChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listCartItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        private CartItem cartItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderCartBinding.bind(itemView);
        }

        public ViewholderCartBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewholderCartBinding binding) {
            this.binding = binding;
        }

        public CartItem getCartItem() {
            return cartItem;
        }

        public void setCartItem(CartItem cartItem) {
            this.cartItem = cartItem;
        }
    }
}