package com.propsightai.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AuctionRequestDTO {

    private Integer propertyId;

    private BigDecimal startingPrice;

    private BigDecimal reservePrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}