package com.stepup.fragment;

import android.app.Dialog;
import android.os.Bundle;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentMyBottomSheetBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBottomSheetBinding.inflate(inflater);
        getAllCoupons();
        // Inflate layout của BottomSheet
        return binding.getRoot();
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

                            binding.rvCoupon.setAdapter(new CouponAdapter(couponList));
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
