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
import com.stepup.databinding.FragmentFavoriteBinding;
import com.stepup.databinding.ViewHolderFavoriteBinding;
import com.stepup.databinding.ViewholderCartBinding;
import com.stepup.model.AddToCartDTO;
import com.stepup.model.CartItem;
import com.stepup.model.FavoriteItem;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteAdapter  extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{

    private List<FavoriteItem> listFavoriteItem;
    private Context context;
    ViewGroup viewGroupParent;
    private FragmentFavoriteBinding binding;

    public FavoriteAdapter(List<FavoriteItem> listFavoriteItem, FragmentFavoriteBinding binding) {
        this.listFavoriteItem = listFavoriteItem;
        this.binding = binding;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        viewGroupParent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_favorite, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        FavoriteItem item = listFavoriteItem.get(position);
        holder.setFavoriteItem(item);
        ProductVariant variant = item.getProductVariant();
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(variant.getColor().getName() + "/" +variant.getSize().getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(variant.getPromotionPrice());
        holder.binding.totalEachItem.setText(priceText);
        Glide.with(holder.itemView.getContext())
                .load(item.getProductVariant().getColor().getColorImages().get(0).getImageUrl()) // Giả định có phương thức getPictureUrl()
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.pic);
        holder.binding.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                AddToCartDTO addToCartDTO = new AddToCartDTO(item.getProductVariant().getId(), 1);
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<String> callAddFavoriteItemToCart = apiService.addCart(addToCartDTO);
                callAddFavoriteItemToCart.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()&&response.body()!= null)
                        {
                            Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                            Log.d("Send Item To Cart", "Message: : " + response.body());
                            Call<String> removeCall = apiService.removeFavoriteItem(item.getId());
                            removeCall.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        listFavoriteItem.remove(position);
                                        notifyDataSetChanged();
                                        Log.d("Remove Favorite", "Success: " + response.body());
                                    }
                                    hideLoading(holder.itemView);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    hideLoading(holder.itemView);
                                    Log.e("Remove Favorite Error", "Error: " + t.getMessage());
                                }
                            });
                            listFavoriteItem.remove(position);
                            notifyDataSetChanged();
                            hideLoading(holder.itemView);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        hideLoading(holder.itemView);
                        Log.e("RetrofitError", "Error: " + t.getMessage());
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ViewHolderFavoriteBinding binding;
        private FavoriteItem favoriteItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewHolderFavoriteBinding.bind(itemView);
        }

        public ViewHolderFavoriteBinding getBinding()
        {
            return binding;
        }
        public void setBinding(ViewHolderFavoriteBinding binding) {
            this.binding = binding;
        }

        public FavoriteItem getFavoriteItem() {
            return favoriteItem;
        }

        public void setFavoriteItem(FavoriteItem favoriteItem) {
            this.favoriteItem = favoriteItem;
        }
    }
    private void ShowCustomDeleteDialog (int position, View itemView)
    {

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
            removeItemFromFavorite(position, itemView); // Xóa sản phẩm khi xác nhận
            dialog.dismiss(); // Đóng dialog sau khi xóa
            showLoading(itemView);
        });

//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Làm nền trong suốt
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    private void removeItemFromFavorite(int position, View itemView)
    {
        FavoriteItem item = listFavoriteItem.get(position);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> removeCall = apiService.removeFavoriteItem(item.getId());
        removeCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listFavoriteItem.remove(position);
                    notifyDataSetChanged();
                    Log.d("Remove Favorite", "Success: " + response.body());
                }
                hideLoading(itemView);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideLoading(itemView);
                Log.e("Remove Favorite Error", "Error: " + t.getMessage());
            }
        });
    }

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
