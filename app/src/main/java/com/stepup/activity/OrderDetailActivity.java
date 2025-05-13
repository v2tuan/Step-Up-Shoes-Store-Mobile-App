package com.stepup.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.adapter.OrderAdapter;
import com.stepup.adapter.OrderItemAdapter;
import com.stepup.databinding.ActivityOrderDetailBinding;
import com.stepup.fragment.CartFragment;
import com.stepup.model.AddToCartDTO;
import com.stepup.model.Address;
import com.stepup.model.Order;
import com.stepup.model.OrderItem;
import com.stepup.model.OrderItemResponse;
import com.stepup.model.OrderResponse;
import com.stepup.model.OrderShippingStatus;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderStatus,tvOderTittle;
    private LinearLayout layoutProcessingRefund;
    private LinearLayout layoutCompleted, layoutRefurn, layoutChat, layoutAddress, layoutCancelDetail;
    private LinearLayout layoutRefundCompleted;
    private LinearLayout layoutCancelled;

    private Button btnSeeDetails;
    private Button btnRate;
    private Button btnBuyAgain;
    private Button btnContact;
    private Button btnRefund;

    private OrderResponse currentOrder;

    private ActivityOrderDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize views
        initViews();
        showLoading();
        getOrder();
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvOrderStatus = binding.tvOrderStatus;
        tvOderTittle = binding.titleOrder;
//        layoutProcessingRefund = findViewById(R.id.layout_processing_refund);
        layoutCompleted = binding.orderCompleteHelp;
        layoutRefurn =binding.returnOrder;
        layoutChat =binding.chat;
        layoutAddress =binding.layoutAddress;
        layoutRefundCompleted = binding.layoutRefundCompleted;
        layoutCancelled = binding.layoutCancelled;
        layoutCancelDetail= binding.cancleDetail;
      //  btnSeeDetails = binding.btnSeeDetails;
        btnBuyAgain =binding.btnBuyAgain;
//        btnBuyAgain = findViewById(R.id.btn_buy_again);
//        btnContact = findViewById(R.id.btn_contact);
//        btnRefund = findViewById(R.id.btn_refund);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnRefundDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this , RefundDetail.class);
                intent.putExtra("order", currentOrder);
                startActivity(intent);
            }
        });
    }

    private void getOrder() {
        Long orderId = getIntent().getLongExtra("orderId", -1);
        if(orderId != -1){
            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Call<ResponseObject> callOrder = apiService.getOrder(orderId);
            callOrder.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                    if(response.isSuccessful()){
                        // ✅ Thành công (status code 200-299)
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getData()); // convert data thành JSON string

                        Type type = new TypeToken<OrderResponse>() {}.getType();
                        currentOrder = gson.fromJson(json, type); // parse lại JSON thành List
                        displayOrderDetails(currentOrder);
                    }
                    else {
                        AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, "Có lỗi xảy ra, Vui lòng thử lại sau!");
                    }
                    hideLoading();
                }

                @Override
                public void onFailure(Call<ResponseObject> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    hideLoading();
                    AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, "Có lỗi xảy ra, Vui lòng thử lại sau!");
                }
            });
        }
    }

    private void displayOrderDetails(OrderResponse order) {
        // Hide all status layouts first
//        layoutProcessingRefund.setVisibility(View.GONE);
//        layoutCompleted.setVisibility(View.GONE);
//        layoutRefundCompleted.setVisibility(View.GONE);
//        layoutCancelled.setVisibility(View.GONE);

        // Show appropriate layout based on order status
        switch (order.getStatus()) {
            case PENDING:
                tvOrderStatus.setText("Đang xử lý đơn hàng");
                setupPendingView(order);
                break;
//            case OrderShippingStatus.PROCESSING_RETURN_REFUND:
//                tvOrderStatus.setText("Đang xử lý yêu cầu Trả hàng/Hoàn tiền");
//                layoutProcessingRefund.setVisibility(View.VISIBLE);
//                // Setup specific information for this status
//                setupProcessingRefundView(order);
//                break;
//
            case PREPARING:
                tvOrderStatus.setText("Đơn hàng được nhà bán hàng chuẩn bị");
                tvOrderStatus.setBackgroundColor(Color.parseColor("#FFEB3B"));
                layoutCompleted.setVisibility(View.VISIBLE);
                layoutRefurn.setVisibility(View.GONE);
                // layoutCompleted.setVisibility(View.VISIBLE);
                // Setup specific information for this status
                break;
            case DELIVERING:
                tvOrderStatus.setText("Đơn hàng đang được giao đến bạn");
               // layoutCompleted.setVisibility(View.VISIBLE);
                // Setup specific information for this status
                break;
            case DELIVERED:
                tvOrderStatus.setText("Đơn hàng đã hoàn thành");
                layoutCompleted.setVisibility(View.VISIBLE);
               // layoutCompleted.setVisibility(View.VISIBLE);
                // Setup specific information for this status
                setupCompletedView(order);
                break;
//
//            case OrderStatus.REFUND_COMPLETED:
//                tvOrderStatus.setText("Hoàn tiền thành công");
//                layoutRefundCompleted.setVisibility(View.VISIBLE);
//                // Setup specific information for this status
//                setupRefundCompletedView(order);
//                break;
//
            case CANCELLED:
                tvOderTittle.setText("Cancelled Order Detail");
                tvOrderStatus.setVisibility(View.GONE);
                layoutCancelled.setVisibility(View.VISIBLE);
                layoutAddress.setVisibility(View.GONE);
                layoutCancelDetail.setVisibility(View.VISIBLE);
                binding.username.setText(order.getAddress().getFullName());
                String updatedAt = order.getUpdatedAt(); // Chuỗi ngày giờ
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                try {
                    Date date = dateFormat.parse(updatedAt); // Chuyển đổi thành đối tượng Date
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String formattedDate = outputFormat.format(date); // Định dạng lại ngày giờ
                    binding.ngayyeucau.setText(formattedDate);
                    binding.datetimeCancle.setText("vào lúc: "+formattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.paymentmethod.setText((order.getPaymentMethod().toString()));
                // Setup specific information for this status
                setupCancelledView(order);
                break;
            case RETURNED:
                tvOderTittle.setText("Return/Refund Order Detail");
                tvOrderStatus.setText("Đơn hàng đã được hoàn trả");
                tvOrderStatus.setVisibility(View.GONE);
                // layoutCompleted.setVisibility(View.VISIBLE);
                // Setup specific information for this status

                break;
        }


        // Setup common information for all statuses
        setupCommonInformation(order);
    }

    private void setupCompletedView(OrderResponse order)
    {

        binding.btnBuyAgain.setVisibility(View.VISIBLE);
        binding.btnRefund.setVisibility(View.VISIBLE);
        binding.btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<ResponseObject> returnOrder = apiService.returnOrder(order.getId());
                returnOrder.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                        ResponseObject responseObject = (ResponseObject) response.body();
                        hideLoading();
                        if(response.isSuccessful()){
                            binding.btnRefund.setVisibility(View.GONE);
                            AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, responseObject.getMessage());
                        }
                        else {
                            AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.ic_check, "Return the order failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        hideLoading();
                        AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, "Error");
                    }
                });
            }
        });
        binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
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
    }
    private void setupCancelledView(OrderResponse order){
        binding.btnBuyAgain.setVisibility(View.VISIBLE);
        binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
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
    }
    private void BuyAgain(List<AddToCartDTO> addToCartDTO)
    {
        showLoading();
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> addCart = apiService.addToCartOrder(addToCartDTO);
        addCart.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideLoading();
                if(response.isSuccessful()){
                    Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
                    intent.putExtra("navigate_to", "cart");  // Gửi thông điệp yêu cầu mở CartFragment
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else {
                    AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.ic_check, "Process failed");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                hideLoading();
                AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, "Error");
            }
        });

    }
    private void setupReturnView(OrderResponse order){
        binding.btnRefundDetail.setVisibility(View.VISIBLE);
        binding.btnRefundDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this, RefundDetail.class);
                startActivity(intent);
            }
        });
        binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
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
    }
    private void setupPendingView(OrderResponse order) {
        binding.btnCancelOrder.setVisibility(View.VISIBLE);
        binding.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder();
            }
        });
    }

    private void setupCommonInformation(OrderResponse order) {
        Address address = currentOrder.getAddress();
        binding.tvName.setText(address.getFullName());
        binding.tvPhone.setText(address.getPhone());
        binding.txtAddress.setText(address.getAddr());

        List<OrderItemResponse> orderItems = order.getOrderItems();
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter<>(orderItems, new OrderItemAdapter.OnToggleExpandListener(){
            @Override
            public void onToggleExpand(boolean isExpanded) {
                // Cập nhật text của button dựa vào trạng thái
//                holder.binding.btnToggleExpand.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                binding.textViewExpand.setText(isExpanded ? "See less" : "See more");
                binding.imageViewExpand.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            }
        });

        orderItemAdapter.setOrderStatus(order.getStatus().toString(), order.getId());
        // Ẩn nút nếu danh sách không thể mở rộng (số item <= 1)
        binding.btnToggleExpand.setVisibility(orderItemAdapter.canExpand() ? View.VISIBLE : View.GONE);
        binding.btnToggleExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đổi trạng thái mở rộng/thu gọn khi click nút
                orderItemAdapter.toggleExpand();
            }
        });
        binding.recyclerView.setAdapter(orderItemAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this, LinearLayoutManager.VERTICAL, false));

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(order.getTotalPrice());
        binding.tvTotalAmount.setText(priceText);
    }

    // Hiển thị process bar
    private void showLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);
        overlay.setClickable(false);
    }

    void cancelOrder(){
        showLoading();
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> cancelOrder = apiService.cancelOrder(currentOrder.getId());
        cancelOrder.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                ResponseObject responseObject = (ResponseObject) response.body();
                hideLoading();
                if(response.isSuccessful()){
                    AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.ic_tick, responseObject.getMessage());
                }
                else {
                    AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, responseObject.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                hideLoading();
                AppUtils.showDialogNotify(OrderDetailActivity.this, R.drawable.error, "Error, please try again later!");
            }
        });
    }
}