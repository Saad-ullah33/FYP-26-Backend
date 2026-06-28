package com.propsightai.Model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class UserSubscription {

    @Id
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanID")
    private SubscriptionPlan plan;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;
}
