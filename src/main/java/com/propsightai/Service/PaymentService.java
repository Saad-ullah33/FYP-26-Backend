//package com.propsightai.Service;
//
//import com.propsightai.Model.Payment;
//import com.propsightai.Model.User;
//import com.propsightai.Repository.PaymentRepository;
//import com.propsightai.Repository.SubscriptionRepository;
//import com.propsightai.Role.PaymentMethod;
//import com.propsightai.Role.PaymentStatus;
//import com.propsightai.Role.SubscriptionPlan;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@Transactional
//public class PaymentService {
//
//    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private SubscriptionRepository subscriptionRepository;
//
//    @Autowired
//    private SubscriptionService subscriptionService;
//
//    /**
//     * Initiate payment for subscription upgrade.
//     * Returns Payment object with orderId and instructions.
//     */
//    public Payment initiatePayment(User user, SubscriptionPlan newPlan, PaymentMethod method) {
//        logger.info("Initiating payment for user {} to upgrade to plan {}", user.getId(), newPlan);
//
//        if (newPlan == SubscriptionPlan.FREE) {
//            throw new RuntimeException("Payment not required for FREE tier");
//        }
//
//        int amountPKR = newPlan.getMonthlyPriceInPKR();
//        String orderId = generateOrderId();
//
//        Payment payment = new Payment(user, BigDecimal.valueOf(amountPKR), method, orderId);
//        payment.setDescription("Subscription upgrade to " + newPlan.getDisplayName());
//        payment.setStatus(PaymentStatus.PENDING);
//
//        Payment savedPayment = paymentRepository.save(payment);
//        logger.info("Payment initiated: orderId={}, amount={}, method={}", orderId, amountPKR, method);
//
//        return savedPayment;
//    }
//
//    /**
//     * Verify and process payment.
//     * This is called after payment provider confirms payment success.
//     */
//    public Payment verifyAndProcessPayment(String orderId, String transactionId, SubscriptionPlan plan) {
//        logger.info("Verifying payment: orderId={}, transactionId={}", orderId, transactionId);
//
//        Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Payment not found: " + orderId));
//
//        if (payment.getStatus() != PaymentStatus.PENDING) {
//            throw new RuntimeException("Payment already processed: " + orderId);
//        }
//
//        // Mark as successful
//        payment.setStatus(PaymentStatus.SUCCESS);
//        payment.setTransactionId(transactionId);
//        payment.setVerifiedAt(LocalDateTime.now());
//        Payment verifiedPayment = paymentRepository.save(payment);
//
//        // Update subscription
//        User user = payment.getUser();
//        Subscription subscription = subscriptionService.upgradePlan(user.getId(), plan);
//        payment.setSubscription(subscription);
//        paymentRepository.save(payment);
//
//        logger.info("Payment verified and subscription updated for user {}: {} -> {}", user.getId(), payment.getStatus(), plan);
//
//        return verifiedPayment;
//    }
//
//    /**
//     * Mark payment as failed.
//     */
//    public Payment markPaymentFailed(String orderId, String reason) {
//        logger.warn("Marking payment as failed: orderId={}, reason={}", orderId, reason);
//
//        Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Payment not found: " + orderId));
//
//        payment.setStatus(PaymentStatus.FAILED);
//        payment.setMetadata(reason != null ? reason : "Unknown failure");
//
//        return paymentRepository.save(payment);
//    }
//
//    /**
//     * Get payment history for user.
//     */
//    public Page<Payment> getPaymentHistory(User user, Pageable pageable) {
//        logger.debug("Fetching payment history for user {}", user.getId());
//        return paymentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
//    }
//
//    /**
//     * Get successful payments for user.
//     */
//    public java.util.List<Payment> getSuccessfulPayments(User user) {
//        return paymentRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PaymentStatus.SUCCESS);
//    }
//
//    /**
//     * Get payment by orderId.
//     */
//    public Optional<Payment> getPayment(String orderId) {
//        return paymentRepository.findByOrderId(orderId);
//    }
//
//    /**
//     * Get total revenue.
//     */
//    public BigDecimal getTotalRevenue() {
//        return paymentRepository.sumAmountByStatus(PaymentStatus.SUCCESS);
//    }
//
//    /**
//     * Generate unique orderId.
//     */
//    private String generateOrderId() {
//        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
//    }
//}
