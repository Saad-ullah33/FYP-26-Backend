package com.propsightai.Role;

public enum SubscriptionPlan {
    FREE("Free Tier", 0, "Limited access - view only"),
    BASIC("Basic Plan", 500, "10 properties/month - no bidding"),
    PREMIUM("Premium Plan", 2000, "Unlimited properties & bidding");

    private final String displayName;
    private final int monthlyPriceInPKR;
    private final String description;

    SubscriptionPlan(String displayName, int monthlyPriceInPKR, String description) {
        this.displayName = displayName;
        this.monthlyPriceInPKR = monthlyPriceInPKR;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMonthlyPriceInPKR() {
        return monthlyPriceInPKR;
    }

    public String getDescription() {
        return description;
    }
}
