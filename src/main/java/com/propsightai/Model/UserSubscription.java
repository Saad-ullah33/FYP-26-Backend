//package com.propsightai.Model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "user_subscriptions")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserSubscription {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subscription_id", nullable = false)
//    private SubscriptionPlan subscription;
//
//    @Column(name = "assigned_at")
//    private LocalDate assignedAt;
//
//    @Column(name = "valid")
//    private Boolean valid;
//}
//
