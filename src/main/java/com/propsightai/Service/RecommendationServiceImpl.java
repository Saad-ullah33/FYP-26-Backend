package com.propsightai.Service;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.ActivityEvent;
import com.propsightai.Model.Property;
import com.propsightai.Repository.ActivityRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.ActivityEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PropertyScoringService scoringService;

    @Autowired
    private PredictionService predictionService;

    @Override
    public List<PropertyResponseDto> getRecommendations(Integer userId, Integer limit) {
        logger.info("Getting recommendations for user {}", userId);
        return getPersonalizedRecommendations(userId, limit);
    }

    @Override
    public List<PropertyResponseDto> getPersonalizedRecommendations(Integer userId, Integer limit) {
        logger.info("Getting personalized recommendations for user {}", userId);

        int recommendationLimit = limit != null && limit > 0 ? limit : 10;

        try {
            // Get user's recently viewed properties to understand preferences
            List<ActivityEvent> userViews = activityRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(e -> e.getEventType() == ActivityEventType.PROPERTY_VIEW)
                    .limit(10) // Look at last 10 views
                    .collect(Collectors.toList());

            if (userViews.isEmpty()) {
                // If no history, return trending properties
                return getTrendingProperties(recommendationLimit);
            }

            // Get categories from viewed properties
            Set<String> userPreferredCategories = new HashSet<>();
            Map<String, Integer> categoryFrequency = new HashMap<>();

            for (ActivityEvent view : userViews) {
                Property property = propertyRepository.findById(view.getPropertyId()).orElse(null);
                if (property != null) {
                    String category = property.getPropertyType().toString();
                    userPreferredCategories.add(category);
                    categoryFrequency.merge(category, 1, Integer::sum);
                }
            }

            // Get properties in preferred categories, excluding already viewed
            Set<Integer> viewedPropertyIds = userViews.stream()
                    .map(ActivityEvent::getPropertyId)
                    .collect(Collectors.toSet());

            List<Property> allProperties = propertyRepository.findAll();
            List<PropertyResponseDto> recommendations = allProperties.stream()
                    .filter(p -> !viewedPropertyIds.contains(p.getId())) // Exclude viewed
                    .filter(p -> userPreferredCategories.contains(p.getPropertyType().toString())) // Match category
                    .sorted(Comparator.comparing(this::getPropertyScore).reversed()) // Sort by score
                    .limit(recommendationLimit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            logger.info("Generated {} personalized recommendations for user {}", recommendations.size(), userId);
            return recommendations;

        } catch (Exception e) {
            logger.error("Error generating personalized recommendations for user {}", userId, e);
            return getTrendingProperties(recommendationLimit);
        }
    }

    @Override
    public List<PropertyResponseDto> getTrendingProperties(Integer limit) {
        logger.info("Getting trending properties");

        int trendingLimit = limit != null && limit > 0 ? limit : 10;

        try {
            // Get most viewed properties in last 30 days
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            
            List<Object[]> mostViewed = activityRepository.getMostViewedProperties(
                    ActivityEventType.PROPERTY_VIEW,
                    thirtyDaysAgo
            );

            List<PropertyResponseDto> trending = new ArrayList<>();
            
            for (Object[] row : mostViewed) {
                Integer propertyId = ((Number) row[0]).intValue();
                Property property = propertyRepository.findById(propertyId).orElse(null);
                if (property != null) {
                    trending.add(convertToDto(property));
                }
                if (trending.size() >= trendingLimit) break;
            }

            logger.info("Found {} trending properties", trending.size());
            return trending;

        } catch (Exception e) {
            logger.error("Error getting trending properties", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<PropertyResponseDto> getSimilarProperties(Integer propertyId, Integer limit) {
        logger.info("Getting properties similar to property {}", propertyId);

        int similarLimit = limit != null && limit > 0 ? limit : 5;

        try {
            Property referenceProperty = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            List<Property> allProperties = propertyRepository.findAll();
            
            List<PropertyResponseDto> similarProperties = allProperties.stream()
                    .filter(p -> !p.getId().equals(propertyId)) // Exclude reference property
                    .filter(p -> p.getPropertyType().equals(referenceProperty.getPropertyType())) // Same type
                    .filter(p -> p.getPurpose().equals(referenceProperty.getPurpose())) // Same purpose
                    .filter(p -> p.getCity() != null && p.getCity().equals(referenceProperty.getCity())) // Same city
                    .sorted(Comparator.comparing(p -> Math.abs(
                            (p.getPrice() != null ? p.getPrice() : 0) - 
                            (referenceProperty.getPrice() != null ? referenceProperty.getPrice() : 0)
                    ))) // Sort by price similarity
                    .limit(similarLimit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            logger.info("Found {} similar properties to property {}", similarProperties.size(), propertyId);
            return similarProperties;

        } catch (Exception e) {
            logger.error("Error getting similar properties for property {}", propertyId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get numeric score for a property for ranking.
     */
    private double getPropertyScore(Property property) {
        try {
            var score = scoringService.calculateScore(property);
            return score.getScore() != 0 ? score.getScore() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Convert Property to PropertyResponseDto.
     */
    private PropertyResponseDto convertToDto(Property property) {
        PropertyResponseDto dto = new PropertyResponseDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setPurpose(property.getPurpose());
        dto.setPropertyType(property.getPropertyType());
        dto.setImages(property.getImages().stream()
                .map(img -> img.getCloudinary_src())
                .collect(Collectors.toList()));
        dto.setCity(property.getCity() != null ? property.getCity().getName() : null);
        dto.setLocation(property.getLocation());
        dto.setBathrooms(property.getBathrooms());
        dto.setBedrooms(property.getBedrooms());
        dto.setAddress(property.getAddress());
        dto.setIsAvailable(property.getAvailable());
        dto.setFeatured(property.getFeatured());
        dto.setAuctionEnabled(property.getAuctionEnabled());
        dto.setViewsCount(property.getViewsCount());
        dto.setApproved(property.getApproved());
        dto.setSold(property.getSold());
        dto.setCreatedAt(property.getCreatedAt());

        try {
            dto.setPrediction(predictionService.predictProperty(property));
        } catch (Exception e) {
            logger.warn("Failed to generate prediction for property {}", property.getId());
        }

        try {
            dto.setScore(scoringService.calculateScore(property));
        } catch (Exception e) {
            logger.warn("Failed to calculate score for property {}", property.getId());
        }

        return dto;
    }
}
