//package com.propsightai.Repository;
//
//import com.propsightai.Model.Payment;
//import com.propsightai.Model.User;
//import com.propsightai.Role.PaymentMethod;
//import com.propsightai.Role.PaymentStatus;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//    // Find payment by order ID
//    Optional<Payment> findByOrderId(String orderId);
//
//    // Find payment by transaction ID
//    Optional<Payment> findByTransactionId(String transactionId);
//
//    // Find payments by user
//    List<Payment> findByUserOrderByCreatedAtDesc(User user);
//
//    Page<Payment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
//
//    // Find payments by status
//    List<Payment> findByStatus(PaymentStatus status);
//
//    // Find successful payments by user
//    List<Payment> findByUserAndStatusOrderByCreatedAtDesc(User user, PaymentStatus status);
//
//    // Count payments by method
//    long countByPaymentMethod(PaymentMethod method);
//
//    // Count payments by status
//    long countByStatus(PaymentStatus status);
//
//    // Sum revenue
//    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
//    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
//
//    // Find pending payments
//    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime dateTime);
//
//    // Find successful payments in date range
//    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.verifiedAt >= :startDate AND p.verifiedAt <= :endDate")
//    List<Payment> findSuccessfulPaymentsInDateRange(
//            @Param("status") PaymentStatus status,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );
//}
