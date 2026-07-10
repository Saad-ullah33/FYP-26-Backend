package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.propsightai.Role.PaymentMethod;
import com.propsightai.Role.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {

    @NotNull
    @JsonProperty("plan")
    private SubscriptionPlan plan;

    @NotNull
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("phone_number")
    private String phoneNumber; // For JazzCash/EasyPaisa

    @JsonProperty("metadata")
    private String metadata; // Optional metadata
}
