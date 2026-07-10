package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class AuctionAnalytics {

    @NotNull
    @JsonProperty("auction_id")
    private Integer auctionId;

    @JsonProperty("property_id")
    private Integer propertyId;

    @JsonProperty("property_title")
    private String propertyTitle;

    @NotNull
    @JsonProperty("bid_count")
    private Integer bidCount;

    @NotNull
    @JsonProperty("bidder_count")
    private Integer bidderCount;

    @JsonProperty("current_highest_bid")
    private Double currentHighestBid;

    @JsonProperty("view_count")
    private Long viewCount;

    @JsonProperty("status")
    private String status;

    // Constructors
    public AuctionAnalytics() {
    }

    public AuctionAnalytics(Integer auctionId, Integer bidCount, Integer bidderCount, Double currentHighestBid) {
        this.auctionId = auctionId;
        this.bidCount = bidCount;
        this.bidderCount = bidderCount;
        this.currentHighestBid = currentHighestBid;
    }

    // Getters and Setters
    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

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

    public Integer getBidCount() {
        return bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }

    public Integer getBidderCount() {
        return bidderCount;
    }

    public void setBidderCount(Integer bidderCount) {
        this.bidderCount = bidderCount;
    }

    public Double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(Double currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
