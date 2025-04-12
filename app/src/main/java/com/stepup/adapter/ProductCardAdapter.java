package com.stepup.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.activity.DetailActivity;
import com.stepup.databinding.ViewholderRecommendedBinding;
import com.stepup.model.FavoriteItem;
import com.stepup.model.FavoriteItemDTO;
import com.stepup.model.Product;
import com.stepup.model.ProductCard;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ViewHolder>{
    private List<ProductCard> items;
    private Context context;

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
    public ProductCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderRecommendedBinding binding = ViewholderRecommendedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardAdapter.ViewHolder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(items.get(position).getPrice());
        holder.binding.priceTxt.setText(priceText);//        holder.binding.ratingTxt.setText(String.valueOf(items.get(position).getRating()));

        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop());

        Glide.with(holder.itemView.getContext())
                .load(items.get(position).getImageUrl())
                .apply(requestOptions)
                .into(holder.binding.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                    intent.putExtra("object", items.get(currentPosition));
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });

        holder.binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FavoriteItemDTO favoriteItem = new FavoriteItemDTO();
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<Product> callProduct = apiService.getProductById(items.get(position).getId());
                callProduct.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.isSuccessful() && response.body() != null) {
                           Product product = response.body();
                            Log.e("Product", "id " + product);
                           favoriteItem.setProductVariantId(product.getProductVariants().get(0).getId());
                           Log.e("favoriteid", "id " + favoriteItem.getProductVariantId());
                            Call<String> callAddToFavorite = apiService.addToFavorite(favoriteItem);
                            callAddToFavorite.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("API_ERROR", "Error fetching product", t);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Log.e("RetrofitError", "Error: " + t.getMessage());
                    }
                });
              //  Log.e("API_ERROR", "id " + favoriteItem.getProductVariantId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
