package com.stepup.model;

import java.io.Serializable;

public enum PaymentStatus implements Serializable {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    REFUNDING
}