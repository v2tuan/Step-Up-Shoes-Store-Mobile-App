package com.stepup.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.activity.OrderOverviewActivity;
import com.stepup.adapter.OrderAdapter;
import com.stepup.databinding.FragmentPendingOrdersBinding;
import com.stepup.databinding.FragmentPreparingOrdersBinding;
import com.stepup.model.OrderResponse;
import com.stepup.model.OrderShippingStatus;
import com.stepup.model.ResponseObject;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreparingOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreparingOrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PreparingOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProcessingOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreparingOrdersFragment newInstance(String param1, String param2) {
        PreparingOrdersFragment fragment = new PreparingOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentPreparingOrdersBinding binding;
    Call<ResponseObject> callOrderByStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreparingOrdersBinding.inflate(inflater, container, false);
        getOrder();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void getOrder(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        callOrderByStatus = apiService.getOrdersByStatus(OrderShippingStatus.PREPARING.toString());

        callOrderByStatus.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (!isAdded() || getContext() == null) return; // Kiểm tra fragment còn sống

                if (response.isSuccessful()){
                    // ✅ Thành công (status code 200-299)
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData()); // convert data thành JSON string

                    Type type = new TypeToken<List<OrderResponse>>() {}.getType();
                    List<OrderResponse> orderList = gson.fromJson(json, type); // parse lại JSON thành List<Order>

                    OrderAdapter orderAdapter = new OrderAdapter(orderList);
                    binding.rvOrderContainer.setAdapter(orderAdapter);
                    binding.rvOrderContainer.setLayoutManager(
                            new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    );
                    if(!orderList.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.GONE);
                    }
                    else {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
                else {
                    // ❌ Không thành công (ví dụ 400 hoặc 500)
                    AppUtils.showDialogNotify(requireActivity(), R.drawable.error, "Có lỗi xảy ra, Vui lòng thử lại sau!");
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                // ❌ Lỗi kết nối đến server (mất mạng, timeout,...)
                if (!isAdded() || getContext() == null) return; // Kiểm tra fragment còn sống

                Log.e(TAG, t.getMessage());
                AppUtils.showDialogNotify(requireActivity(), R.drawable.error, "Có lỗi xảy ra, Vui lòng thử lại sau!");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callOrderByStatus != null && !callOrderByStatus.isCanceled()) {
            callOrderByStatus.cancel();
        }
    }
}