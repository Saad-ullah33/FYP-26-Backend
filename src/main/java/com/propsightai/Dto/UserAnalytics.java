package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class UserAnalytics {

    @NotNull
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String userEmail;

    @NotNull
    @JsonProperty("total_bids")
    private Integer totalBids;

    @NotNull
    @JsonProperty("successful_bids")
    private Integer successfulBids;

    @JsonProperty("total_spent")
    private Double totalSpent;

    @JsonProperty("properties_viewed")
    private Long propertiesViewed;

    @JsonProperty("auctions_participated")
    private Integer auctionsParticipated;

    // Constructors
    public UserAnalytics() {
    }

    public UserAnalytics(Integer userId, String userName, Integer totalBids, Integer successfulBids) {
        this.userId = userId;
        this.userName = userName;
        this.totalBids = totalBids;
        this.successfulBids = successfulBids;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getTotalBids() {
        return totalBids;
    }

    public void setTotalBids(Integer totalBids) {
        this.totalBids = totalBids;
    }

    public Integer getSuccessfulBids() {
        return successfulBids;
    }

    public void setSuccessfulBids(Integer successfulBids) {
        this.successfulBids = successfulBids;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Long getPropertiesViewed() {
        return propertiesViewed;
    }

    public void setPropertiesViewed(Long propertiesViewed) {
        this.propertiesViewed = propertiesViewed;
    }

    public Integer getAuctionsParticipated() {
        return auctionsParticipated;
    }

    public void setAuctionsParticipated(Integer auctionsParticipated) {
        this.auctionsParticipated = auctionsParticipated;
    }
}
