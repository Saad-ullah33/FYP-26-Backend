package com.propsightai.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidCreateDto {
    @NotNull
    private Integer auctionId;

    @NotNull
    @Positive
    private BigDecimal amount;
}