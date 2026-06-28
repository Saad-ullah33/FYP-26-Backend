package com.propsightai.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "plan",
            fetch = FetchType.LAZY)
    private List<UserSubscription> userSubscriptions = new ArrayList<>();

    private String name;

    private Integer maxProperties;

    private Integer maxImages;

    private Boolean featuredAds;

    private Boolean auctionAccess;

    private Boolean prioritySupport;

    private Boolean analyticsAccess;

    private Boolean unlimitedImages;

    private Integer adDurationDays;

    private Double price;
}