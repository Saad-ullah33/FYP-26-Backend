package com.propsightai.Dto;

import java.util.Map;

public record EstimationRequest(
    String area,
    String sector,
    String propertyType,
    String purpose
) {}