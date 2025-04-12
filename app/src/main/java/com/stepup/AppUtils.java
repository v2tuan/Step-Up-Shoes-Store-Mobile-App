package com.stepup;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;

import com.stepup.databinding.NotifyDialogBinding;

public class AppUtils {
    // Hiển thị Dialog không làm tối nền
    public static void showDialogNotify(Activity activity, @DrawableRes int imageResId, String message) {
        Dialog dialog = new Dialog(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.notify_dialog, null);
        dialog.setContentView(dialogView);

        // Không làm tối nền
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Binding layout
        NotifyDialogBinding binding = NotifyDialogBinding.bind(dialogView); // Dùng bind thay vì inflate lại
        binding.imageView.setImageResource(imageResId);
        binding.textView.setText(message);

        dialog.show();

        // Thiết lập lại kích thước dialog theo đúng layout
        Window window = dialog.getWindow();
        if (window != null) {
            // Làm nền của dialog trong suốt
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Tự động đóng sau 1 giây
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 2000);
    }

}
