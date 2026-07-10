//package com.propsightai.Controller;
//
//import com.propsightai.Dto.InitiatePaymentRequest;
//import com.propsightai.Dto.PaymentHistoryResponse;
//import com.propsightai.Model.Payment;
//import com.propsightai.Model.User;
//import com.propsightai.Repository.UserRepository;
//import com.propsightai.Role.SubscriptionPlan;
//import com.propsightai.Service.PaymentService;
//import com.propsightai.security.CustomUserDetails;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/payment")
//@CrossOrigin(origins = "*")
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * Initiate payment for subscription upgrade.
//     * POST /api/payment/initiate
//     */
//    @PostMapping("/initiate")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> initiatePayment(
//            @Valid @RequestBody InitiatePaymentRequest request,
//            Authentication authentication
//    ) {
//        log.info("Initiating payment for plan: {}, method: {}", request.getPlan(), request.getPaymentMethod());
//
//        try {
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            User user = userDetails.getUser();
//
//            // Initiate payment
//            Payment payment = paymentService.initiatePayment(user, request.getPlan(), request.getPaymentMethod());
//
//            // Build response with payment instructions
//            Map<String, Object> response = new HashMap<>();
//            response.put("order_id", payment.getOrderId());
//            response.put("amount", payment.getAmount());
//            response.put("currency", payment.getCurrency());
//            response.put("method", payment.getPaymentMethod());
//            response.put("status", payment.getStatus());
//            response.put("description", payment.getDescription());
//
//            // Add method-specific instructions
//            switch (request.getPaymentMethod()) {
//                case JAZZCASH:
//                    response.put("instructions", "Send payment to JazzCash account. Reference: " + payment.getOrderId());
//                    response.put("code", "*141#");
//                    break;
//                case EASYPAISA:
//                    response.put("instructions", "Send payment via EasyPaisa. Reference: " + payment.getOrderId());
//                    response.put("code", "*786#");
//                    break;
//                case BANK_TRANSFER:
//                    response.put("instructions", "Transfer to PropSightAI Bank Account. Reference: " + payment.getOrderId());
//                    break;
//                case DEBIT_CARD, CREDIT_CARD:
//                    response.put("instructions", "Enter your card details. Order ID: " + payment.getOrderId());
//                    break;
//            }
//
//            log.info("Payment initiated successfully: orderId={}", payment.getOrderId());
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error initiating payment", e);
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Verify payment (called after user completes payment).
//     * POST /api/payment/verify
//     */
//    @PostMapping("/verify")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> verifyPayment(
//            @RequestParam String orderId,
//            @RequestParam String transactionId,
//            @RequestParam SubscriptionPlan plan,
//            Authentication authentication
//    ) {
//        log.info("Verifying payment: orderId={}, transactionId={}, plan={}", orderId, transactionId, plan);
//
//        try {
//            Payment payment = paymentService.verifyAndProcessPayment(orderId, transactionId, plan);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "SUCCESS");
//            response.put("message", "Payment verified and subscription updated");
//            response.put("order_id", payment.getOrderId());
//            response.put("transaction_id", payment.getTransactionId());
//            response.put("plan", plan);
//
//            log.info("Payment verified successfully: orderId={}", orderId);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error verifying payment", e);
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Get payment history for authenticated user.
//     * GET /api/payment/history
//     */
//    @GetMapping("/history")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> getPaymentHistory(
//            Authentication authentication,
//            Pageable pageable
//    ) {
//        log.info("Fetching payment history for user: {}", authentication.getName());
//
//        try {
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            User user = userDetails.getUser();
//
//            Page<Payment> payments = paymentService.getPaymentHistory(user, pageable);
//
//            Page<PaymentHistoryResponse> response = payments.map(p -> {
//                PaymentHistoryResponse resp = new PaymentHistoryResponse();
//                resp.setPaymentId(p.getId());
//                resp.setOrderId(p.getOrderId());
//                resp.setAmount(p.getAmount());
//                resp.setCurrency(p.getCurrency());
//                resp.setMethod(p.getPaymentMethod());
//                resp.setStatus(p.getStatus());
//                resp.setDescription(p.getDescription());
//                resp.setCreatedAt(p.getCreatedAt());
//                resp.setVerifiedAt(p.getVerifiedAt());
//                return resp;
//            });
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error fetching payment history", e);
//            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Webhook endpoint for payment provider callbacks (mock implementation).
//     * POST /api/payment/callback
//     */
//    @PostMapping("/callback")
//    public ResponseEntity<?> handlePaymentCallback(
//            @RequestParam String orderId,
//            @RequestParam String transactionId,
//            @RequestParam String status
//    ) {
//        log.info("Received payment callback: orderId={}, transactionId={}, status={}", orderId, transactionId, status);
//
//        try {
//            if ("SUCCESS".equalsIgnoreCase(status)) {
//                // Payment successful - provider will call /verify endpoint
//                log.info("Payment callback success: orderId={}", orderId);
//                return ResponseEntity.ok(Map.of("status", "RECEIVED", "message", "Callback received successfully"));
//            } else {
//                // Payment failed
//                paymentService.markPaymentFailed(orderId, "Provider callback: " + status);
//                log.warn("Payment callback failure: orderId={}", orderId);
//                return ResponseEntity.ok(Map.of("status", "RECEIVED", "message", "Failure recorded"));
//            }
//
//        } catch (Exception e) {
//            log.error("Error handling payment callback", e);
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//}
