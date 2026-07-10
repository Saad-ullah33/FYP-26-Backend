package com.propsightai.Service;

import com.propsightai.Dto.AuctionAnalytics;
import com.propsightai.Dto.PropertyAnalytics;
import com.propsightai.Dto.SystemStats;
import com.propsightai.Dto.UserAnalytics;
import com.propsightai.Model.Auction;
import com.propsightai.Model.Property;
import com.propsightai.Model.User;
import com.propsightai.Repository.*;
import com.propsightai.Role.ActivityEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public SystemStats getSystemStats() {
        logger.info("Fetching system statistics");

        try {
            SystemStats stats = new SystemStats();

            // User stats
            long totalUsers = userRepository.count();
            stats.setTotalUsers(totalUsers);

            // Daily active users
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = today.withHour(23).withMinute(59).withSecond(59);
            long activeUsersToday = activityRepository.countUniqueUsersInDateRange(startOfDay, endOfDay);
            stats.setActiveUsersToday(activeUsersToday);

            // Property stats
            long totalProperties = propertyRepository.count();
            stats.setTotalProperties(totalProperties);

            // Auction stats
            long activeAuctions = auctionRepository.countByStatusActive();
            stats.setActiveAuctions(activeAuctions);

            // Bid stats
            long totalBids = bidRepository.count();
            stats.setTotalBids(totalBids);

            long bidsToday = activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.BID_PLACED,
                    startOfDay,
                    endOfDay
            );
            stats.setBidsToday(bidsToday);

            // View stats
            long viewsToday = activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.PROPERTY_VIEW,
                    startOfDay,
                    endOfDay
            );
            stats.setPropertiesViewedToday(viewsToday);

            long totalViews = activityRepository.countByEventType(ActivityEventType.PROPERTY_VIEW);
            stats.setTotalPropertiesViewed(totalViews);

            // Revenue estimate (based on bids - simple calculation)
            Double estimatedRevenue = bidRepository.findAll().stream()
                    .filter(b -> b.getAmount() != null)
                    .mapToDouble(b -> b.getAmount().doubleValue())
                    .sum() * 0.05; // Assume 5% platform fee
            stats.setRevenueEstimate(estimatedRevenue);

            logger.info("System stats retrieved: {} users, {} properties, {} active auctions",
                    totalUsers, totalProperties, activeAuctions);
            return stats;

        } catch (Exception e) {
            logger.error("Error fetching system statistics", e);
            return new SystemStats(); // Return empty stats on error
        }
    }

    @Override
    public List<PropertyAnalytics> getMostViewedProperties(Integer limit, Integer days) {
        logger.info("Fetching most viewed properties (limit: {}, days: {})", limit, days);

        int viewLimit = limit != null && limit > 0 ? limit : 10;
        int dayRange = days != null && days > 0 ? days : 30;

        try {
            LocalDateTime sinceDate = LocalDateTime.now().minusDays(dayRange);

            List<Object[]> viewData = activityRepository.getMostViewedProperties(
                    ActivityEventType.PROPERTY_VIEW,
                    sinceDate
            );

            List<PropertyAnalytics> analytics = new ArrayList<>();

            for (Object[] row : viewData) {
                Integer propertyId = ((Number) row[0]).intValue();
                Long viewCount = ((Number) row[1]).longValue();

                Property property = propertyRepository.findById(propertyId).orElse(null);
                if (property != null) {
                    PropertyAnalytics pa = new PropertyAnalytics();
                    pa.setPropertyId(propertyId);
                    pa.setPropertyTitle(property.getTitle());
                    pa.setViewCount(viewCount);
                    
                    // Implement click tracking - count interactions/clicks from activity events
                    long clickCount = activityRepository.findAll().stream()
                            .filter(activity -> activity.getPropertyId() != null && 
                                    activity.getPropertyId().equals(propertyId) &&
                                    activity.getEventType() != null &&
                                    activity.getEventType().toString().equalsIgnoreCase("CLICK"))
                            .count();
                    pa.setClickCount(clickCount);
                    
                    pa.setBidCount(bidRepository.countByAuctionPropertyId(propertyId));
                    analytics.add(pa);
                }

                if (analytics.size() >= viewLimit) break;
            }

            logger.info("Found {} most viewed properties", analytics.size());
            return analytics;

        } catch (Exception e) {
            logger.error("Error fetching most viewed properties", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<AuctionAnalytics> getMostActiveAuctions(Integer limit) {
        logger.info("Fetching most active auctions");

        int auctionLimit = limit != null && limit > 0 ? limit : 10;

        try {
            List<Auction> auctions = auctionRepository.findAll().stream()
                    .sorted((a1, a2) -> {
                        long bids1 = bidRepository.countByAuctionId(a1.getId());
                        long bids2 = bidRepository.countByAuctionId(a2.getId());
                        return Long.compare(bids2, bids1); // Descending order
                    })
                    .limit(auctionLimit)
                    .collect(Collectors.toList());

            List<AuctionAnalytics> analytics = new ArrayList<>();

            for (Auction auction : auctions) {
                AuctionAnalytics aa = new AuctionAnalytics();
                aa.setAuctionId(auction.getId());
                aa.setPropertyId(auction.getProperty() != null ? auction.getProperty().getId() : null);
                aa.setPropertyTitle(auction.getProperty() != null ? auction.getProperty().getTitle() : "Unknown");
                long bidCount = bidRepository.countByAuctionId(auction.getId());
                aa.setBidCount((int) bidCount);
                aa.setCurrentHighestBid(auction.getCurrentHighestBid() != null ?
                        auction.getCurrentHighestBid().doubleValue() : 0.0);
                aa.setStatus(auction.getStatus() != null ? auction.getStatus().toString() : "UNKNOWN");

                // Count distinct bidders
                Long bidderCount = bidRepository.countDistinctBiddersByAuctionId(auction.getId());
                aa.setBidderCount(bidderCount != null ? bidderCount.intValue() : 0);

                analytics.add(aa);
            }

            logger.info("Found {} most active auctions", analytics.size());
            return analytics;

        } catch (Exception e) {
            logger.error("Error fetching most active auctions", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserAnalytics> getTopBidders(Integer limit) {
        logger.info("Fetching top bidders");

        int bidderLimit = limit != null && limit > 0 ? limit : 10;

        try {
            List<User> users = userRepository.findAll().stream()
                    .map(user -> new AbstractMap.SimpleEntry<>(
                            user,
                            bidRepository.countByBidder_Id(user.getId())
                    ))
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(bidderLimit)
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());

            List<UserAnalytics> analytics = new ArrayList<>();

            for (User user : users) {
                UserAnalytics ua = new UserAnalytics();
                ua.setUserId(user.getId());
                ua.setUserName(user.getName());
                ua.setUserEmail(user.getEmail());
                Integer totalBids = bidRepository.countByBidder_Id(user.getId());
                ua.setTotalBids((int) totalBids);
                
                // Count successful bids (where user won the auction)
                long successfulBids = bidRepository.findAll().stream()
                        .filter(bid -> bid.getBidder().getId().equals(user.getId()) && 
                                bid.getAuction().getWinner() != null &&
                                bid.getAuction().getWinner().getId().equals(user.getId()))
                        .count();
                ua.setSuccessfulBids((int) successfulBids);
                
                // Calculate total amount spent
                Double totalSpent = bidRepository.findAll().stream()
                        .filter(bid -> bid.getBidder().getId().equals(user.getId()))
                        .mapToDouble(bid -> bid.getAmount() != null ? bid.getAmount().doubleValue() : 0.0)
                        .sum();
                ua.setTotalSpent(totalSpent);

                analytics.add(ua);
            }

            logger.info("Found {} top bidders", analytics.size());
            return analytics;

        } catch (Exception e) {
            logger.error("Error fetching top bidders", e);
            return new ArrayList<>();
        }
    }

    @Override
    public long getDailyActiveUsers() {
        logger.info("Fetching daily active users");

        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = today.withHour(23).withMinute(59).withSecond(59);

            return activityRepository.countUniqueUsersInDateRange(startOfDay, endOfDay);
        } catch (Exception e) {
            logger.error("Error fetching daily active users", e);
            return 0;
        }
    }

    @Override
    public long getTodayBidCount() {
        logger.info("Fetching today's bid count");

        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = today.withHour(23).withMinute(59).withSecond(59);

            return activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.BID_PLACED,
                    startOfDay,
                    endOfDay
            );
        } catch (Exception e) {
            logger.error("Error fetching today's bid count", e);
            return 0;
        }
    }

    @Override
    public long getDailyViewCount() {
        logger.info("Fetching daily view count");

        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = today.withHour(23).withMinute(59).withSecond(59);

            return activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.PROPERTY_VIEW,
                    startOfDay,
                    endOfDay
            );
        } catch (Exception e) {
            logger.error("Error fetching daily view count", e);
            return 0;
        }
    }
}
