package com.stepup.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.databinding.ViewholderColorFilerBinding;

import java.util.List;

public class ColorFilterAdapter extends RecyclerView.Adapter<ColorFilterAdapter.ColorViewHolder> {

    private final Context context;
    private final List<String> colorList;
    private final OnColorClickListener listener;
    private String selectedColor;
    private int selectedPosition = -1;

    public interface OnColorClickListener {
        void onColorClick(String colorName);
    }

    public ColorFilterAdapter(Context context, List<String> colorList, OnColorClickListener listener) {
        this.context = context;
        this.colorList = colorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewholderColorFilerBinding binding = ViewholderColorFilerBinding.inflate(inflater, parent, false);
        return new ColorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        String colorName = colorList.get(position);
        int color = parseColor(colorName);

        GradientDrawable drawable = (GradientDrawable) holder.binding.colorCircle.getBackground();
        drawable.setColor(color);

        if (position == selectedPosition) {
            drawable.setStroke(5, Color.BLACK);
        } else {
            drawable.setStroke(0, Color.TRANSPARENT);
        }

        holder.binding.textColorName.setText(colorName); // Gán tên màu vào TextView
        holder.binding.getRoot().setOnClickListener(v -> {
            if (selectedPosition == position) {
                // Nếu chọn lại màu đã chọn, bỏ chọn nó
                selectedPosition = -1;
                listener.onColorClick(null);  // Trả về null khi bỏ chọn
            } else {
                // Nếu chọn màu khác, cập nhật vị trí và thông báo cho listener
                selectedPosition = position;
                selectedColor = colorName;
                listener.onColorClick(colorName);
            }
            notifyDataSetChanged();  // Cập nhật giao diện
        });
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        ViewholderColorFilerBinding binding;

        public ColorViewHolder(@NonNull ViewholderColorFilerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private int parseColor(String colorName) {
        try {
            return Color.parseColor(colorName.toLowerCase().replace(" ", ""));
        } catch (IllegalArgumentException e) {
            return Color.GRAY;
        }
    }
}
