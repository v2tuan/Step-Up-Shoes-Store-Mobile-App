package com.stepup.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.adapter.OrderItemAdapter;
import com.stepup.databinding.ActivityCheckOutBinding;
import com.stepup.fragment.MyBottomSheetFragment;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.Address;
import com.stepup.model.Attribute;
import com.stepup.model.CartItem;
import com.stepup.model.Coupon;
import com.stepup.model.CouponCondition;
import com.stepup.model.OrderDTO;
import com.stepup.model.OrderItemDTO;
import com.stepup.model.PaymentMethod;
import com.stepup.model.ProductVariant;
import com.stepup.model.ResponseObject;
import com.stepup.model.payment.PaymentDTO;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends BaseActivity {
    private ActivityCheckOutBinding binding;
    private double taxRate = 0;
    private Coupon couponSelected = null;
    private Address addressSelected;
    private List<CartItem> orderItemList;
    private PaymentMethod paymentMethod = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getDefaultAddress();
        getOrderItems();
        setupPaymentMethodSelection();
        setupCouponButton();
        setupCheckOutButton();
        setupAddress();
        // Đảm bảo tính toán tổng sau khi Adapter hoàn tất quá trình binding dữ liệu
        // Bởi vì khi dùng post(), hành động calculateCart() sẽ được đưa vào queueMessage
        binding.rvOrderItems.post(() -> calculate());


    }
    private void setupAddress()
    {
        binding.addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CheckOutActivity.this, AddressSelectActivity.class);
            if (addressSelected != null) {
                intent.putExtra("selectedAddressId", addressSelected.getId());
            }
            startActivityForResult(intent,0);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Address selectedAddress = (Address) data.getSerializableExtra("selectedAddress");
            if (selectedAddress != null) {
                addressSelected = selectedAddress;
                binding.txtName.setText(selectedAddress.getFullName());
                binding.txtPhone.setText(selectedAddress.getPhone());
                binding.txtAddress.setText(selectedAddress.getAddr());
            }
        }
    }

    private void setupCheckOutButton() {
        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderItemList == null || orderItemList.isEmpty()){
                    AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, "The order item cannot be empty");
                    return;
                }

                List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
                for(CartItem orderItem : orderItemList) {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setProductVariantId(orderItem.getProductVariant().getId());
                    orderItemDTO.setCount(orderItem.getCount());
                    orderItemDTOS.add(orderItemDTO);
                }

                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setOrderItems(orderItemDTOS);
                if(addressSelected == null){
                    AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, "Chưa chọn địa chỉ nhận hàng");
                    return;
                }
                orderDTO.setAddressId(addressSelected.getId());

                if(couponSelected != null) {
                    orderDTO.setCouponId(couponSelected.getId());
                }

                if (paymentMethod != null) {
                    orderDTO.setPaymentMethod(paymentMethod);
                }
                else {
                    AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, "Please select a payment method");
                    return;
                }

                APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                Call<ResponseObject> callCreateOrder = apiService.createOrder(orderDTO);

                showLoading();
                callCreateOrder.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                        ResponseObject responseObject = response.body(); // Lấy JSON từ server trả về
                        if(response.isSuccessful()){
                            // ✅ Thành công (status code 200-299)
//                            AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.ic_tick, responseObject.getMessage());
                            Log.i(TAG, "Đặt hàng thành công");
                            if(orderDTO.getPaymentMethod() == PaymentMethod.VNPAY){
                                redirectPaymentActivity(((Double) responseObject.getData()).longValue());
                            }
                            else{
                                Intent intent = new Intent(CheckOutActivity.this, OrderResultActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // ❌ Không thành công (ví dụ 400 hoặc 500)
                            try {
                                hideLoading();
                                // Có thể parse bằng Gson nếu server vẫn trả JSON
                                AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, responseObject.getMessage());
                            } catch (Exception e) {
                                hideLoading();
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        // ❌ Lỗi kết nối đến server (mất mạng, timeout,...)
                        showLoading();
                        Log.e(TAG, t.getMessage());
                        AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, "Có lỗi xảy ra vui lòng thử lại sau!");
                    }
                });
            }
        });
    }

    private void redirectPaymentActivity(Long orderId){
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(orderId);
        paymentDTO.setLanguage("vn");

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> callCreatePayment = apiService.createPayment(paymentDTO);
        callCreatePayment.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                ResponseObject responseObject = response.body();
                if(response.isSuccessful()){
                    Intent intent = new Intent(CheckOutActivity.this, PaymentActivity.class);
                    intent.putExtra("paymentURL", responseObject.getData().toString());
                    startActivity(intent);
                    finish();
                }
                else {
                    hideLoading();
                    // ❌ Không thành công (ví dụ 400 hoặc 500)
                    AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, "Có lỗi xáy ra vui lòng thử lại sau");
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                hideLoading();
                // ❌ Lỗi kết nối đến server (mất mạng, timeout,...)
                AppUtils.showDialogNotify(CheckOutActivity.this, R.drawable.error, t.getMessage());
            }
        });
    }

    private void setupCouponButton() {
        binding.btnCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetFragment bottomSheet = new MyBottomSheetFragment(new MyBottomSheetFragment.VoucherSelectedListener() {
                    @Override
                    public void onVoucherSelected(Coupon coupon) {
                        binding.getRoot().post(() -> {
                            CouponCondition couponConditionDiscount = coupon.getCouponConditionList().get(0);
                            for(CouponCondition condition : coupon.getCouponConditionList()){
                                switch (condition.getAttribute()) {
                                    case MINIMUM_AMOUNT:
                                        if (calculate() < Double.parseDouble(condition.getValue())) return;
                                        break;
                                    case EXPIRY:
                                        LocalDate expiry = LocalDate.parse(condition.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                        AndroidThreeTen.init(CheckOutActivity.this); // nap du lieu mui gio
                                        if (LocalDate.now().isAfter(expiry)) return;
                                        break;
                                    case DISCOUNT:
                                        couponConditionDiscount = condition;
                                        break;
                                }
                            }
                            couponSelected = coupon;
                            taxRate = Double.parseDouble(couponConditionDiscount.getValue()) / 100;
                            calculate();
                        });
                    }
                });
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    private void getDefaultAddress() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> call = apiService.getDefaultAddress();
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject responseObject = response.body();
                    if(responseObject.getData() != null){
                        // Kiểm tra xem obj có phải là LinkedTreeMap không
                        if (responseObject.getData() instanceof LinkedTreeMap) {
                            Gson gson = new Gson();
                            String json = gson.toJson(responseObject.getData());  // Chuyển đổi LinkedTreeMap thành chuỗi JSON
                            Address defaultAddress = gson.fromJson(json, Address.class);  // Chuyển chuỗi JSON thành Address
                            addressSelected = defaultAddress;

                            // Tiến hành xử lý đối tượng Address
                            binding.txtName.setText(defaultAddress.getFullName());
                            binding.txtPhone.setText(defaultAddress.getPhone());
                            binding.txtAddress.setText(defaultAddress.getAddr());
                        } else {
                            // Xử lý trường hợp obj không phải là LinkedTreeMap hoặc Address
                            Log.e("Error", "Dữ liệu trả về không phải là LinkedTreeMap");
                        }
                    }

             } else {
                    Toast.makeText(CheckOutActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(CheckOutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderItems() {
        try {
            Intent intent = getIntent();
            orderItemList = intent.getParcelableArrayListExtra("orderItems");
            binding.rvOrderItems.setAdapter(new OrderItemAdapter(orderItemList, new ChangeNumberItemsListener() {
                @Override
                public void onChanged() {
                    calculate();
                }
            }));
            binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom)); // drawable divider của bạn
            binding.rvOrderItems.addItemDecoration(dividerItemDecoration);
        }
        catch (Exception e){
            Log.e("Error Get Order Items", e.getMessage());
        }
    }

    private void setupPaymentMethodSelection() {
        final RadioButton radioButtonCOD = findViewById(R.id.radioButtonCOD);
        final RadioButton radioButtonZalopay = findViewById(R.id.radioButtonVnpay);

        final RadioButton[] radioButtons = {radioButtonCOD, radioButtonZalopay};

        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xác định RadioButton được click
                RadioButton clickedRadioButton = (RadioButton) view;

                // Nếu RadioButton đã được click đang ở trạng thái chưa check
                // thì đảm bảo nó được check
                // (đôi khi Android UI có thể vô hiệu hóa việc check khi click)
                if (!clickedRadioButton.isChecked()) {
                    clickedRadioButton.setChecked(true);
                }

                // Bỏ chọn tất cả RadioButton khác
                for (RadioButton rb : radioButtons) {
                    if (rb != clickedRadioButton) {
                        rb.setChecked(false);
                    }
                }

                // Xử lý logic khi lựa chọn thay đổi
                if (clickedRadioButton.getId() == R.id.radioButtonCOD) {
                    // Xử lý khi "Pay when receiving goods" được chọn
                    Log.d("PaymentSelection", "Selected: Pay when receiving goods");
                    paymentMethod = PaymentMethod.COD;
                } else if (clickedRadioButton.getId() == R.id.radioButtonVnpay) {
                    // Xử lý khi "Payment by bank" được chọn
                    Log.d("PaymentSelection", "Selected: Payment by zalopay");
                    paymentMethod = PaymentMethod.VNPAY;
                }
            }
        };

        // Thiết lập listener cho mỗi RadioButton
        for (RadioButton rb : radioButtons) {
            rb.setOnClickListener(radioClickListener);
        }

        // Thiết lập giá trị mặc định (nếu cần)
        // radioButton1.setChecked(true);
    }

    private double calculate() {
        double totalFee = 0;
        double feeDelivery = 30000;
        if (binding.rvOrderItems.getAdapter() == null) return 0; // Kiểm tra tránh lỗi NullPointerException

        // Duyệt qua tất cả các item trong RecyclerView để tính tổng
        for (int i = 0; i < binding.rvOrderItems.getChildCount(); i++) {
            View child = binding.rvOrderItems.getChildAt(i);
            RecyclerView.ViewHolder holder = binding.rvOrderItems.getChildViewHolder(child);

            if (holder instanceof OrderItemAdapter.ViewHolderOrderItem1) {
                OrderItemAdapter.ViewHolderOrderItem1 holderCartItem = (OrderItemAdapter.ViewHolderOrderItem1) holder;
                ProductVariant variant = holderCartItem.getCartItem().getProductVariant();

                if (variant != null && variant.getPromotionPrice() != null) {
                    totalFee += variant.getPromotionPrice() * holderCartItem.getCartItem().getCount();
                }
            }
        }

        // Định dạng tiền tệ
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(totalFee);
        String feeDeliveryText = format.format(feeDelivery);
        Double totalTax = totalFee * taxRate;
        String totalTaxText = format.format(totalTax);
        binding.totalFeeTxt.setText(priceText);
        binding.deliveryTxt.setText(feeDeliveryText);
        binding.txtDiscount.setText(totalTaxText);
        binding.taxTxt.setText( "- " + totalTaxText);
        if(totalTax > 0){
            binding.txtDiscountContainer.setVisibility(View.VISIBLE);
        }
        double total = totalFee + feeDelivery - totalTax;
        String totalPriceText = format.format(total);
        binding.totalTxt.setText(totalPriceText);
        binding.txtTotalPrice.setText(totalPriceText);
        return total;
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
}