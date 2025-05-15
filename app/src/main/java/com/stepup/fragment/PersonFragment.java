package com.stepup.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.stepup.R;
import com.stepup.activity.AddressActivity;
import com.stepup.activity.ChatActivity;
import com.stepup.activity.LoginActivity;
import com.stepup.activity.OrderOverviewActivity;
import com.stepup.adapter.CartAdapter;
import com.stepup.config.WebSocketManager;
import com.stepup.databinding.FragmentHomeBinding;
import com.stepup.databinding.FragmentPersonBinding;
import com.stepup.listener.ChangeNumberItemsListener;
import com.stepup.model.CartItem;
import com.stepup.model.OrderShippingStatus;
import com.stepup.model.User;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FragmentPersonBinding binding;
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
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        getUser();
        showLoading();
        setEvent();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void setEvent() {
        binding.btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddressActivity.class));
            }
        });

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.pending) {
                Intent intent = new Intent(requireContext(), OrderOverviewActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.goods) {
                Intent intent = new Intent(requireContext(), OrderOverviewActivity.class);
                intent.putExtra("tab_position", OrderShippingStatus.PREPARING.ordinal());
                startActivity(intent);
            } else if (item.getItemId() == R.id.delivery) {
                Intent intent = new Intent(requireContext(), OrderOverviewActivity.class);
                intent.putExtra("tab_position", OrderShippingStatus.DELIVERING.ordinal());
                startActivity(intent);
            } else if (item.getItemId() == R.id.settings) {
                // Activity Settings
            }
            return true;
        });

        binding.btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).edit();
                editor.remove("token");
                editor.remove("rememberMe");
                editor.apply();

                WebSocketManager webSocketManager = WebSocketManager.getInstance();
                webSocketManager.disconnect();

                // Navigate to LoginActivity
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    private void getUser(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<User> callProfile = apiService.profile();
        callProfile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if(user == null){
                    return;
                }
                binding.tvName.setText(user.getFullName());
                if(user.getProfileImage() != null) {
                    Glide.with(getContext())
                            .load(user.getProfileImage())
                            .into(binding.profileImage);
                }
                else {
                    binding.profileImage.setImageResource(R.drawable.default_avatar);
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                hideLoading();
                Log.e("RetrofitError", "Error: " + t.getMessage());
            }
        });
    }

    // Hiển thị process bar
    private void showLoading() {
        FrameLayout overlay = binding.overlay;
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        FrameLayout overlay = binding.overlay;
        overlay.setVisibility(View.GONE);
        overlay.setClickable(false);
    }
}