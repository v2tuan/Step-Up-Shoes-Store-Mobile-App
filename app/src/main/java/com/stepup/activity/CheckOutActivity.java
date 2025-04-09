package com.stepup.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.stepup.R;
import com.stepup.adapter.CartAdapter;
import com.stepup.adapter.CouponAdapter;
import com.stepup.adapter.OrderItemAdapter;
import com.stepup.databinding.ActivityCheckOutBinding;
import com.stepup.fragment.MyBottomSheetFragment;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.Address;
import com.stepup.model.Attribute;
import com.stepup.model.CartItem;
import com.stepup.model.Coupon;
import com.stepup.model.CouponCondition;
import com.stepup.model.ProductVariant;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends BaseActivity {
    private ActivityCheckOutBinding binding;
    private double taxRate = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_check_out);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Address defaultAddress = getDefaultAddress();
        getDefaultAddress();
        getOrderItems();
        setupRadioButtons();

        // Đảm bảo tính toán tổng sau khi Adapter hoàn tất quá trình binding dữ liệu
        // Bởi vì khi dùng post(), hành động calculateCart() sẽ được đưa vào queueMessage
        binding.rvOrderItems.post(() -> calculate());

        binding.btnCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetFragment bottomSheet = new MyBottomSheetFragment(new MyBottomSheetFragment.VoucherSelectedListener() {
                    @Override
                    public void onVoucherSelected(Coupon coupon) {
                        binding.getRoot().post(() -> {
                            CouponCondition couponConditionDiscount = coupon.getCouponConditionList().get(0);
                            for(CouponCondition condition : coupon.getCouponConditionList()){
                                if(condition.getAttribute().equals(Attribute.MINIMUM_AMOUNT)){
                                    if(calculate() < Double.parseDouble(condition.getValue())){
                                        return;
                                    }
                                }
                                if(condition.getAttribute().equals(Attribute.EXPIRY)){
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                                    LocalDate inputDate = LocalDate.parse(condition.getValue(), formatter);
                                    AndroidThreeTen.init(CheckOutActivity.this); // nap du lieu mui gio
                                    LocalDate today = LocalDate.now();
                                    if (today.isAfter(inputDate)) return;
                                }
                                if(condition.getAttribute().equals(Attribute.DISCOUNT)){
                                    couponConditionDiscount = condition;
                                }
                            }
                            taxRate = Double.parseDouble(couponConditionDiscount.getValue()) / 100;
                            calculate();
                        });
                    }
                });
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

//        SearchBar searchBar = binding.catSearchBar;
//        SearchView searchView = binding.catSearchView;
//        searchView.setupWithSearchBar(searchBar);
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
            List<CartItem> receivedList = intent.getParcelableArrayListExtra("orderItems");
            binding.rvOrderItems.setAdapter(new OrderItemAdapter(receivedList, new ChangeNumberItemsListener() {
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

    // Đặt code này trong Activity hoặc Fragment của bạn
    private void setupRadioButtons() {
        final RadioButton radioButtonCOD = findViewById(R.id.radioButtonCOD);
        final RadioButton radioButtonMoMo = findViewById(R.id.radioButtonMomo);
        final RadioButton radioButtonZalopay = findViewById(R.id.radioButtonZalopay);

        final RadioButton[] radioButtons = {radioButtonCOD, radioButtonMoMo, radioButtonZalopay};

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
                    // Thêm code xử lý của bạn tại đây
                } else if (clickedRadioButton.getId() == R.id.radioButtonMomo) {
                    // Xử lý khi "Payment by bank" được chọn
                    Log.d("PaymentSelection", "Selected: Payment by bank");
                    // Thêm code xử lý của bạn tại đây
                } else if (clickedRadioButton.getId() == R.id.radioButtonZalopay) {
                    // Xử lý khi "Payment by bank" được chọn
                    Log.d("PaymentSelection", "Selected: Payment by bank");
                    // Thêm code xử lý của bạn tại đây
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

            if (holder instanceof OrderItemAdapter.ViewHolder) {
                OrderItemAdapter.ViewHolder holderCartItem = (OrderItemAdapter.ViewHolder) holder;
                ProductVariant variant = holderCartItem.getCartItem().getProductVariant();
                totalFee += variant.getPromotionPrice() * holderCartItem.getCartItem().getCount();
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
}