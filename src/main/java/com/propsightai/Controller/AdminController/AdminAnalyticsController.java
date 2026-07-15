package com.propsightai.Controller.AdminController;

import com.propsightai.Dto.AuctionAnalytics;
import com.propsightai.Dto.PropertyAnalytics;
import com.propsightai.Dto.SystemStats;
import com.propsightai.Dto.UserAnalytics;
import com.propsightai.Service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')") // Role-based security boundary handshake enforced at class level
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Returns high-level operational statistics (Total Users, DAU, Total Properties, Estimated Revenue)
     * primarily used for the main Admin KPI dashboard grid.
     */
    @GetMapping("/system-stats")
    public ResponseEntity<SystemStats> getSystemStats() {
        log.info("Admin Request: Fetching global system stat metrics grid.");
        return ResponseEntity.ok(analyticsService.getSystemStats());
    }

    /**
     * Returns properties sorted by popularity metrics (views/interactions) across a dynamic timeline window.
     */
    @GetMapping("/most-viewed")
    public ResponseEntity<List<PropertyAnalytics>> getMostViewedProperties(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "days", defaultValue = "30") Integer days) {

        log.info("Admin Request: Generating property view heat maps up to limit: {}, window: {} days", limit, days);
        return ResponseEntity.ok(analyticsService.getMostViewedProperties(limit, days));
    }

    /**
     * Returns the most active live or concluded auctions sorted by total bid interaction counts.
     */
    @GetMapping("/most-active-auctions")
    public ResponseEntity<List<AuctionAnalytics>> getMostActiveAuctions(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        log.info("Admin Request: Retrieving highest transactional volume auction listings up to limit: {}", limit);
        return ResponseEntity.ok(analyticsService.getMostActiveAuctions(limit));
    }

    /**
     * Returns platform power users ranked by transaction counts and financial turnover volume.
     */
    @GetMapping("/top-bidders")
    public ResponseEntity<List<UserAnalytics>> getTopBidders(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        log.info("Admin Request: Pulling investor profile billing leaderboards up to limit: {}", limit);
        return ResponseEntity.ok(analyticsService.getTopBidders(limit));
    }

    /**
     * Get isolated daily engagement metrics.
     */
    @GetMapping("/daily-metrics")
    public ResponseEntity<Map<String, Object>> getDailyMetrics() {
        log.info("Admin Request: Gathering dynamic real-time daily metrics.");

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("daily_active_users", analyticsService.getDailyActiveUsers());
        metrics.put("bids_today", analyticsService.getTodayBidCount());
        metrics.put("views_today", analyticsService.getDailyViewCount());

        return ResponseEntity.ok(metrics);
    }

    /**
     * Get comprehensive dashboard data payload optimized to feed complete front-end dashboard panels
     * in a single client network request.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        log.info("Admin Request: Compiling comprehensive data package.");

        Map<String, Object> dashboard = new HashMap<>();

        // 1. Structural KPIs
        dashboard.put("system_stats", analyticsService.getSystemStats());

        // 2. Daily Snapshots
        Map<String, Object> dailyMetrics = new HashMap<>();
        dailyMetrics.put("active_users", analyticsService.getDailyActiveUsers());
        dailyMetrics.put("bids", analyticsService.getTodayBidCount());
        dailyMetrics.put("views", analyticsService.getDailyViewCount());
        dashboard.put("daily_metrics", dailyMetrics);

        // 3. Leaderboards / Top Performers (Slices restricted to 5 items for payload efficiency)
        dashboard.put("most_viewed_properties", analyticsService.getMostViewedProperties(5, 30));
        dashboard.put("most_active_auctions", analyticsService.getMostActiveAuctions(5));
        dashboard.put("top_bidders", analyticsService.getTopBidders(5));

        return ResponseEntity.ok(dashboard);
    }
}