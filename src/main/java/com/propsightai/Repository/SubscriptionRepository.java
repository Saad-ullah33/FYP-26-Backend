//package com.propsightai.Repository;
//
//import com.propsightai.Model.User;
//import com.propsightai.Role.SubscriptionPlan;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
//
//    // Find subscription by user
//    Optional<Subscription> findByUser(User user);
//
//    Optional<Subscription> findByUserId(Integer userId);
//
//    // Find active subscriptions
//    List<Subscription> findByIsActiveTrueAndPlan(SubscriptionPlan plan);
//
//    // Find subscriptions expiring soon
//    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.endDate IS NOT NULL AND s.endDate BETWEEN :startDate AND :endDate")
//    List<Subscription> findExpiringSubscriptions(
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate
//    );
//
//    // Count subscriptions by plan
//    long countByPlan(SubscriptionPlan plan);
//
//    // Count active subscriptions
//    long countByIsActiveTrue();
//
//    // Find subscriptions with quota usage
//    @Query("SELECT s FROM Subscription s WHERE s.plan = :plan AND s.monthlyPropertyQuotaUsed >= 10")
//    List<Subscription> findBasicPlansWithFullQuota(@Param("plan") SubscriptionPlan plan);
//
//    Page<Subscription> findByPlan(SubscriptionPlan plan, Pageable pageable);
//
//    Long countByActive(boolean b);
//}
