package com.propsightai.Service;

import com.propsightai.Model.Auction;
import com.propsightai.Role.AuctionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AuctionValidator {

    /**
     * Validates if a incoming bid satisfies all domain and business logic constraints.
     * Throws explicit runtime exceptions if the bid violates baseline constraints.
     *
     * @param auction The targeted auction entity
     * @param amount  The proposed bidding amount
     */
    public void validateBidAllowed(Auction auction, BigDecimal amount) {
        // 1. Structural Sanity Checks
        if (auction == null) {
            throw new IllegalArgumentException("Target auction context cannot be null.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bid amount must be a positive, non-zero value.");
        }

        // 2. State & Soft-Delete Validation
        if (Boolean.TRUE.equals(auction.getDeleted())) {
            throw new IllegalStateException("Cannot bid on an auction that has been deleted.");
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Bidding rejected: Auction status is currently " + auction.getStatus());
        }

        // 3. Temporal (Time-Window) Verification
        LocalDateTime now = LocalDateTime.now();
        if (auction.getStartTime() != null && now.isBefore(auction.getStartTime())) {
            throw new IllegalStateException("Bidding rejected: Auction has not officially started yet.");
        }

        if (auction.getEndTime() != null && now.isAfter(auction.getEndTime())) {
            throw new IllegalStateException("Bidding rejected: Auction window has already closed.");
        }

        // 4. Pricing Framework Validation
        BigDecimal currentHighest = auction.getCurrentHighestBid();

        if (currentHighest != null) {
            // Outbid validation
            if (amount.compareTo(currentHighest) <= 0) {
                throw new IllegalArgumentException("Bid of " + amount + " must strictly exceed the current highest bid of " + currentHighest);
            }
        } else {
            // First-bid threshold floor validation
            BigDecimal startingPrice = auction.getStartingPrice();
            if (startingPrice != null && amount.compareTo(startingPrice) < 0) {
                throw new IllegalArgumentException("The initial bid must be at least equal to the opening starting price of " + startingPrice);
            }
        }
    }
}