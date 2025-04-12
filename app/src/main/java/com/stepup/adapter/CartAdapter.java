package com.stepup.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.R;
import com.stepup.activity.DetailActivity;
import com.stepup.databinding.FragmentCartBinding;
import com.stepup.databinding.ViewholderCartBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> listCartItem;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private Context context;
    ViewGroup viewGroupParent;
    private FragmentCartBinding binding;

    public CartAdapter(List<CartItem> listCartItem, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listCartItem = listCartItem; // Ta đang gán tham chiếu (reference) của đối tượng từ tham số vào biến thành viên của lớp. Điều đó nghĩa là chung cung tro toi 1 dia chi bo nho
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    public CartAdapter(List<CartItem> listCartItem, ChangeNumberItemsListener changeNumberItemsListener, FragmentCartBinding binding) {
        this.listCartItem = listCartItem;
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.binding = binding;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        viewGroupParent = parent;
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
                if(count == 0){
                    showCustomDeleteDialog(position, holder.itemView);
                    return;
                }
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

    private void showCustomDeleteDialog(int position, View itemView) {
        // Tạo một Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate layout tùy chỉnh
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_confirm_remove_cart_item, null);

        // Gán layout vào Dialog
        builder.setView(customView);

        // Lấy các thành phần trong dialog
//        TextView dialogTitle = customView.findViewById(R.id.dialogTitle);
//        TextView dialogMessage = customView.findViewById(R.id.dialogMessage);
        Button btnCancel = customView.findViewById(R.id.btnCancel);
        Button btnConfirm = customView.findViewById(R.id.btnConfirm);

        // Tạo Dialog
        AlertDialog dialog = builder.create();

        // Xử lý hành động của các nút
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog khi hủy
        });

        btnConfirm.setOnClickListener(v -> {
            removeItemFromCart(position, itemView); // Xóa sản phẩm khi xác nhận
            dialog.dismiss(); // Đóng dialog sau khi xóa
            showLoading(itemView);
        });

//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Làm nền trong suốt
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void removeItemFromCart(int position, View itemView){
        CartItem item = listCartItem.get(position);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> callRemoveCartItem = apiService.removeCartItem(item.getId());
        callRemoveCartItem.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                    Log.d("Remove From Cart", "Message: : " + response.body());
                    listCartItem.remove(position);
                    notifyDataSetChanged();
                    hideLoading(itemView);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideLoading(itemView);
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    // Hiển thị process bar
    private void showLoading(View itemView) {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading(View itemView) {
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
    }

}