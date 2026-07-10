package com.propsightai.Role;

public enum ActivityEventType {
    PROPERTY_VIEW("Property viewed"),
    PROPERTY_CLICK("Property clicked"),
    AUCTION_VIEW("Auction viewed"),
    BID_PLACED("Bid placed"),
    PROPERTY_FAVORITE("Property favorited"),
    PROPERTY_SHARE("Property shared"),
    SEARCH_PERFORMED("Search performed"),
    FILTER_APPLIED("Filter applied");

    private final String description;

    ActivityEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
