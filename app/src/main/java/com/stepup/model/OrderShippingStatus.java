package com.stepup.model;

public enum OrderShippingStatus {
    PENDING,          // Đang chờ xử lý
    PREPARING,        // Đang chuẩn bị
    DELIVERING,       // Đang giao hàng
    DELIVERED,        // Đã giao hàng
    CANCELLED,        // Đã hủy
    RETURNED         // Đã trả hàng
}
