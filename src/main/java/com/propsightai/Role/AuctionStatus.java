package com.propsightai.Role;

public enum AuctionStatus {
    DRAFT,              // auction created but not published
    PENDING_APPROVAL,   // waiting admin verification
    APPROVED,           // approved and ready
    REJECTED,           // rejected by admin
    SCHEDULED,          // future auction
    ACTIVE,             // live auction running
    PAUSED,             // temporarily stopped
    SUSPENDED,          // blocked due to violation
    ENDED,              // auction time finished
    RESERVE_NOT_MET,    // bids exist but reserve price not achieved
    PAYMENT_PENDING,    // winner selected but payment not completed
    PAYMENT_FAILED,     // winner failed payment
    SOLD,               // payment successful
    SETTLED,            // ownership/document transfer completed
    CANCELLED,          // manually cancelled
    CLOSED              // final archived state
    ;

    /**
     * Grouping utility enabling backward-compatibility filters
     * inside the administrative query engine interfaces.
     * Maps each status cleanly to its primary target audience view.
     */
    public AuctionViewType getViewTypeFallback() {
        return switch (this) {
            // Public marketplace visible items map to PUBLIC view
            case ACTIVE, SCHEDULED, ENDED, RESERVE_NOT_MET, SOLD -> AuctionViewType.PUBLIC;

            // Administrative internal actions map to ADMIN view metrics
            case DRAFT, PENDING_APPROVAL, REJECTED, SUSPENDED -> AuctionViewType.ADMIN;

            // Concluded private settlement updates map to USER dashboard boundaries
            case PAUSED, APPROVED, PAYMENT_PENDING, PAYMENT_FAILED, SETTLED, CANCELLED, CLOSED -> AuctionViewType.USER;
        };
    }
}