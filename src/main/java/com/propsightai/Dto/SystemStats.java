package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class SystemStats {

    @NotNull
    @JsonProperty("total_users")
    private Long totalUsers;

    @NotNull
    @JsonProperty("active_users_today")
    private Long activeUsersToday;

    @NotNull
    @JsonProperty("total_properties")
    private Long totalProperties;

    @NotNull
    @JsonProperty("active_auctions")
    private Long activeAuctions;

    @NotNull
    @JsonProperty("total_bids")
    private Long totalBids;

    @NotNull
    @JsonProperty("bids_today")
    private Long bidsToday;

    @JsonProperty("properties_viewed_today")
    private Long propertiesViewedToday;

    @JsonProperty("total_properties_viewed")
    private Long totalPropertiesViewed;

    @JsonProperty("revenue_estimate")
    private Double revenueEstimate;

    // Constructors
    public SystemStats() {
    }

    public SystemStats(Long totalUsers, Long activeUsersToday, Long totalProperties, Long activeAuctions, Long totalBids) {
        this.totalUsers = totalUsers;
        this.activeUsersToday = activeUsersToday;
        this.totalProperties = totalProperties;
        this.activeAuctions = activeAuctions;
        this.totalBids = totalBids;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsersToday() {
        return activeUsersToday;
    }

    public void setActiveUsersToday(Long activeUsersToday) {
        this.activeUsersToday = activeUsersToday;
    }

    public Long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(Long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public Long getActiveAuctions() {
        return activeAuctions;
    }

    public void setActiveAuctions(Long activeAuctions) {
        this.activeAuctions = activeAuctions;
    }

    public Long getTotalBids() {
        return totalBids;
    }

    public void setTotalBids(Long totalBids) {
        this.totalBids = totalBids;
    }

    public Long getBidsToday() {
        return bidsToday;
    }

    public void setBidsToday(Long bidsToday) {
        this.bidsToday = bidsToday;
    }

    public Long getPropertiesViewedToday() {
        return propertiesViewedToday;
    }

    public void setPropertiesViewedToday(Long propertiesViewedToday) {
        this.propertiesViewedToday = propertiesViewedToday;
    }

    public Long getTotalPropertiesViewed() {
        return totalPropertiesViewed;
    }

    public void setTotalPropertiesViewed(Long totalPropertiesViewed) {
        this.totalPropertiesViewed = totalPropertiesViewed;
    }

    public Double getRevenueEstimate() {
        return revenueEstimate;
    }

    public void setRevenueEstimate(Double revenueEstimate) {
        this.revenueEstimate = revenueEstimate;
    }
}
