package com.propsightai.Service;

import com.propsightai.Model.Auction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuctionValidator {

    public void validateBidAllowed(Auction auction, BigDecimal amount) {
        if (auction == null) throw new IllegalArgumentException("Auction not found");
        if (auction.getStatus() == null || !auction.getStatus().name().equals("ACTIVE")) {
            throw new IllegalStateException("Auction is not active");
        }

        BigDecimal current = auction.getCurrentHighestBid();
        if (current != null && amount.compareTo(current) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than current highest bid");
        }
    }
}