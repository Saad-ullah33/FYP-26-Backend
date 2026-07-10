package com.propsightai.Controller.PredictionController;

import com.propsightai.Dto.PredictionResponse;
import com.propsightai.Model.Property;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Service.PredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Get price prediction for a specific property
     * GET /api/predictions/{propertyId}
     */
    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getPrediction(@PathVariable Integer propertyId) {
        log.info("Getting price prediction for property ID: {}", propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            PredictionResponse prediction = predictionService.predictProperty(property);
            return ResponseEntity.ok(prediction);
        } catch (IllegalArgumentException e) {
            log.warn("Property not found: {}", propertyId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting prediction for property {}", propertyId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error generating prediction"));
        }
    }

    /**
     * Record actual price for a property to improve model accuracy
     * POST /api/predictions/{propertyId}/record-actual-price
     * Body: { "actualPrice": 5000000 }
     */
    @PostMapping("/{propertyId}/record-actual-price")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> recordActualPrice(
            @PathVariable Integer propertyId,
            @RequestBody Map<String, Double> request
    ) {
        log.info("Recording actual price for property ID: {}", propertyId);

        try {
            Double actualPrice = request.get("actualPrice");
            if (actualPrice == null || actualPrice <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid actual price"));
            }

            propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            predictionService.recordActualPrice(propertyId, actualPrice);

            return ResponseEntity.ok(Map.of(
                    "message", "Actual price recorded successfully",
                    "propertyId", propertyId,
                    "actualPrice", actualPrice
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Property not found: {}", propertyId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error recording actual price for property {}", propertyId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error recording actual price"));
        }
    }

    /**
     * Get model accuracy statistics
     * GET /api/predictions/model/accuracy
     */
    @GetMapping("/model/accuracy")
    public ResponseEntity<?> getModelAccuracy() {
        log.info("Getting model accuracy");

        try {
            Double accuracy = predictionService.getModelAccuracy();
            return ResponseEntity.ok(Map.of(
                    "accuracy_percentage", accuracy,
                    "model_status", accuracy >= 80 ? "High Performance" : accuracy >= 60 ? "Good" : "Needs Improvement",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Error getting model accuracy", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error fetching model accuracy"));
        }
    }

    /**
     * Get prediction for multiple properties
     * POST /api/predictions/batch
     * Body: { "propertyIds": [1, 2, 3] }
     */
    @PostMapping("/batch")
    public ResponseEntity<?> getBatchPredictions(@RequestBody Map<String, Object> request) {
        log.info("Getting batch predictions");

        try {
            @SuppressWarnings("unchecked")
            java.util.List<Integer> propertyIds = (java.util.List<Integer>) request.get("propertyIds");

            if (propertyIds == null || propertyIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Property IDs required"));
            }

            if (propertyIds.size() > 50) {
                return ResponseEntity.badRequest().body(Map.of("error", "Maximum 50 properties allowed per batch"));
            }

            Map<Integer, Object> results = new HashMap<>();
            for (Integer propertyId : propertyIds) {
                try {
                    Property property = propertyRepository.findById(propertyId).orElse(null);
                    if (property != null) {
                        PredictionResponse prediction = predictionService.predictProperty(property);
                        results.put(propertyId, prediction);
                    }
                } catch (Exception e) {
                    log.warn("Error predicting property {}", propertyId, e);
                    results.put(propertyId, Map.of("error", "Prediction failed"));
                }
            }

            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "total_processed", results.size(),
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Error getting batch predictions", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error processing batch predictions"));
        }
    }

    /**
     * Get prediction explanation for a property
     * GET /api/predictions/{propertyId}/explanation
     */
    @GetMapping("/{propertyId}/explanation")
    public ResponseEntity<?> getPredictionExplanation(@PathVariable Integer propertyId) {
        log.info("Getting prediction explanation for property ID: {}", propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            PredictionResponse prediction = predictionService.predictProperty(property);

            Map<String, Object> explanation = new HashMap<>();
            explanation.put("propertyId", propertyId);
            explanation.put("predictedPrice", prediction.getPredictedPrice());
            explanation.put("confidence", prediction.getConfidence() + "%");
            explanation.put("priceRange", Map.of(
                    "min", prediction.getPriceRangeMin(),
                    "max", prediction.getPriceRangeMax()
            ));
            explanation.put("pricingFactors", prediction.getFactors());
            explanation.put("notes", prediction.getPredictionNotes());
            explanation.put("factorExplanation", Map.of(
                    "size_score", "Base price calculated from property area × PKR 5000 per sq ft",
                    "location_factor", "Location premium/discount based on city (Islamabad +40%, Karachi +30%, etc.)",
                    "type_factor", "Property type multiplier (House +25%, Commercial +40%, etc.)",
                    "purpose_factor", "Usage purpose multiplier (Commercial +30%, Investment +20%)",
                    "auction_factor", "Market activity factor based on current bidding"
            ));

            return ResponseEntity.ok(explanation);
        } catch (IllegalArgumentException e) {
            log.warn("Property not found: {}", propertyId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting prediction explanation for property {}", propertyId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error generating explanation"));
        }
    }

    /**
     * Compare predicted vs actual prices
     * GET /api/predictions/{propertyId}/accuracy-check
     */
    @GetMapping("/{propertyId}/accuracy-check")
    public ResponseEntity<?> getAccuracyCheck(@PathVariable Integer propertyId) {
        log.info("Getting accuracy check for property ID: {}", propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            PredictionResponse prediction = predictionService.predictProperty(property);

            Map<String, Object> response = new HashMap<>();
            response.put("propertyId", propertyId);
            response.put("predictedPrice", prediction.getPredictedPrice());
            response.put("confidence", prediction.getConfidence() + "%");
            response.put("status", "awaiting_actual_price");
            response.put("message", "Actual price has not been recorded yet. Please record actual price to compare.");
            response.put("recordActualPriceEndpoint", "POST /api/predictions/" + propertyId + "/record-actual-price");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Property not found: {}", propertyId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting accuracy check for property {}", propertyId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error fetching accuracy data"));
        }
    }
}
