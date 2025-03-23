package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.R;
import com.stepup.model.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Banner> bannerList;
    private Context context;

    public BannerAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item_container, parent, false); // item_slider.xml là layout cho từng slide
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.BannerViewHolder holder, int position) {
        // Gọi hàm setImage để gán ảnh vào ImageView
        holder.setImage(bannerList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        public void setImage(Banner banner, Context context) {
//            RequestOptions requestOptions = new RequestOptions().transform(new CenterInside());
//            Glide.with(context)
//                    .load(banner.getImageUrl()) // giả sử trong SliderModel có phương thức getUrl()
//                    .apply(requestOptions)
//                    .into(imageView);

            // load anh voi Glide
            Glide.with(context)
                    .load(banner.getImageUrl())
                    .into(imageView);
        }
    }
}
