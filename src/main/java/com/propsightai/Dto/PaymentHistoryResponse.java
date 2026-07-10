package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.propsightai.Role.PaymentMethod;
import com.propsightai.Role.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {

    @NotNull
    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("method")
    private PaymentMethod method;

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("verified_at")
    private LocalDateTime verifiedAt;
}
