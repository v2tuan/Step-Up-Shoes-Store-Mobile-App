package com.stepup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.R;
import com.stepup.databinding.ViewholderOrderBinding;
import com.stepup.model.Order;
import com.stepup.model.OrderResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    List<OrderResponse> orderList;
    private Context context;

    public OrderAdapter(List<OrderResponse> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
        String status = order.getStatus().toString(); // Ví dụ: "PENDING"
        String capitalizedStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        holder.binding.txtStatus.setText(capitalizedStatus); // Hiển thị: "Pending"

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(order.getTotalPrice());
        holder.binding.txtTotalPrice.setText(priceText);
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter<>(order.getOrderItems(),new OrderItemAdapter.OnToggleExpandListener() {
            @Override
            public void onToggleExpand(boolean isExpanded) {
                // Cập nhật text của button dựa vào trạng thái
//                holder.binding.btnToggleExpand.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                holder.binding.textViewExpand.setText(isExpanded ? "See less" : "See more");
                holder.binding.imageViewExpand.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            }
        });

        // Ẩn nút nếu danh sách không thể mở rộng (số item <= 1)
        holder.binding.btnToggleExpand.setVisibility(orderItemAdapter.canExpand() ? View.VISIBLE : View.GONE);
        holder.binding.btnToggleExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đổi trạng thái mở rộng/thu gọn khi click nút
                orderItemAdapter.toggleExpand();
            }
        });

        holder.binding.recyclerView.setAdapter(orderItemAdapter);
        holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderOrderBinding.bind(itemView);
        }
    }
}
