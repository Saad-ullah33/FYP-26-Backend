package com.propsightai.Service;

import com.propsightai.Dto.BudgetSearchResponse;
import com.propsightai.Dto.EstimationRequest;
import com.propsightai.Dto.PredictionResponse;
import com.propsightai.Model.Property;

import java.util.List;
import java.util.Map;

public interface PredictionService {

    /**
     * Predict property price based on characteristics.
     *
     * @param property the property to predict price for
     * @return prediction with price, confidence, and range
     */
    PredictionResponse predictProperty(Property property);

    /**
     * Update model with actual selling price (for model accuracy tracking).
     *
     * @param propertyId the property ID
     * @param actualPrice the actual selling price
     */
    void recordActualPrice(Integer propertyId, Double actualPrice);

    /**
     * Get prediction accuracy metrics (optional).
     *
     * @return accuracy percentage
     */
    Double getModelAccuracy();

    List<Map<String, Object>> getHistoricalTrends(Integer propertyId);

    List<Map<String, Object>> getTopUndervaluedDeals(int limit, String city);

    List<BudgetSearchResponse> getPropertiesByBudgetWithPredictions(double minPrice, double maxPrice);

    PredictionResponse estimateRawPropertyPrice(EstimationRequest request);
}
