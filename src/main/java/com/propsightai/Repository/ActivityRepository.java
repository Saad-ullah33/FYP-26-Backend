package com.propsightai.Repository;

import com.propsightai.Model.ActivityEvent;
import com.propsightai.Role.ActivityEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEvent, Integer> {

    // Count user activities
    long countByUserId(Integer userId);

    // Count property views
    long countByPropertyIdAndEventType(Integer propertyId, ActivityEventType eventType);

    // Get user's recent activities
    List<ActivityEvent> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Get activities for a property
    List<ActivityEvent> findByPropertyIdOrderByCreatedAtDesc(Integer propertyId);

    // Find activities of specific type
    List<ActivityEvent> findByEventTypeOrderByCreatedAtDesc(ActivityEventType eventType);

    // Count events by type within date range
    @Query("SELECT COUNT(ae) FROM ActivityEvent ae WHERE ae.eventType = :eventType " +
           "AND ae.createdAt >= :startDate AND ae.createdAt <= :endDate")
    long countEventsByTypeInDateRange(
            @Param("eventType") ActivityEventType eventType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get most viewed properties (last N days)
    @Query("SELECT ae.propertyId, COUNT(ae) as viewCount " +
           "FROM ActivityEvent ae " +
           "WHERE ae.eventType = :eventType AND ae.createdAt >= :sinceDate " +
           "GROUP BY ae.propertyId " +
           "ORDER BY viewCount DESC")
    List<Object[]> getMostViewedProperties(
            @Param("eventType") ActivityEventType eventType,
            @Param("sinceDate") LocalDateTime sinceDate
    );

    // Get user activity summary for a date range
    @Query("SELECT ae.eventType, COUNT(ae) as count " +
           "FROM ActivityEvent ae " +
           "WHERE ae.userId = :userId AND ae.createdAt >= :startDate AND ae.createdAt <= :endDate " +
           "GROUP BY ae.eventType")
    List<Object[]> getUserActivitySummary(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Daily unique users count
    @Query("SELECT COUNT(DISTINCT ae.userId) FROM ActivityEvent ae " +
           "WHERE ae.createdAt >= :startDate AND ae.createdAt <= :endDate")
    long countUniqueUsersInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Count all events of a specific type
    long countByEventType(ActivityEventType eventType);
}
