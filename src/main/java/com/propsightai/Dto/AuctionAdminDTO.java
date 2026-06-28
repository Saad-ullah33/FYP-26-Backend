package com.propsightai.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionAdminDTO {

    private Integer id;

    private Integer propertyId;

    private String propertyTitle;

    private BigDecimal startingPrice;

    private BigDecimal reservePrice;

    private BigDecimal currentHighestBid;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private String winnerName;

    private Integer totalBids;

    private List<BidDTO> bids;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}