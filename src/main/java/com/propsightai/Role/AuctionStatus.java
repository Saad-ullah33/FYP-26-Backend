package com.propsightai.Role;

public enum AuctionStatus {
    SCHEDULED,   // future auction
    ACTIVE,      // currently running
    FAILED,      // ended but reserve not met
    SOLD,        // successful auction
    CANCELLED,
    CLOSED
}

