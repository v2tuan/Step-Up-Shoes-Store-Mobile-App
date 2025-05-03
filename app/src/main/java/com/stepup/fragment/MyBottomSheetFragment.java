package com.stepup.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.CheckOutActivity;
import com.stepup.adapter.CartAdapter;
import com.stepup.adapter.CouponAdapter;
import com.stepup.databinding.FragmentCartBinding;
import com.stepup.databinding.FragmentMyBottomSheetBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.Address;
import com.stepup.model.CartItem;
import com.stepup.model.Coupon;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentMyBottomSheetBinding binding;
    private List<Coupon> allCoupons = new ArrayList<>();
    public interface VoucherSelectedListener {
        void onVoucherSelected(Coupon coupon);
    }

    private VoucherSelectedListener voucherSelectedListener;

    public void setVoucherSelectedListener(VoucherSelectedListener listener) {
        this.voucherSelectedListener = listener;
    }

    public MyBottomSheetFragment(VoucherSelectedListener voucherSelectedListener) {
        this.voucherSelectedListener = voucherSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBottomSheetBinding.inflate(inflater);
        getAllCoupons();
        searchCoupons();
        applyDiscount();
        // Inflate layout của BottomSheet
        return binding.getRoot();
    }
    private void applyDiscount() {
        binding.btnApply.setOnClickListener(view -> {
            // Lấy adapter hiện tại
            CouponAdapter adapter = (CouponAdapter) binding.rvCoupon.getAdapter();
            if (adapter != null) {
                Coupon selectedCoupon = adapter.getSelectedCoupon();
                if (selectedCoupon != null) {
                    // Gửi dữ liệu selectedCoupon về qua listener
                    if (voucherSelectedListener != null) {
                        voucherSelectedListener.onVoucherSelected(selectedCoupon);
                    }
                    // Đóng BottomSheet sau khi chọn
                    dismiss();
                } else {
                    // Nếu chưa chọn coupon nào
                    AppUtils.showDialogNotify(requireActivity(), R.drawable.error, "Please Choose Coupon !");
                }
            }
        });
    }

    private void searchCoupons(){
        binding.searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Không cần thực hiện gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Lọc danh sách khi văn bản thay đổi
                filterVoucherList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void filterVoucherList(String query) {
        List<Coupon> filteredList = new ArrayList<>();
        for (Coupon voucher : allCoupons) { // Sử dụng allCoupons đã lưu trữ từ API
            if (voucher.getCode().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(voucher);
            }
        }
        // Cập nhật danh sách trong adapter
        CouponAdapter couponAdapter = (CouponAdapter) binding.rvCoupon.getAdapter();
        if (couponAdapter != null) {
            couponAdapter.updateList(filteredList);
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                // Mở rộng tối đa khi BottomSheet xuất hiện
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // Cho phép kéo thả và mở rộng tối đa theo chiều cao của nội dung
                behavior.setSkipCollapsed(true); // Bỏ trạng thái COLLAPSED nếu muốn
                behavior.setHideable(true); // Có thể kéo xuống để đóng

                // Cài đặt chiều cao tối đa của BottomSheet (có thể kéo thả)
            }
        });

        return dialog;
    }

    private void getAllCoupons(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseObject> call = apiService.getAllCoupons();
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject responseObject = response.body();
                    if(responseObject.getData() != null){
                        // Kiểm tra xem obj có phải là LinkedTreeMap không
                        List<?> dataList = (List<?>) responseObject.getData();
                        if (!dataList.isEmpty() && dataList.get(0) instanceof LinkedTreeMap) {
                            Gson gson = new Gson();
                            String json = gson.toJson(responseObject.getData());  // Chuyển đổi LinkedTreeMap thành chuỗi JSON
                            List<Coupon> couponList = gson.fromJson(json, new TypeToken<List<Coupon>>(){}.getType());
                            allCoupons = couponList;
                            binding.rvCoupon.setAdapter(new CouponAdapter(couponList, voucherSelectedListener));
                            binding.rvCoupon.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
                            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_custom)); // drawable divider của bạn
                            binding.rvCoupon.addItemDecoration(dividerItemDecoration);

                            DefaultItemAnimator animator = new DefaultItemAnimator();
                            animator.setSupportsChangeAnimations(false);
                            binding.rvCoupon.setItemAnimator(animator);

                        } else {
                            // Xử lý trường hợp obj không phải là LinkedTreeMap hoặc Address
                            Log.e("Error", "Dữ liệu trả về không phải là LinkedTreeMap");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }
}
