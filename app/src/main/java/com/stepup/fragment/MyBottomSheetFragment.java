package com.stepup.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.internal.ViewUtils;
import com.stepup.R;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout của BottomSheet
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false);
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
}
