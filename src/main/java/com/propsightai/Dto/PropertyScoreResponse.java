package com.propsightai.Dto;

import lombok.Data;

@Data
public class PropertyScoreResponse {
    private int score;
    private String label;
    private String reason;
}