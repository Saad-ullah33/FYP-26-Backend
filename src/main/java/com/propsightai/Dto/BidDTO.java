package com.propsightai.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class BidDTO {

    private Integer id;

    private BigDecimal amount;

    private String bidderName;

    private LocalDateTime bidTime;
}