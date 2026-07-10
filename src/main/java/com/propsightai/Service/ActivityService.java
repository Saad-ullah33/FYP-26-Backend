package com.propsightai.Service;

import com.propsightai.Model.ActivityEvent;
import com.propsightai.Repository.ActivityRepository;
import com.propsightai.Role.ActivityEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Log an activity event (asynchronous to avoid blocking requests).
     *
     * @param userId the user ID
     * @param propertyId the property ID (optional)
     * @param eventType the type of event
     */
    @Async
    public void logActivity(Integer userId, Integer propertyId, ActivityEventType eventType) {
        logActivity(userId, propertyId, eventType, null);
    }

    /**
     * Log an activity event with metadata.
     *
     * @param userId the user ID
     * @param propertyId the property ID (optional)
     * @param eventType the type of event
     * @param metadata optional metadata (e.g., JSON string)
     */
    @Async
    public void logActivity(Integer userId, Integer propertyId, ActivityEventType eventType, String metadata) {
        try {
            ActivityEvent event = new ActivityEvent(userId, propertyId, eventType, metadata);
            activityRepository.save(event);
            logger.debug("Activity logged: userId={}, propertyId={}, eventType={}", userId, propertyId, eventType);
        } catch (Exception e) {
            // Don't fail the main request if activity logging fails
            logger.warn("Failed to log activity event: {}", e.getMessage(), e);
        }
    }

    /**
     * Get view count for a property.
     *
     * @param propertyId the property ID
     * @return count of VIEW events for this property
     */
    public long getPropertyViewCount(Integer propertyId) {
        return activityRepository.countByPropertyIdAndEventType(propertyId, ActivityEventType.PROPERTY_VIEW);
    }

    /**
     * Get bid count for a property.
     *
     * @param propertyId the property ID
     * @return count of BID_PLACED events for this property
     */
    public long getPropertyBidCount(Integer propertyId) {
        return activityRepository.countByPropertyIdAndEventType(propertyId, ActivityEventType.BID_PLACED);
    }

    /**
     * Get user activity count.
     *
     * @param userId the user ID
     * @return total activity events for this user
     */
    public long getUserActivityCount(Integer userId) {
        return activityRepository.countByUserId(userId);
    }
}
