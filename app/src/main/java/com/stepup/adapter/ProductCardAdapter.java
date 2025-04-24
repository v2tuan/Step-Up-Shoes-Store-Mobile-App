package com.stepup.adapter;

import android.app.Activity;
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
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.DetailActivity;
import com.stepup.databinding.ViewholderRecommendedBinding;
import com.stepup.model.FavoriteDTO;
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
        if(items.get(position).isFav()) {
            holder.binding.favBtn.setImageResource(R.drawable.ic_favorite_fill);
        }
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
                holder.binding.progressBar.setVisibility(View.VISIBLE);
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                if(items.get(position).isFav())
                {
                    Call<String> callDeleteFav = apiService.removeToFavorite1(items.get(position).getId());
                    callDeleteFav.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            holder.binding.progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                AppUtils.showDialogNotify((Activity) context,R.drawable.ic_tick, "Remove Favorite Successfull");
                                holder.binding.favBtn.setImageResource(R.drawable.btn_3);
                            }
                            else
                            {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            holder.binding.progressBar.setVisibility(View.GONE);
                            Log.e("API_ERROR", "Error fetching product", t);
                        }
                    });
                }
                else
                {
                    Call<String> callAddToFavorite = apiService.addToFavorite1(items.get(position).getId());
                    callAddToFavorite.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            holder.binding.progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                AppUtils.showDialogNotify((Activity) context,R.drawable.ic_tick, "Add Favorite Successfull");
                                holder.binding.favBtn.setImageResource(R.drawable.ic_favorite_fill);
                            }
                            else
                            {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            holder.binding.progressBar.setVisibility(View.GONE);
                            Log.e("API_ERROR", "Error fetching product", t);
                        }
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
