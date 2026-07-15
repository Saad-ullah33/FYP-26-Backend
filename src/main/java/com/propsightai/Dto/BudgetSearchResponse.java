package com.propsightai.Dto;

import java.util.List;
import java.util.Map;
public record BudgetSearchResponse(
    Integer propertyId,
    String title,
    String address,
    double listingPrice,
    double aiPredictedPrice,
    double expectedGrowthPercentage,
    List<Map<String, Object>> futureForecast
) {}