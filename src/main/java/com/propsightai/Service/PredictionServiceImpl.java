package com.propsightai.Service;

import com.propsightai.Dto.PredictionResponse;
import com.propsightai.Model.Property;
import com.propsightai.Model.PredictionRecord;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Repository.PredictionRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {

    private final PropertyRepository propertyRepository;

    private final PredictionRecordRepository predictionRecordRepository;

    @Autowired
    public PredictionServiceImpl(PropertyRepository propertyRepository, PredictionRecordRepository predictionRecordRepository) {
        this.propertyRepository = propertyRepository;
        this.predictionRecordRepository = predictionRecordRepository;
    }

    @Override
    public PredictionResponse predictProperty(Property property) {
        log.info("Generating price prediction for property ID: {}", property.getId());

        try {
            // Calculate base price per square foot (market average)
            double basePricePerSqFt = 5000.0; // PKR 5000 per sq ft (configurable)

            // Extract numeric area value from property (handles "5000 sq ft" or "5000")
            double area = parseArea(property.getArea());

            // Calculate price factors
            Map<String, Double> factors = new HashMap<>();

            // 1. Size factor: area * base price
            double sizeScore = area * basePricePerSqFt;
            factors.put("size_score", sizeScore);

            // 2. Location factor (city-based multiplier)
            double locationMultiplier = getLocationMultiplier(property.getCity() != null ? property.getCity().getName() : "Default");
            double locationFactor = sizeScore * (locationMultiplier - 1.0);
            factors.put("location_factor", locationFactor);

            // 3. Property type factor
            double typeMultiplier = getPropertyTypeMultiplier(property.getPropertyType() != null ? property.getPropertyType().toString() : "RESIDENTIAL");
            double typeFactor = sizeScore * (typeMultiplier - 1.0);
            factors.put("type_factor", typeFactor);

            // 4. Purpose factor (Residential vs Commercial)
            double purposeMultiplier = getPurposeMultiplier(property.getPurpose() != null ? property.getPurpose().toString() : "RESIDENTIAL");
            double purposeFactor = sizeScore * (purposeMultiplier - 1.0);
            factors.put("purpose_factor", purposeFactor);

            // 5. Auction activity factor (if property has active auction)
            double auctionFactor = 0;
            if (property.getAuctions() != null && property.getAuctions().getCurrentHighestBid() != null) {
                double highestBid = property.getAuctions().getCurrentHighestBid().doubleValue();
                auctionFactor = Math.min(highestBid * 0.1, sizeScore * 0.2);
            }
            factors.put("auction_factor", auctionFactor);

            // Calculate predicted price
            double predictedPrice = sizeScore +
                    locationFactor +
                    typeFactor +
                    purposeFactor +
                    auctionFactor;

            // Confidence score (0-100) based on data completeness
            int confidence = calculateConfidence(property);

            // Price range (±15% with adjusted multipliers)
            double priceRangeMin = predictedPrice * 0.85;
            double priceRangeMax = predictedPrice * 1.15;

            log.info("Price prediction completed: {} PKR (Confidence: {}%)", predictedPrice, confidence);

            // Record the prediction for model accuracy tracking
            PredictionRecord record = new PredictionRecord();
            record.setProperty(property);
            record.setPredictedPrice(predictedPrice);
            record.setConfidenceScore(confidence);
            record.setPredictedAt(LocalDateTime.now());
            predictionRecordRepository.save(record);

            PredictionResponse response = new PredictionResponse(
                    predictedPrice,
                    confidence,
                    priceRangeMin,
                    priceRangeMax,
                    factors
            );

            response.setPredictionNotes(generatePredictionNotes(property, confidence));
            return response;

        } catch (Exception e) {
            log.error("Error generating price prediction for property ID: {}", property.getId(), e);
            return getDefaultPrediction(property);
        }
    }

    @Override
    public void recordActualPrice(Integer propertyId, Double actualPrice) {
        log.info("Recording actual price for property ID: {} - Price: {}", propertyId, actualPrice);
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            // Find the most recent prediction record for this property
            var predictions = predictionRecordRepository.findByPropertyIdOrderByPredictedAtDesc(propertyId);
            if (!predictions.isEmpty()) {
                PredictionRecord latestRecord = predictions.get(0);
                latestRecord.setActualPrice(actualPrice);
                latestRecord.setRecordedAt(LocalDateTime.now());
                latestRecord.calculateError();
                predictionRecordRepository.save(latestRecord);
                log.info("Recorded actual price for property {}. Error: {}%", propertyId, latestRecord.getPredictionErrorPercentage());
            }
        } catch (Exception e) {
            log.error("Error recording actual price for property ID: {}", propertyId, e);
        }
    }

    @Override
    public Double getModelAccuracy() {
        try {
            Long totalPredictions = predictionRecordRepository.countCompletedPredictions();
            if (totalPredictions == 0) {
                log.debug("No completed predictions yet, returning default accuracy");
                return 75.0;
            }

            Long accuratePredictions = predictionRecordRepository.countAccuratePredictions();
            Double accuracy = (accuratePredictions.doubleValue() / totalPredictions) * 100;
            log.debug("Model accuracy: {}% ({}/{} accurate predictions)", accuracy, accuratePredictions, totalPredictions);
            return accuracy;
        } catch (Exception e) {
            log.error("Error calculating model accuracy", e);
            return 75.0;
        }
    }

    /**
     * Parse area from string (handles "5000 sq ft" or "5000")
     */
    private double parseArea(String areaStr) {
        if (areaStr == null || areaStr.trim().isEmpty()) {
            return 5000.0; // Default area
        }
        
        try {
            // Extract numeric part only
            String numericPart = areaStr.replaceAll("[^0-9.]", "");
            return Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            log.warn("Could not parse area value: {}, using default", areaStr);
            return 5000.0;
        }
    }

    /**
     * Calculate confidence score based on data completeness (0-100)
     */
    private int calculateConfidence(Property property) {
        int confidence = 50; // Base confidence

        if (property.getArea() != null && !property.getArea().isEmpty()) confidence += 15;
        if (property.getCity() != null) confidence += 15;
        if (property.getPropertyType() != null) confidence += 10;
        if (property.getPurpose() != null) confidence += 10;
        if (property.getAuctions() != null) confidence += 5;

        return Math.min(confidence, 100);
    }

    /**
     * Get location multiplier based on city (e.g., Islamabad premium vs smaller cities)
     */
    private double getLocationMultiplier(String cityName) {
        return switch (cityName.toLowerCase()) {
            case "islamabad" -> 1.4;
            case "lahore" -> 1.35;
            case "karachi" -> 1.3;
            case "rawalpindi" -> 1.2;
            case "peshawar" -> 1.1;
            default -> 1.0;
        };
    }

    /**
     * Get property type multiplier
     */
    private double getPropertyTypeMultiplier(String propertyType) {
        return switch (propertyType.toUpperCase()) {
            case "APARTMENT" -> 1.15;
            case "HOUSE" -> 1.25;
            case "COMMERCIAL" -> 1.4;
            case "LAND" -> 0.9;
            case "FLAT" -> 1.1;
            default -> 1.0;
        };
    }

    /**
     * Get purpose multiplier (Residential vs Commercial)
     */
    private double getPurposeMultiplier(String purpose) {
        return switch (purpose.toUpperCase()) {
            case "COMMERCIAL" -> 1.3;
            case "RESIDENTIAL" -> 1.0;
            case "INVESTMENT" -> 1.2;
            default -> 1.0;
        };
    }

    /**
     * Generate human-readable prediction notes
     */
    private String generatePredictionNotes(Property property, int confidence) {
        StringBuilder notes = new StringBuilder();

        notes.append("Prediction based on ");
        if (confidence >= 80) {
            notes.append("strong market data (high confidence)");
        } else if (confidence >= 60) {
            notes.append("moderate market data (medium confidence)");
        } else {
            notes.append("limited market data (use with caution)");
        }

        if (property.getCity() != null) {
            notes.append(" in ").append(property.getCity().getName());
        }

        if (property.getArea() != null) {
            notes.append(" for ").append(property.getArea());
        }

        notes.append(". Actual market value may vary based on condition, amenities, and current demand.");

        return notes.toString();
    }

    /**
     * Return default prediction when calculation fails
     */
    private PredictionResponse getDefaultPrediction(Property property) {
        double area = parseArea(property.getArea());
        double basePrediction = area * 3000;
        return new PredictionResponse(
                basePrediction,
                40, // Low confidence
                basePrediction * 0.7,
                basePrediction * 1.3
        );
    }
}


