package com.stepup.adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import com.stepup.databinding.FragmentCartBinding;
import com.stepup.databinding.ViewholderCartBinding;
import com.stepup.databinding.ViewholderOrderItemBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private List<CartItem> listCartItem;
    private Context context;
    ViewGroup viewGroupParent;

    public OrderItemAdapter(List<CartItem> listCartItem) {
        this.listCartItem = listCartItem;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        viewGroupParent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_item, parent, false);
        return new OrderItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        CartItem item = listCartItem.get(position);

        holder.setCartItem(item);
        ProductVariant variant = item.getProductVariant();

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(variant.getColor().getName() + "/" + variant.getSize().getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(variant.getPromotionPrice());
        holder.binding.totalEachItem.setText(priceText);
        holder.binding.numberItemTxt.setText("x" + String.valueOf(item.getCount()));

        Glide.with(holder.itemView.getContext())
                .load(item.getProductVariant().getColor().getColorImages().get(0).getImageUrl()) // Giả định có phương thức getPictureUrl()
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.pic);
    }

    @Override
    public int getItemCount() {
        return listCartItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderItemBinding binding;
        private CartItem cartItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderOrderItemBinding.bind(itemView);
        }

        public ViewholderOrderItemBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewholderOrderItemBinding binding) {
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
