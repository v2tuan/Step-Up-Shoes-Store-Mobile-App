package com.stepup.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.stepup.R;
import com.stepup.activity.AddAddressActivity;
import com.stepup.activity.CheckOutActivity;
import com.stepup.activity.DetailActivity;
import com.stepup.adapter.CartAdapter;
import com.stepup.adapter.SizeAdapter;
import com.stepup.databinding.ActivityAddAddressBinding;
import com.stepup.databinding.FragmentCartBinding;
import com.stepup.databinding.FragmentHomeBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.ProductVariant;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentCartBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);

        showLoading();

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<CartItem>> callCart = apiService.getAllCartItem();
        callCart.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> cartItems = response.body();
                    binding.viewCart.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    // Thiết lập Adapter
                    CartAdapter cartAdapter = new CartAdapter(cartItems, new ChangeNumberItemsListener() {
                        @Override
                        public void onChanged() {
                            calculateCart();
                        }
                    }, binding);

                    binding.viewCart.setAdapter(cartAdapter);

                    // Đảm bảo tính toán tổng sau khi Adapter hoàn tất quá trình binding dữ liệu
                    // Bởi vì khi dùng post(), hành động calculateCart() sẽ được đưa vào queueMessage
                    binding.viewCart.post(() -> calculateCart());

                    if (cartItems.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                        binding.scrollView2.setVisibility(View.GONE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                        binding.scrollView2.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                hideLoading();
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });

        binding.btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }

    // Hiển thị process bar
    private void showLoading() {
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        binding.overlay.setVisibility(View.GONE);
        binding.overlay.setClickable(false);
    }

    private void calculateCart() {
        double total = 0;
        if (binding.viewCart.getAdapter() == null) return; // Kiểm tra tránh lỗi NullPointerException

        // Duyệt qua tất cả các item trong RecyclerView để tính tổng
        for (int i = 0; i < binding.viewCart.getChildCount(); i++) {
            View child = binding.viewCart.getChildAt(i);
            RecyclerView.ViewHolder holder = binding.viewCart.getChildViewHolder(child);

            if (holder instanceof CartAdapter.ViewHolder) {
                CartAdapter.ViewHolder holderCartItem = (CartAdapter.ViewHolder) holder;
                ProductVariant variant = holderCartItem.getCartItem().getProductVariant();
                total += variant.getPromotionPrice() * holderCartItem.getCartItem().getCount();
            }
        }

        // Định dạng tiền tệ
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(total);
        binding.totalTxt.setText(priceText);
    }
}