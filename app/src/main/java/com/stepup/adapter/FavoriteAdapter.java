package com.stepup.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.databinding.FragmentFavoriteBinding;
import com.stepup.databinding.ViewholderRecommendedBinding;
import com.stepup.fragment.MyBottomFavoriteFragment;
import com.stepup.model.Color;
import com.stepup.model.Favorite;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import com.stepup.viewModel.FavoriteDiffCallback;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteAdapter  extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{

    private List<Favorite> listFavoriteItem;
    private Context context;
    ViewGroup viewGroupParent;
    private FragmentFavoriteBinding binding;

    public FavoriteAdapter(List<Favorite> listFavoriteItem, FragmentFavoriteBinding binding) {
        this.listFavoriteItem = listFavoriteItem;
        this.binding = binding;
    }
    public void updateList(List<Favorite> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FavoriteDiffCallback(listFavoriteItem, newList));
        listFavoriteItem.clear();
        listFavoriteItem.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        updateEmptyView();
    }
    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        viewGroupParent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_recommended, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        Favorite item = listFavoriteItem.get(position);
        holder.setFavoriteItem(item);
        Color color = item.getColor();
        holder.binding.titleTxt.setText(item.getTitle());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.binding.priceTxt.setText(format.format(item.getPrice()));
        Glide.with(holder.itemView.getContext())
                .load(item.getColor().getColorImages().get(0).getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.pic);
        holder.binding.favBtn.setImageResource(R.drawable.favorite_fill);

        holder.binding.favBtn.setOnClickListener(view -> {
            Long id = item.getId();
            showLoading();
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Call<String> callRemoveCartItem = apiService.removeFavoriteItem(item.getId());
            callRemoveCartItem.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        AppUtils.showDialogNotify((Activity) context, R.drawable.ic_tick, "Xóa yêu thích thành công");
                        int indexToRemove = -1;
                        for (int i = 0; i < listFavoriteItem.size(); i++) {
                            if (listFavoriteItem.get(i).getId()==id) {
                                indexToRemove = i;
                                break;
                            }
                        }

                        if (indexToRemove >= 0 && indexToRemove < listFavoriteItem.size()) {
                            List<Favorite> newList = new ArrayList<>(listFavoriteItem);
                            newList.remove(indexToRemove);
                            updateList(newList);
                            Log.d("FavoriteAdapter", "Removed item with id: " + id + " at index: " + indexToRemove);
                        } else {
                            Log.e("FavoriteAdapter", "Invalid index: " + indexToRemove + ", size: " + listFavoriteItem.size());
                        }
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    hideLoading();
                    Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.itemView.setOnClickListener(v -> {
            MyBottomFavoriteFragment bottomSheet = MyBottomFavoriteFragment.newInstance(
                    item.getTitle(), item.getColor().getName(),
                    String.valueOf(item.getPrice()),
                    item.getColor().getColorImages().get(0).getImageUrl(), item.getColor().getId()
            );
            bottomSheet.show(((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return listFavoriteItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ViewholderRecommendedBinding binding;
        private Favorite favoriteItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderRecommendedBinding.bind(itemView);
        }

        public ViewholderRecommendedBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewholderRecommendedBinding binding) {
            this.binding = binding;
        }

        public Favorite getFavoriteItem() {
            return favoriteItem;
        }

        public void setFavoriteItem(Favorite favoriteItem) {
            this.favoriteItem = favoriteItem;
        }
    }

    private void updateEmptyView() {
        if (listFavoriteItem.isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView2.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);
        }
    }
    private void showLoading() {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
    }

}
