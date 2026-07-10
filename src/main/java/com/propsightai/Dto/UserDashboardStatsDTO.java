package com.propsightai.Dto;

import lombok.Data;

@Data
public class UserDashboardStatsDTO {

    private int totalProperties;
    private int totalAuctions;

    private int activeAuctions;
    private int pendingAuctions;
    private int rejectedAuctions;
    private int soldAuctions;
}