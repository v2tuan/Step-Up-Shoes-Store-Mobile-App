package com.stepup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;

import com.stepup.databinding.NotifyDialogBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public static String getRealPathFromURI(Context context, Uri uri) throws IOException {
        // Kiểm tra nếu Uri là từ MediaStore
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                // Nếu không lấy được trực tiếp, sao chép file vào cache
                return copyFileToCache(context, uri);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Nếu Uri là file trực tiếp
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        // Mặc định: sao chép file vào cache
        return copyFileToCache(context, uri);
    }

    // Sao chép file từ Uri vào thư mục cache
    private static String copyFileToCache(Context context, Uri uri) throws IOException {
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File cacheFile = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(cacheFile)) {
            if (inputStream == null) {
                throw new IOException("Không thể mở input stream từ Uri");
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return cacheFile.getAbsolutePath();
    }

}
