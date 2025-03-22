package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.databinding.ViewholderRecommendedBinding;
import com.stepup.model.ProductCard;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
                .load(items.get(position).getProductImages().get(0).getImageUrl())
                .apply(requestOptions)
                .into(holder.binding.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
