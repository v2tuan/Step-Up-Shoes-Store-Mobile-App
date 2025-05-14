package com.stepup.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.activity.FullScreenImageActivity;
import com.stepup.model.ReviewResponse;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewResponse> reviewList;

    public ReviewAdapter(Context context, List<ReviewResponse> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResponse review = reviewList.get(position);

        // Bind data
        holder.tvUserFullName.setText(review.getUserFullName());
        holder.tvProductName.setText(review.getProductName());
        holder.tvContent.setText(review.getContent());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        // Format createdAt
        if (review.getCreatedAt() != null) {
            try {
                // Convert LocalDateTime to Date (requires API 26, so use a workaround for older APIs)
                Date date = dateFormat.parse(review.getCreatedAt());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDate = outputFormat.format(date);
                holder.tvCreatedAt.setText("Created at: " + formattedDate);
            } catch (Exception e) {
                holder.tvCreatedAt.setText("Created at: N/A");
            }
        } else {
            holder.tvCreatedAt.setText("Created at: N/A");
        }

        // Display rating stars
        holder.llRatingStars.removeAllViews();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(context);
            star.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            star.setImageResource(i < review.getRating() ? R.drawable.star : R.drawable.staroutline);
            holder.llRatingStars.addView(star);
        }

        // Display review images
        ImageAdapter imageAdapter = new ImageAdapter(context, review.getImageUrls(), image -> {
            // Open full-screen image when clicked
            if (image instanceof String) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image_url", (String) image);
                context.startActivity(intent);
            }
        });
        holder.rvReviewImages.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserFullName, tvProductName, tvContent, tvCreatedAt;
        LinearLayout llRatingStars;
        RecyclerView rvReviewImages;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserFullName = itemView.findViewById(R.id.tv_user_full_name);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            llRatingStars = itemView.findViewById(R.id.ll_rating_stars);
            rvReviewImages = itemView.findViewById(R.id.rv_review_images);
            rvReviewImages.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}