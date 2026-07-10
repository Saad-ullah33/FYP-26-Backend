package com.propsightai.Role;

public enum SubscriptionFeature {
    CREATE_PROPERTY("Create Property"),
    PLACE_BID("Place Bid"),
    AI_FEATURES("AI Features Access"),
    PREMIUM_SEARCH("Premium Search Ranking"),
    RECOMMENDATION_ENGINE("Recommendation Engine"),
    ADVANCED_ANALYTICS("Advanced Analytics");

    private final String displayName;

    SubscriptionFeature(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
