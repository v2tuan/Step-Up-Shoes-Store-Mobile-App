package com.stepup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.DetailActivity;
import com.stepup.databinding.ViewholderRecommendedBinding;
import com.stepup.model.FavoriteDTO;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.FavoriteViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ViewHolder> {
    private List<ProductCard> items;
    private Context context;
    private FavoriteViewModel viewModel;

    public ProductCardAdapter(List<ProductCard> items) {
        this.items = items;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderRecommendedBinding binding;

        public ViewHolder(ViewholderRecommendedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderRecommendedBinding binding = ViewholderRecommendedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCard item = items.get(position);
        holder.binding.titleTxt.setText(item.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(item.getPrice());
        holder.binding.priceTxt.setText(priceText);

//        // Check favorite status using productId
//        viewModel.checkFavoriteStatusByProductId(item.getId(), isFavorite -> {
//            holder.binding.favBtn.setImageResource(isFavorite ? R.drawable.ic_favorite_fill : R.drawable.favorite);
//            item.setFav(isFavorite); // Update local state
//        });
        if(items.get(position).isFav()) {
            holder.binding.favBtn.setImageResource(R.drawable.favorite_fill);
        }
        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop());


        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .apply(new RequestOptions()
                        .transform(new CenterCrop())
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.binding.pic);

        holder.itemView.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("object", items.get(currentPosition));
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.binding.favBtn.setOnClickListener(view -> {
            holder.binding.progressBar.setVisibility(View.VISIBLE);
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Call<String> call;

            if (item.isFav()) {
                call = apiService.removeToFavorite1(item.getId());
            } else {
                call = apiService.addToFavorite1(item.getId());
            }

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    holder.binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        item.setFav(!item.isFav()); // Cập nhật trạng thái cục bộ
                        AppUtils.showDialogNotify((Activity) context, R.drawable.ic_tick,
                                item.isFav() ? "Thêm yêu thích thành công" : "Xóa yêu thích thành công");
                        notifyItemChanged(position); // Chỉ cập nhật mục hiện tại
                    } else {
                        Toast.makeText(context, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    holder.binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}