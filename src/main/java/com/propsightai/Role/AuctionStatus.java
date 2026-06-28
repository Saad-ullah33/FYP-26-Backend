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
}

