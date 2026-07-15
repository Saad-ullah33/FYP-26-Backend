package com.propsightai.Role;

import java.util.List;

public enum AuctionViewType {

    /**
     * Public Marketplace Views:
     * Only displays items that have gone live, are about to go live, or have reached
     * a visible finality state for non-authenticated browsers.
     */
    PUBLIC(List.of(
            AuctionStatus.ACTIVE,
            AuctionStatus.SCHEDULED,
            AuctionStatus.ENDED,
            AuctionStatus.RESERVE_NOT_MET,
            AuctionStatus.SOLD
    )),

    /**
     * User/Owner Dashboard Views:
     * Grants visibility into every life-cycle phase of their owned property assets,
     * including payment updates, cancellations, and settlement transitions.
     */
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
            AuctionStatus.SETTLED,
            AuctionStatus.CANCELLED,
            AuctionStatus.CLOSED
    )),

    /**
     * Management/Admin Panel Views:
     * Full overarching system root access to every status constraint across the platform.
     */
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