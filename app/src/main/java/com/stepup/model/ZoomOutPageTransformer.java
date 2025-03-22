package com.stepup.model;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f; // Tỷ lệ thu nhỏ tối thiểu cho các trang không được focus
    private static final float MARGIN = 15f; // Khoảng cách giữa các phần tử (đơn vị: pixel)

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();  // Lấy chiều rộng của trang
        int pageHeight = view.getHeight(); // Lấy chiều cao của trang

        if (position < -1) { // Trang nằm ngoài màn hình bên trái (hoàn toàn không hiển thị)
            // Thu nhỏ trang và không hiển thị
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // Trang đang hiển thị hoặc một phần nằm trong màn hình
            // Tính toán tỷ lệ thu nhỏ dựa trên vị trí của trang
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));

            // Tính toán khoảng cách lề để căn giữa trang khi thu nhỏ
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            // Điều chỉnh vị trí ngang của trang để tạo hiệu ứng chuyển động mượt mà và thêm khoảng cách
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2 + MARGIN * position);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2 + MARGIN * position);
            }

            // Áp dụng tỷ lệ thu nhỏ cho trang
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // Trang nằm ngoài màn hình bên phải (hoàn toàn không hiển thị)
            // Thu nhỏ trang và không hiển thị
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        }
    }
}
