package com.propsightai.Role;

import java.util.List;

public enum AuctionViewType{

    PUBLIC(List.of(
            AuctionStatus.ACTIVE,
            AuctionStatus.SCHEDULED,
            AuctionStatus.ENDED,
            AuctionStatus.RESERVE_NOT_MET,
            AuctionStatus.SOLD
    )),

    USER(List.of(
            AuctionStatus.DRAFT,
            AuctionStatus.PENDING_APPROVAL,
            AuctionStatus.APPROVED,
            AuctionStatus.REJECTED,
            AuctionStatus.SCHEDULED,
            AuctionStatus.ACTIVE,
            AuctionStatus.PAUSED,
            AuctionStatus.SUSPENDED,
            AuctionStatus.ENDED,
            AuctionStatus.RESERVE_NOT_MET,
            AuctionStatus.PAYMENT_PENDING,
            AuctionStatus.PAYMENT_FAILED,
            AuctionStatus.SOLD,
            AuctionStatus.CLOSED
    )),

    ADMIN(List.of(
            AuctionStatus.DRAFT,
            AuctionStatus.PENDING_APPROVAL,
            AuctionStatus.APPROVED,
            AuctionStatus.REJECTED,
            AuctionStatus.SCHEDULED,
            AuctionStatus.ACTIVE,
            AuctionStatus.PAUSED,
            AuctionStatus.SUSPENDED,
            AuctionStatus.ENDED,
            AuctionStatus.RESERVE_NOT_MET,
            AuctionStatus.PAYMENT_PENDING,
            AuctionStatus.PAYMENT_FAILED,
            AuctionStatus.SOLD,
            AuctionStatus.SETTLED,
            AuctionStatus.CANCELLED,
            AuctionStatus.CLOSED
    ));

    private final List<AuctionStatus> statuses;

    AuctionViewType(List<AuctionStatus> statuses) {
        this.statuses = statuses;
    }

    public List<AuctionStatus> getStatuses() {
        return statuses;
    }
}