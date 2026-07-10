package com.propsightai.Role;

public enum PaymentMethod {
    JAZZCASH("JazzCash", "PKR"),
    EASYPAISA("EasyPaisa", "PKR"),
    BANK_TRANSFER("Bank Transfer", "PKR"),
    DEBIT_CARD("Debit Card", "PKR"),
    CREDIT_CARD("Credit Card", "PKR");

    private final String displayName;
    private final String currency;

    PaymentMethod(String displayName, String currency) {
        this.displayName = displayName;
        this.currency = currency;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCurrency() {
        return currency;
    }
}
