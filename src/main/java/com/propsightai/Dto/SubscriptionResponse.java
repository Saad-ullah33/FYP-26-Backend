package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.propsightai.Role.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    @NotNull
    @JsonProperty("plan")
    private SubscriptionPlan plan;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("monthly_quota_used")
    private Integer monthlyPropertyQuotaUsed;

    @JsonProperty("monthly_quota_remaining")
    private Integer monthlyQuotaRemaining;

    @JsonProperty("price_pkr")
    private Integer monthlyPriceInPKR;

    @JsonProperty("description")
    private String description;
}
