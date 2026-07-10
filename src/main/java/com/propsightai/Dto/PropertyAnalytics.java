package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class PropertyAnalytics {

    @NotNull
    @JsonProperty("property_id")
    private Integer propertyId;

    @JsonProperty("property_title")
    private String propertyTitle;

    @NotNull
    @JsonProperty("view_count")
    private Long viewCount;

    @NotNull
    @JsonProperty("click_count")
    private Long clickCount;

    @JsonProperty("bid_count")
    private Long bidCount;

    @JsonProperty("favorite_count")
    private Long favoriteCount;

    // Constructors
    public PropertyAnalytics() {
    }

    public PropertyAnalytics(Integer propertyId, String propertyTitle, Long viewCount, Long clickCount) {
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.viewCount = viewCount;
        this.clickCount = clickCount;
    }

    // Getters and Setters
    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getBidCount() {
        return bidCount;
    }

    public void setBidCount(Long bidCount) {
        this.bidCount = bidCount;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
