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
import com.propsightai.Role.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ActivityRepository activityRepository;

    private static final double PLATFORM_FEE_MULTIPLIER = 0.05;

    @Override
    public SystemStats getSystemStats() {
        log.info("Fetching real-time global system statistics matrix.");
        try {
            SystemStats stats = new SystemStats();
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime tomorrowStart = startOfDay.plusDays(1);

            stats.setTotalUsers(userRepository.count());
            stats.setActiveUsersToday(activityRepository.countUniqueUsersInDateRange(startOfDay, tomorrowStart));
            stats.setTotalProperties(propertyRepository.count());
            stats.setActiveAuctions(auctionRepository.countByStatusActive());
            stats.setTotalBids(bidRepository.count());

            stats.setBidsToday(activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.BID_PLACED, startOfDay, tomorrowStart));
            stats.setPropertiesViewedToday(activityRepository.countEventsByTypeInDateRange(
                    ActivityEventType.PROPERTY_VIEW, startOfDay, tomorrowStart));
            stats.setTotalPropertiesViewed(activityRepository.countByEventType(ActivityEventType.PROPERTY_VIEW));

            Double totalBidVolume = bidRepository.sumAllBidAmounts();
            stats.setRevenueEstimate(totalBidVolume != null ? totalBidVolume * PLATFORM_FEE_MULTIPLIER : 0.0);

            return stats;
        } catch (Exception e) {
            log.error("Fatal exception during system analytics calculation batch", e);
            return new SystemStats();
        }
    }

    @Override
    public List<PropertyAnalytics> getMostViewedProperties(Integer limit, Integer days) {
        int viewLimit = (limit != null && limit > 0) ? limit : 10;
        int dayRange = (days != null && days > 0) ? days : 30;
        log.info("Fetching top viewed properties metadata. Limit: {}, Window: {} days", viewLimit, dayRange);

        try {
            LocalDateTime sinceDate = LocalDateTime.now().minusDays(dayRange);
            List<Object[]> viewData = activityRepository.getMostViewedProperties(
                    ActivityEventType.PROPERTY_VIEW, sinceDate, PageRequest.of(0, viewLimit));

            if (viewData.isEmpty()) return Collections.emptyList();

            // Extract IDs for Batch In-Clause Execution
            List<Integer> propertyIds = viewData.stream().map(row -> ((Number) row[0]).intValue()).toList();

            // Batch look up all required properties in 1 database trip
            Map<Integer, Property> propertyMap = propertyRepository.findAllById(propertyIds).stream()
                    .collect(Collectors.toMap(Property::getId, Function.identity()));

            List<PropertyAnalytics> analytics = new ArrayList<>();
            for (Object[] row : viewData) {
                Integer propertyId = ((Number) row[0]).intValue();
                Long viewCount = ((Number) row[1]).longValue();

                Property property = propertyMap.get(propertyId);
                if (property != null) {
                    PropertyAnalytics pa = new PropertyAnalytics();
                    pa.setPropertyId(propertyId);
                    pa.setPropertyTitle(property.getTitle());
                    pa.setViewCount(viewCount);

                    // Scalar metrics remain fast, but entities are fully memory-cached now
                    pa  .setClickCount(activityRepository.countClicksForProperty(propertyId, ActivityEventType.PROPERTY_CLICK));
                    pa.setBidCount((long) bidRepository.countByAuctionPropertyId(propertyId));
                    analytics.add(pa);
                }
            }
            return analytics;
        } catch (Exception e) {
            log.error("Error building property analytics profile", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<AuctionAnalytics> getMostActiveAuctions(Integer limit) {
        int auctionLimit = (limit != null && limit > 0) ? limit : 10;
        log.info("Compiling most active auctions up to threshold limit: {}", auctionLimit);

        try {
            List<Object[]> activeAuctionData = bidRepository.findAuctionsOrderedByBidCount(PageRequest.of(0, auctionLimit));
            if (activeAuctionData.isEmpty()) return Collections.emptyList();

            // Extract IDs for Batch In-Clause execution
            List<Integer> auctionIds = activeAuctionData.stream().map(row -> ((Number) row[0]).intValue()).toList();

            // Batch fetch all auction entities with 1 single query assignment
            Map<Integer, Auction> auctionMap = auctionRepository.findAllById(auctionIds).stream()
                    .collect(Collectors.toMap(Auction::getId, Function.identity()));

            List<AuctionAnalytics> analytics = new ArrayList<>();
            for (Object[] row : activeAuctionData) {
                Integer auctionId = ((Number) row[0]).intValue();
                Long bidCount = ((Number) row[1]).longValue();

                Auction auction = auctionMap.get(auctionId);
                if (auction != null) {
                    AuctionAnalytics aa = new AuctionAnalytics();
                    aa.setAuctionId(auctionId);
                    aa.setPropertyId(auction.getProperty() != null ? auction.getProperty().getId() : null);
                    aa.setPropertyTitle(auction.getProperty() != null ? auction.getProperty().getTitle() : "Unknown");
                    aa.setBidCount(bidCount.intValue());
                    aa.setCurrentHighestBid(auction.getCurrentHighestBid() != null ? auction.getCurrentHighestBid().doubleValue() : 0.0);
                    aa.setStatus(auction.getStatus() != null ? auction.getStatus().name() : "UNKNOWN");

                    Long bidderCount = bidRepository.countDistinctBiddersByAuctionId(auctionId);
                    aa.setBidderCount(bidderCount != null ? bidderCount.intValue() : 0);

                    analytics.add(aa);
                }
            }
            return analytics;
        } catch (Exception e) {
            log.error("Error generating auction data insights stream", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserAnalytics> getTopBidders(Integer limit) {
        int bidderLimit = (limit != null && limit > 0) ? limit : 10;
        log.info("Aggregating system top platform bidders profile summary. Limit: {}", bidderLimit);

        try {
            List<Object[]> topBiddersData = bidRepository.findTopBiddersByBidCount(PageRequest.of(0, bidderLimit));
            if (topBiddersData.isEmpty()) return Collections.emptyList();

            // Extract User IDs for Batch Extraction
            List<Integer> userIds = topBiddersData.stream().map(row -> ((Number) row[0]).intValue()).toList();

            // Batch user profile initialization mapping
            Map<Integer, User> userMap = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));

            List<UserAnalytics> analytics = new ArrayList<>();
            for (Object[] row : topBiddersData) {
                Integer userId = ((Number) row[0]).intValue();
                Long totalBids = ((Number) row[1]).longValue();

                User user = userMap.get(userId);
                if (user != null) {
                    UserAnalytics ua = new UserAnalytics();
                    ua.setUserId(userId);
                    ua.setUserName(user.getName());
                    ua.setUserEmail(user.getEmail());
                    ua.setTotalBids(totalBids.intValue());

                    ua.setSuccessfulBids(bidRepository.countSuccessfulBidsByBidder(userId));
                    ua.setTotalSpent(bidRepository.sumTotalAmountSpentByBidder(userId));

                    analytics.add(ua);
                }
            }
            return analytics;
        } catch (Exception e) {
            log.error("Error mapping user dashboard metrics", e);
            return new ArrayList<>();
        }
    }

    @Override
    public long getDailyActiveUsers() {
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            return activityRepository.countUniqueUsersInDateRange(startOfDay, startOfDay.plusDays(1));
        } catch (Exception e) {
            log.error("Error calculating daily active user count matrix", e);
            return 0;
        }
    }

    @Override
    public long getTodayBidCount() {
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            return activityRepository.countEventsByTypeInDateRange(ActivityEventType.BID_PLACED, startOfDay, startOfDay.plusDays(1));
        } catch (Exception e) {
            log.error("Error calculating daily transaction logs", e);
            return 0;
        }
    }

    @Override
    public long getDailyViewCount() {
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            return activityRepository.countEventsByTypeInDateRange(ActivityEventType.PROPERTY_VIEW, startOfDay, startOfDay.plusDays(1));
        } catch (Exception e) {
            log.error("Error tracking view counters", e);
            return 0;
        }
    }
}