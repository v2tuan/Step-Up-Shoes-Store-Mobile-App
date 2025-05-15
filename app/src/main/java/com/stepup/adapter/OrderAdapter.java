package com.stepup.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.DetailActivity;
import com.stepup.activity.MainActivity;
import com.stepup.activity.OrderDetailActivity;
import com.stepup.activity.RefundDetail;
import com.stepup.databinding.ViewholderOrderBinding;
import com.stepup.model.AddToCartDTO;
import com.stepup.model.Order;
import com.stepup.model.OrderItemResponse;
import com.stepup.model.OrderResponse;
import com.stepup.model.PaymentMethod;
import com.stepup.model.PaymentStatus;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter<>(order.getOrderItems(), new OrderItemAdapter.OnToggleExpandListener() {
            @Override
            public void onToggleExpand(boolean isExpanded) {
                // Cập nhật text của button dựa vào trạng thái
//                holder.binding.btnToggleExpand.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//                holder.binding.textViewExpand.setText(isExpanded ? "See less" : "See more");
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

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", order.getId());
                context.startActivity(intent);
            }
        });
        holder.binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<AddToCartDTO> addToCartDTOList = new ArrayList<>();

                for (OrderItemResponse item : order.getOrderItems()) {
                    AddToCartDTO dto = new AddToCartDTO();
                    dto.setProductVariantId(item.getProductVariant().getId());
                    dto.setQuantity(item.getCount());
                    addToCartDTOList.add(dto);
                }
                BuyAgain(addToCartDTOList);
            }
        });
        holder.binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pay(position,holder);
            }
        });
        holder.binding.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", order.getId());
                context.startActivity(intent);
            }
        });
        switch (order.getStatus()) {
            case CANCELLED:
                holder.binding.view.setVisibility(View.VISIBLE);
                holder.binding.btnBuyAgain.setVisibility(View.VISIBLE);

                if(order.getPaymentMethod().equals(PaymentMethod.VNPAY)&& order.getPaymentStatus().equals(PaymentStatus.REFUNDING))
                {
                    holder.binding.view.setVisibility(View.GONE);
                    holder.binding.txtStatus.setText("VNPayCancled - Refunding ");
                    holder.binding.btnRefund.setVisibility(View.VISIBLE);
                    holder.binding.btnRefund.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent((Activity) context , RefundDetail.class);
                            intent.putExtra("price", order.getTotalPrice());        // nếu là double
                            intent.putExtra("status", order.getPaymentStatus().toString());
                            intent.putExtra("date", order.getUpdatedAt());
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case PENDING:
                holder.binding.view.setVisibility(View.VISIBLE);
                if (order.getPaymentMethod().equals(PaymentMethod.VNPAY) && order.getPaymentStatus().equals(PaymentStatus.PENDING))
                {
                    String createAt = order.getCreatedAt();
                    if (createAt.length() > 23) {
                        createAt = createAt.substring(0, 23); // "2025-05-10T16:09:17.663"
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    try {
                        Date updatedAtDate = dateFormat.parse(createAt);
                        Date now = new Date();
                        long diffMillis = now.getTime() - updatedAtDate.getTime();
                        long hoursElapsed = TimeUnit.MILLISECONDS.toHours(diffMillis);
                        if (hoursElapsed <= 24) {
                            holder.binding.txtStatus.setText("Wait for payment");
                            holder.binding.btnPay.setVisibility(View.VISIBLE);
                        } else {
                            // Over 24 hours: Handle expired payment
                            holder.binding.txtStatus.setText("Payment expired");
                            holder.binding.btnPay.setVisibility(View.GONE);
                            if (hoursElapsed >= 48)
                            {
                                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                                Call<ResponseObject> cancelOrder = apiService.cancelOrder(order.getId());
                                cancelOrder.enqueue(new Callback<ResponseObject>() {
                                    @Override
                                    public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                                        ResponseObject responseObject = (ResponseObject) response.body();
                                        if(response.isSuccessful()){
                                            AppUtils.showDialogNotify((Activity) context, R.drawable.ic_tick,"Đã hủy đơn hết hạn thanh toán sau 1 ngày");
                                        }
                                        else {
                                            AppUtils.showDialogNotify((Activity) context, R.drawable.error, responseObject.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                                        Log.e(TAG, t.getMessage());
                                        AppUtils.showDialogNotify((Activity) context, R.drawable.error, "Error");
                                    }
                                });
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        holder.binding.txtStatus.setText("Error processing payment");
                        holder.binding.btnPay.setVisibility(View.GONE);
                    }
                }
                break;

            case DELIVERED:
                holder.binding.btnBuyAgain.setVisibility(View.VISIBLE);
                holder.binding.btnDetail.setVisibility(View.VISIBLE);
                break;
            case PREPARING:
                holder.binding.view.setVisibility(View.VISIBLE);
                if (order.getPaymentMethod().equals(PaymentMethod.VNPAY) && order.getPaymentStatus().equals(PaymentStatus.PENDING))
                {
                    holder.binding.txtStatus.setText("Wait for payment");
                    holder.binding.btnPay.setVisibility(View.VISIBLE);
                }
                break;
            case RETURNED:
                if(order.getPaymentStatus().equals(PaymentStatus.REFUNDING))
                {
                    holder.binding.txtStatus.setText("Processing Refurn");
                }
                holder.binding.view.setVisibility(View.VISIBLE);
                holder.binding.btnRefund.setVisibility(View.VISIBLE);
                holder.binding.btnRefund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent((Activity) context , RefundDetail.class);
                        intent.putExtra("price", order.getTotalPrice());        // nếu là double
                        intent.putExtra("status", order.getPaymentStatus().toString());
                        intent.putExtra("date", order.getUpdatedAt());
                        context.startActivity(intent);
                    }
                });
                break;

        }

//        if (order.getPaymentMethod().equals(PaymentMethod.VNPAY)) {
//            if (order.getPaymentStatus().equals(PaymentStatus.PENDING) || order.getPaymentStatus().equals(PaymentStatus.FAILED)) {
//                holder.binding.txtStatus.setText("Wait for payment");
//                AppCompatButton btnPay = new AppCompatButton(context);
//                // Thiết lập ID
//                btnPay.setId(View.generateViewId());
//
//                // Thiết lập kích thước cố định là 150dp x 40dp
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        dpToPx(150),  // Chiều rộng 150dp
//                        dpToPx(40)    // Chiều cao 40dp
//                );
//                btnPay.setLayoutParams(params);
//
//                // Thiết lập background từ drawable
//                btnPay.setBackground(ContextCompat.getDrawable(context, R.drawable.black_bg));
//
//                // Thiết lập text
//                btnPay.setText("Pay");
//
//                // Tắt tính năng tự động viết hoa
//                btnPay.setAllCaps(false);
//
//                // Thiết lập màu text
//                btnPay.setTextColor(Color.WHITE);
//
//                // Thêm sự kiện click nếu cần
//                btnPay.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Xử lý khi nút được nhấn
//
//                    }
//                });
//
//                // Thêm button vào container
//                holder.binding.buttonContainer.addView(btnPay);
//            }
//        }
    }
    private void Pay (int position, ViewHolder view)
    {
        OrderResponse order = orderList.get(position);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> paymentOrder = apiService.payment(order.getId());
        paymentOrder.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                ResponseObject responseObject = (ResponseObject) response.body();
                if(response.isSuccessful()){
                   view.binding.txtStatus.setText("Pending");
                   view.binding.btnPay.setVisibility(View.GONE);
                }
                else {
                    AppUtils.showDialogNotify((Activity) context, R.drawable.error, responseObject.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                AppUtils.showDialogNotify((Activity) context, R.drawable.error, "Error");
            }
        });
    }
    private void BuyAgain (List<AddToCartDTO> addToCartDTO)
    {
        //showLoading();
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> addCart = apiService.addToCartOrder(addToCartDTO);
        addCart.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //hideLoading();
                if(response.isSuccessful()){
                    Intent intent = new Intent(context , MainActivity.class);
                    intent.putExtra("navigate_to", "cart");  // Gửi thông điệp yêu cầu mở CartFragment
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                }
                else {
                    AppUtils.showDialogNotify((Activity) context, R.drawable.ic_check, "Process failed");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                //hideLoading();
                AppUtils.showDialogNotify((Activity) context, R.drawable.error, "Error");
            }
        });
    }
    private void Return(int position, ViewHolder view)
    {
        OrderResponse order = orderList.get(position);
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> returnOrder = apiService.returnOrder(order.getId());
        returnOrder.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                ResponseObject responseObject = (ResponseObject) response.body();
                if(response.isSuccessful()){
                    AppUtils.showDialogNotify((Activity) context, R.drawable.error, responseObject.getMessage());
                }
                else {
                    AppUtils.showDialogNotify((Activity) context, R.drawable.error, responseObject.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                AppUtils.showDialogNotify((Activity) context, R.drawable.error, "Error");
            }
        });
    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
