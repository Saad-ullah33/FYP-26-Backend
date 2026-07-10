//package com.propsightai.Model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "subscription_plans")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class SubscriptionPlan {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
//    private List<UserSubscription> userSubscriptions = new ArrayList<>();
//
//    @Column(nullable = false, unique = true)
//    private String name;
//
//    @Column(name = "max_properties")
//    private Integer maxProperties;
//
//    @Column(name = "max_images")
//    private Integer maxImages;
//
//    @Column(name = "featured_ads")
//    private Boolean featuredAds;
//
//    @Column(name = "auction_access")
//    private Boolean auctionAccess;
//
//    @Column(name = "priority_support")
//    private Boolean prioritySupport;
//
//    @Column(name = "analytics_access")
//    private Boolean analyticsAccess;
//
//    @Column(name = "unlimited_images")
//    private Boolean unlimitedImages;
//
//    @Column(name = "ad_duration_days")
//    private Integer adDurationDays;
//
//    @Column(nullable = false)
//    private Double price;
//}