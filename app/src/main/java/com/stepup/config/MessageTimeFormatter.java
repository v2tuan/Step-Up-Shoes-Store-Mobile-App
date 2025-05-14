package com.stepup.config;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;
import java.util.Locale;

public class MessageTimeFormatter {

    /**
     * Định dạng thời gian thân thiện cho tin nhắn
     * - Nếu là hôm nay: Hiển thị chỉ giờ phút (VD: 3:30 PM)
     * - Nếu là hôm qua: Hiển thị "Yesterday at HH:mm"
     * - Nếu trong tuần này: Hiển thị tên thứ và giờ (VD: Monday at 3:30 PM)
     * - Nếu cách đây hơn 1 tuần: Hiển thị ngày/tháng/năm HH:mm (VD: 05/14/2025 3:30 PM)
     */
    public static String formatTime(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "";
        }

        try {
            // Phân tích chuỗi ISO thành LocalDateTime
            // ISO format: "2025-05-14T15:30:45.123Z" hoặc "2025-05-14T15:30:45"
            LocalDateTime messageDateTime = LocalDateTime.parse(isoDateString,
                    DateTimeFormatter.ISO_DATE_TIME);

            // Lấy ngày hiện tại
            LocalDate today = LocalDate.now();
            LocalDate messageDate = messageDateTime.toLocalDate();

            // Định dạng giờ:phút
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            String formattedTime = messageDateTime.format(timeFormatter);

            // Nếu là hôm nay
            if (messageDate.isEqual(today)) {
                return formattedTime;
            }

            // Nếu là hôm qua
            LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);
            if (messageDate.isEqual(yesterday)) {
                return "Yesterday at " + formattedTime;
            }

            // Nếu trong tuần này (cách đây dưới 7 ngày)
            LocalDate weekAgo = today.minus(7, ChronoUnit.DAYS);
            if (messageDate.isAfter(weekAgo)) {
                // Lấy tên thứ trong tuần
                DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
                String dayName = messageDateTime.format(dayFormatter);
                return dayName + " at " + formattedTime;
            }

            // Nếu cách đây hơn 1 tuần
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.ENGLISH);
            return messageDateTime.format(dateTimeFormatter);

        } catch (Exception e) {
            // Nếu có lỗi trong quá trình phân tích, trả về chuỗi gốc
            return isoDateString;
        }
    }
}