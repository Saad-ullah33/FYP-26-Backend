package com.propsightai.Service;

import com.propsightai.Dto.AuctionAnalytics;
import com.propsightai.Dto.PropertyAnalytics;
import com.propsightai.Dto.SystemStats;
import com.propsightai.Dto.UserAnalytics;

import java.util.List;

public interface AnalyticsService {

    /**
     * Get system-wide statistics.
     *
     * @return system stats (users, auctions, bids, etc.)
     */
    SystemStats getSystemStats();

    /**
     * Get most viewed properties.
     *
     * @param limit maximum number of results
     * @param days filter by last N days
     * @return list of properties with view counts
     */
    List<PropertyAnalytics> getMostViewedProperties(Integer limit, Integer days);

    /**
     * Get most active auctions.
     *
     * @param limit maximum number of results
     * @return list of auctions with bid counts
     */
    List<AuctionAnalytics> getMostActiveAuctions(Integer limit);

    /**
     * Get top bidders.
     *
     * @param limit maximum number of results
     * @return list of users with bid statistics
     */
    List<UserAnalytics> getTopBidders(Integer limit);

    /**
     * Get daily active users count.
     *
     * @return count of unique users active today
     */
    long getDailyActiveUsers();

    /**
     * Get total bids placed today.
     *
     * @return count of bids placed in last 24 hours
     */
    long getTodayBidCount();

    /**
     * Get daily views count.
     *
     * @return count of property views in last 24 hours
     */
    long getDailyViewCount();
}
