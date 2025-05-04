package com.stepup.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.stepup.activity.OrderOverviewActivity;
import com.stepup.fragment.CancelledOrdersFragment;
import com.stepup.fragment.ReturnOrdersFragment;
import com.stepup.fragment.DeliveredOrdersFragment;
import com.stepup.fragment.DeliveringOrdersFragment;
import com.stepup.fragment.PendingOrdersFragment;
import com.stepup.fragment.PreparingOrdersFragment;
import com.stepup.model.OrderShippingStatus;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (OrderShippingStatus.values()[position]) {
            case PENDING: return new PendingOrdersFragment();
            case PREPARING: return new PreparingOrdersFragment();
            case DELIVERING: return new DeliveringOrdersFragment();
            case DELIVERED: return new DeliveredOrdersFragment();
            case CANCELLED: return new CancelledOrdersFragment();
            case RETURNED:
                return new ReturnOrdersFragment();
            default: throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return OrderShippingStatus.values().length;
    }
}
