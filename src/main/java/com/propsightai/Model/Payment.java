//package com.propsightai.Model;
//
//import com.propsightai.Role.PaymentMethod;
//import com.propsightai.Role.PaymentStatus;
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "payments", indexes = {
//    @Index(name = "idx_user_id", columnList = "user_id"),
//    @Index(name = "idx_status", columnList = "status"),
//    @Index(name = "idx_transaction_id", columnList = "transaction_id")
//})
//public class Payment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "PaymentID")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subscription_id")
//    private Subscription subscription; // Nullable if payment not yet linked to subscription
//
//    @Column(name = "amount", nullable = false)
//    private BigDecimal amount; // In PKR
//
//    @Column(name = "currency", nullable = false)
//    private String currency = "PKR";
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "payment_method", nullable = false)
//    private PaymentMethod paymentMethod;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private PaymentStatus status = PaymentStatus.PENDING;
//
//    @Column(name = "transaction_id")
//    private String transactionId; // ID from payment provider
//
//    @Column(name = "order_id", nullable = false, unique = true)
//    private String orderId; // Internal order ID
//
//    @Column(name = "description")
//    private String description; // e.g., "Premium Plan Upgrade for 1 month"
//
//    @Column(name = "metadata", columnDefinition = "JSON")
//    private String metadata; // JSON for additional data (provider response, etc.)
//
//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;
//
//    @Column(name = "verified_at")
//    private LocalDateTime verifiedAt; // When payment was verified
//
//    // Constructors
//    public Payment() {
//    }
//
//    public Payment(User user, BigDecimal amount, PaymentMethod method, String orderId) {
//        this.user = user;
//        this.amount = amount;
//        this.paymentMethod = method;
//        this.orderId = orderId;
//        this.status = PaymentStatus.PENDING;
//        this.currency = "PKR";
//    }
//
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Subscription getSubscription() {
//        return subscription;
//    }
//
//    public void setSubscription(Subscription subscription) {
//        this.subscription = subscription;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//    public PaymentMethod getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public void setPaymentMethod(PaymentMethod paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }
//
//    public PaymentStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(PaymentStatus status) {
//        this.status = status;
//    }
//
//    public String getTransactionId() {
//        return transactionId;
//    }
//
//    public void setTransactionId(String transactionId) {
//        this.transactionId = transactionId;
//    }
//
//    public String getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(String metadata) {
//        this.metadata = metadata;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public LocalDateTime getVerifiedAt() {
//        return verifiedAt;
//    }
//
//    public void setVerifiedAt(LocalDateTime verifiedAt) {
//        this.verifiedAt = verifiedAt;
//    }
//
//    @Override
//    public String toString() {
//        return "Payment{" +
//                "id=" + id +
//                ", user=" + (user != null ? user.getId() : null) +
//                ", amount=" + amount +
//                ", method=" + paymentMethod +
//                ", status=" + status +
//                ", orderId='" + orderId + '\'' +
//                '}';
//    }
//}
