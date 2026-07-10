//package com.propsightai.Controller.AdminController;
//
//import com.propsightai.Dto.SubscriptionResponse;
//import com.propsightai.Model.User;
//import com.propsightai.Repository.SubscriptionRepository;
//import com.propsightai.Repository.UserRepository;
//import com.propsightai.Role.SubscriptionPlan;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/admin/subscription")
//@CrossOrigin(origins = "*")
//public class AdminSubscriptionController {
//
//    @Autowired
//    private SubscriptionService subscriptionService;
//
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * Get subscription for specific user (admin).
//     * GET /api/admin/subscription/user/{userId}
//     */
//    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> getUserSubscription(@PathVariable Integer userId) {
//        log.info("Admin: Fetching subscription for user {}", userId);
//
//        try {
//            Optional<User> user = userRepository.findById(userId);
//            if (user.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            Optional<Subscription> subscription = subscriptionService.getSubscription(userId);
//            if (subscription.isPresent()) {
//                SubscriptionResponse response = mapToResponse(subscription.get(), userId);
//                return ResponseEntity.ok(response);
//            }
//
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            log.error("Error fetching user subscription", e);
//            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Upgrade user's subscription (admin override).
//     * POST /api/admin/subscription/user/{userId}/upgrade
//     */
//    @PostMapping("/user/{userId}/upgrade")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> upgradeUserSubscription(
//            @PathVariable Integer userId,
//            @RequestParam SubscriptionPlan newPlan
//    ) {
//        log.info("Admin: Upgrading subscription for user {} to plan {}", userId, newPlan);
//
//        try {
//            Optional<User> user = userRepository.findById(userId);
//            if (user.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            Subscription subscription = subscriptionService.upgradePlan(userId, newPlan);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "SUCCESS");
//            response.put("message", "User subscription upgraded to " + newPlan);
//            response.put("new_plan", newPlan);
//            response.put("user_id", userId);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error upgrading user subscription", e);
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Get all subscriptions (admin).
//     * GET /api/admin/subscription/all
//     */
//    @GetMapping("/all")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> getAllSubscriptions(Pageable pageable) {
//        log.info("Admin: Fetching all subscriptions");
//
//        try {
//            Page<Subscription> subscriptions = subscriptionRepository.findAll(pageable);
//
//            Page<Map<String, Object>> response = subscriptions.map(sub -> {
//                Map<String, Object> map = new HashMap<>();
//                map.put("subscription_id", sub.getId());
//                map.put("user_id", sub.getUser().getId());
//                map.put("user_email", sub.getUser().getEmail());
//                map.put("plan", sub.getPlan());
//                map.put("start_date", sub.getStartDate());
//                map.put("end_date", sub.getEndDate());
//                map.put("is_active", sub.getActive());
//                map.put("quota_used", sub.getMonthlyPropertyQuotaUsed());
//                return map;
//            });
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error fetching all subscriptions", e);
//            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Get subscription statistics (admin dashboard).
//     * GET /api/admin/subscription/stats
//     */
//    @GetMapping("/stats")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> getSubscriptionStats() {
//        log.info("Admin: Fetching subscription statistics");
//
//        try {
//            Long freeCount = subscriptionRepository.countByPlan(SubscriptionPlan.FREE);
//            Long basicCount = subscriptionRepository.countByPlan(SubscriptionPlan.BASIC);
//            Long premiumCount = subscriptionRepository.countByPlan(SubscriptionPlan.PREMIUM);
//
//            Long activeCount = subscriptionRepository.countByActive(true);
//            Long inactiveCount = subscriptionRepository.countByActive(false);
//
//            Map<String, Object> stats = new HashMap<>();
//            stats.put("total_subscriptions", freeCount + basicCount + premiumCount);
//            stats.put("free_users", freeCount);
//            stats.put("basic_users", basicCount);
//            stats.put("premium_users", premiumCount);
//            stats.put("active_subscriptions", activeCount);
//            stats.put("inactive_subscriptions", inactiveCount);
//            stats.put("revenue_potential_pkr", (basicCount * 500) + (premiumCount * 2000));
//
//            return ResponseEntity.ok(stats);
//
//        } catch (Exception e) {
//            log.error("Error fetching subscription stats", e);
//            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Get subscriptions by plan (admin).
//     * GET /api/admin/subscription/by-plan/{plan}
//     */
//    @GetMapping("/by-plan/{plan}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> getSubscriptionsByPlan(
//            @PathVariable SubscriptionPlan plan,
//            Pageable pageable
//    ) {
//        log.info("Admin: Fetching subscriptions for plan {}", plan);
//
//        try {
//            Page<Subscription> subscriptions = subscriptionRepository.findByPlan(plan, pageable);
//
//            Page<Map<String, Object>> response = subscriptions.map(sub -> {
//                Map<String, Object> map = new HashMap<>();
//                map.put("user_id", sub.getUser().getId());
//                map.put("user_email", sub.getUser().getEmail());
//                map.put("start_date", sub.getStartDate());
//                map.put("is_active", sub.getActive());
//                return map;
//            });
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error fetching subscriptions by plan", e);
//            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Map Subscription entity to response DTO.
//     */
//    private SubscriptionResponse mapToResponse(Subscription subscription, Integer userId) {
//        SubscriptionResponse response = new SubscriptionResponse();
//        response.setPlan(subscription.getPlan());
//        response.setStartDate(subscription.getStartDate());
//        response.setEndDate(subscription.getEndDate());
//        response.setIsActive(subscription.getActive());
//        response.setMonthlyPropertyQuotaUsed(subscription.getMonthlyPropertyQuotaUsed());
//        response.setMonthlyQuotaRemaining(subscriptionService.getPropertiesRemainingInQuota(userId));
//        response.setMonthlyPriceInPKR(subscription.getPlan().getMonthlyPriceInPKR());
//        response.setDescription(subscription.getPlan().getDescription());
//        return response;
//    }
//}
