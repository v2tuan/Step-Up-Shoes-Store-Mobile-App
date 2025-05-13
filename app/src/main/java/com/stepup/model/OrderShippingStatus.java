package com.stepup.model;

import java.io.Serializable;

public enum OrderShippingStatus implements Serializable {
    PENDING,          // Đang chờ xử lý
    PREPARING,        // Đang chuẩn bị
    DELIVERING,       // Đang giao hàng
    DELIVERED,        // Đã giao hàng
    CANCELLED,        // Đã hủy
    RETURNED         // Đã trả hàng
}
