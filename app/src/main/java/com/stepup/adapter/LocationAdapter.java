package com.stepup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.R;

import java.util.ArrayList;
import java.util.List;
public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> items;
    private List<String> filteredItems;
    private OnItemClickListener onItemClickListener;

    // Hai loại giao diện: tiêu đề (header) và mục (item)
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    // Tiền tố để xác định tiêu đề
    private static final String HEADER_PREFIX = "#";

    public interface OnItemClickListener {
        void onItemClick(String location);
    }

    public LocationAdapter(List<String> items, OnItemClickListener listener) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu mục bắt đầu bằng "#", là tiêu đề
        return filteredItems.get(position).startsWith(HEADER_PREFIX) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            // Tạo giao diện cho tiêu đề
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Tạo giao diện cho mục thông thường
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new LocationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = filteredItems.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // Nếu là tiêu đề, loại bỏ tiền tố "#" và hiển thị
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String header = item.replace(HEADER_PREFIX, "");
            headerHolder.tvHeader.setText(header);
        } else {
            // Nếu là mục thông thường, loại bỏ tiền tố như "Tỉnh", "Huyện", "Xã", ...
            LocationViewHolder locationHolder = (LocationViewHolder) holder;
            String displayName = item;
            locationHolder.tvLocation.setText(displayName);
            // Gắn sự kiện nhấn
            locationHolder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public void updateList(List<String> newItems) {
        this.items = newItems;
        this.filteredItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            // Nếu không có từ khóa, hiển thị toàn bộ danh sách
            filteredItems.addAll(items);
        } else {
            // Lọc các mục (bỏ qua tiêu đề) dựa trên từ khóa
            List<String> tempFilteredItems = new ArrayList<>();
            for (String item : items) {
                if (item.startsWith(HEADER_PREFIX)) continue; // Bỏ qua tiêu đề
                if (item.toLowerCase().contains(query.toLowerCase())) {
                    tempFilteredItems.add(item);
                }
            }
            // Thêm lại các tiêu đề dựa trên danh sách đã lọc
            filteredItems.addAll(addHeadersToList(tempFilteredItems));
        }
        notifyDataSetChanged();
    }

    // Thêm tiêu đề (như "A", "B", ...) dựa trên chữ cái đầu tiên của các mục
    private List<String> addHeadersToList(List<String> list) {
        List<String> result = new ArrayList<>();
        String currentHeader = null;
        for (String item : list) {
            if (item.startsWith(HEADER_PREFIX)) continue; // Bỏ qua tiêu đề hiện có
            String firstChar = item.substring(0, 1).toUpperCase();
            if (!firstChar.equals(currentHeader)) {
                currentHeader = firstChar;
                result.add(HEADER_PREFIX + currentHeader); // Thêm tiêu đề với tiền tố "#"
            }
            result.add(item);
        }
        return result;
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;

        LocationViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(android.R.id.text1);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }
}