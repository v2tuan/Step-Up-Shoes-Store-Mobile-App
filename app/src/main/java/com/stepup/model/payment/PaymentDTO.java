package com.stepup.model.payment;

public class PaymentDTO {
    private Long orderId; // Số tiền cần thanh toán
    private String bankCode;
    /// Mã phương thức thanh toán, mã loại ngân hàng hoặc ví điện tử thanh toán.
    /// Nếu không gửi sang tham số này, chuyển hướng người dùng sang VNPAY chọn phương thức thanh toán.
    private String language; // Ngôn ngữ giao diện thanh toán (vd: "vn", "en")

    public PaymentDTO() {
    }

    public PaymentDTO(Long orderId, String bankCode, String language) {
        this.orderId = orderId;
        this.bankCode = bankCode;
        this.language = language;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
