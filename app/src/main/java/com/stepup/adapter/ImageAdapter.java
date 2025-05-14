package com.stepup.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stepup.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<?> imageList;
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(Object image); // Handle both Uri and String
    }

    public ImageAdapter(Context context, List<?> imageList, OnImageClickListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Object image = imageList.get(position);

        if (image instanceof Uri) {
            Glide.with(context)
                    .load((Uri) image)
                    .centerCrop()
                    .into(holder.imageView);
        } else if (image instanceof String) {
            Glide.with(context)
                    .load((String) image)
                    .centerCrop()
                    .placeholder(R.drawable.shoes)
                    .error(R.drawable.error)
                    .into(holder.imageView);
        }

        // Handle image click
        holder.imageView.setOnClickListener(v -> listener.onImageClick(image));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}