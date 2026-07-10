package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class PredictionResponse {

    @NotNull
    @JsonProperty("predicted_price")
    private Double predictedPrice;

    @NotNull
    @JsonProperty("confidence_score")
    private Integer confidence; // 0-100

    @NotNull
    @JsonProperty("price_range_min")
    private Double priceRangeMin;

    @NotNull
    @JsonProperty("price_range_max")
    private Double priceRangeMax;

    @JsonProperty("factors")
    private Map<String, Double> factors; // Breakdown of scoring

    @JsonProperty("prediction_notes")
    private String predictionNotes;

    // Constructors
    public PredictionResponse() {
    }

    public PredictionResponse(Double predictedPrice, Integer confidence, Double priceRangeMin, Double priceRangeMax) {
        this.predictedPrice = predictedPrice;
        this.confidence = confidence;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
    }

    public PredictionResponse(Double predictedPrice, Integer confidence, Double priceRangeMin, Double priceRangeMax, Map<String, Double> factors) {
        this.predictedPrice = predictedPrice;
        this.confidence = confidence;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.factors = factors;
    }

    // Getters and Setters
    public Double getPredictedPrice() {
        return predictedPrice;
    }

    public void setPredictedPrice(Double predictedPrice) {
        this.predictedPrice = predictedPrice;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Double getPriceRangeMin() {
        return priceRangeMin;
    }

    public void setPriceRangeMin(Double priceRangeMin) {
        this.priceRangeMin = priceRangeMin;
    }

    public Double getPriceRangeMax() {
        return priceRangeMax;
    }

    public void setPriceRangeMax(Double priceRangeMax) {
        this.priceRangeMax = priceRangeMax;
    }

    public Map<String, Double> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Double> factors) {
        this.factors = factors;
    }

    public String getPredictionNotes() {
        return predictionNotes;
    }

    public void setPredictionNotes(String predictionNotes) {
        this.predictionNotes = predictionNotes;
    }
}
