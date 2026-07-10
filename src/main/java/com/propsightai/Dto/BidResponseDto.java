package com.propsightai.Dto;


import com.propsightai.Model.Bid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDto {


    private Integer id;

    private Integer auctionId;

    private Integer bidderId;

    private BigDecimal amount;

    private LocalDateTime bidTime;

    private String message;



    // Entity -> DTO constructor
    public BidResponseDto(Bid bid) {

        this.id = bid.getId();

        this.auctionId =
                bid.getAuction().getId();


        this.bidderId =
                bid.getBidder().getId();


        this.amount =
                bid.getAmount();


        this.bidTime =
                bid.getBidTime();


        this.message =
                "Bid placed successfully";
    }
}