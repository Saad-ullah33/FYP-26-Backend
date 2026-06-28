package com.propsightai.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AuctionPublicDTO {

    private Integer id;

    private BigDecimal startingPrice;

    private BigDecimal currentHighestBid;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;   // only readable status

    private Integer propertyId;
    private String propertyTitle;

    private String location;
}