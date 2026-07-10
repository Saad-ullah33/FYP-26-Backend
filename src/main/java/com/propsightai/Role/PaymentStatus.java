package com.propsightai.Role;

public enum PaymentStatus {
    PENDING("Payment pending"),
    SUCCESS("Payment successful"),
    FAILED("Payment failed"),
    CANCELLED("Payment cancelled"),
    REFUNDED("Payment refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
