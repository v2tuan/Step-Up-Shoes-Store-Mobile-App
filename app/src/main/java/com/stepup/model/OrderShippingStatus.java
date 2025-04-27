package com.stepup.model;

public enum OrderShippingStatus {
    PENDING,          // Đang chờ xử lý
    PREPARING,        // Đang chuẩn bị
    DELIVERING,       // Đang giao hàng
    DELIVERED,        // Đã giao hàng
    CANCELLED,        // Đã hủy
    RETURN_REQUESTED, // Yêu cầu trả hàng
    RETURNED         // Đã trả hàng
}
