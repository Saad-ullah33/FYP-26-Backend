package com.propsightai.Repository;

import com.propsightai.Model.ActivityEvent;
import com.propsightai.Role.ActivityEventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEvent, Integer> {

    // ---------------- BASE CRUDS & USER METRICS ----------------

    long countByUserId(Integer userId);

    long countByPropertyIdAndEventType(Integer propertyId, ActivityEventType eventType);

    List<ActivityEvent> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<ActivityEvent> findByPropertyIdOrderByCreatedAtDesc(Integer propertyId);

    List<ActivityEvent> findByEventTypeOrderByCreatedAtDesc(ActivityEventType eventType);

    long countByEventType(ActivityEventType eventType);


    // ---------------- OPTIMIZED SYSTEM ANALYTICS & TIME BOUNDARIES ----------------

    /**
     * Aggregates property views within a specific timeline.
     * Uses open-ended upper boundaries ('< end') to prevent millisecond trimming issues.
     */
    /**
     * Aggregates property views within a specific timeline.
     * Uses open-ended upper boundaries ('< end') to prevent millisecond trimming issues.
     */
    @Query("SELECT COUNT(ae.id) FROM ActivityEvent ae " + // 👈 FIXED: Removed "ae.propertyId,"
            "WHERE ae.eventType = :type AND ae.createdAt >= :start AND ae.createdAt < :end")
    long countEventsByTypeInDateRange(
            @Param("type") ActivityEventType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Tracks unique daily active users (DAU) securely over a given date range.
     */
    @Query("SELECT COUNT(DISTINCT ae.userId) FROM ActivityEvent ae " +
            "WHERE ae.createdAt >= :start AND ae.createdAt < :end")
    long countUniqueUsersInDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Returns a summary breakdown of an individual user's total activities by action type.
     */
    @Query("SELECT ae.eventType, COUNT(ae.id) FROM ActivityEvent ae " +
            "WHERE ae.userId = :userId AND ae.createdAt >= :start AND ae.createdAt < :end " +
            "GROUP BY ae.eventType")
    List<Object[]> getUserActivitySummary(
            @Param("userId") Integer userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    // ---------------- PERFORMANCE-TUNED PAGINATED LOOKUPS ----------------

    /**
     * Fetch top viewed properties leveraging database-level indexing and pagination limits.
     */
    @Query("SELECT ae.propertyId, COUNT(ae.id) FROM ActivityEvent ae " +
            "WHERE ae.eventType = :type AND ae.createdAt >= :since " +
            "GROUP BY ae.propertyId " +
            "ORDER BY COUNT(ae.id) DESC")
    List<Object[]> getMostViewedProperties(
            @Param("type") ActivityEventType type,
            @Param("since") LocalDateTime since,
            Pageable pageable
    );

    /**
     * Counts granular interactions (like specific text string matches or clicks) per property.
     */
    @Query("SELECT COUNT(ae.id) FROM ActivityEvent ae " +
            "WHERE ae.propertyId = :propertyId AND ae.eventType = :clickType")
    long countClicksForProperty(
            @Param("propertyId") Integer propertyId,
            @Param("clickType") ActivityEventType clickType
    );
}