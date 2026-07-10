package com.propsightai.Controller.AdminController;

import com.propsightai.Dto.AuctionAnalytics;
import com.propsightai.Dto.PropertyAnalytics;
import com.propsightai.Dto.SystemStats;
import com.propsightai.Dto.UserAnalytics;
import com.propsightai.Service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get system-wide statistics.
     * GET /api/admin/analytics/system-stats
     */
    @GetMapping("/system-stats")
    public ResponseEntity<SystemStats> getSystemStats() {
        log.info("Admin retrieving system statistics");
        SystemStats stats = analyticsService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get most viewed properties.
     * GET /api/admin/analytics/most-viewed?limit=10&days=30
     */
    @GetMapping("/most-viewed")
    public ResponseEntity<List<PropertyAnalytics>> getMostViewedProperties(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "30") Integer days
    ) {
        log.info("Admin retrieving most viewed properties (limit: {}, days: {})", limit, days);
        List<PropertyAnalytics> results = analyticsService.getMostViewedProperties(limit, days);
        return ResponseEntity.ok(results);
    }

    /**
     * Get most active auctions.
     * GET /api/admin/analytics/most-active-auctions?limit=10
     */
    @GetMapping("/most-active-auctions")
    public ResponseEntity<List<AuctionAnalytics>> getMostActiveAuctions(
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        log.info("Admin retrieving most active auctions (limit: {})", limit);
        List<AuctionAnalytics> results = analyticsService.getMostActiveAuctions(limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Get top bidders.
     * GET /api/admin/analytics/top-bidders?limit=10
     */
    @GetMapping("/top-bidders")
    public ResponseEntity<List<UserAnalytics>> getTopBidders(
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        log.info("Admin retrieving top bidders (limit: {})", limit);
        List<UserAnalytics> results = analyticsService.getTopBidders(limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Get daily engagement metrics.
     * GET /api/admin/analytics/daily-metrics
     */
    @GetMapping("/daily-metrics")
    public ResponseEntity<Map<String, Object>> getDailyMetrics() {
        log.info("Admin retrieving daily metrics");

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("daily_active_users", analyticsService.getDailyActiveUsers());
        metrics.put("bids_today", analyticsService.getTodayBidCount());
        metrics.put("views_today", analyticsService.getDailyViewCount());

        return ResponseEntity.ok(metrics);
    }

    /**
     * Get comprehensive dashboard data.
     * GET /api/admin/analytics/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        log.info("Admin retrieving dashboard data");

        Map<String, Object> dashboard = new HashMap<>();

        // System stats
        dashboard.put("system_stats", analyticsService.getSystemStats());

        // Daily metrics
        Map<String, Object> dailyMetrics = new HashMap<>();
        dailyMetrics.put("active_users", analyticsService.getDailyActiveUsers());
        dailyMetrics.put("bids", analyticsService.getTodayBidCount());
        dailyMetrics.put("views", analyticsService.getDailyViewCount());
        dashboard.put("daily_metrics", dailyMetrics);

        // Top performers
        dashboard.put("most_viewed_properties", analyticsService.getMostViewedProperties(5, 30));
        dashboard.put("most_active_auctions", analyticsService.getMostActiveAuctions(5));
        dashboard.put("top_bidders", analyticsService.getTopBidders(5));

        return ResponseEntity.ok(dashboard);
    }
}
