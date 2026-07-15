package com.propsightai.Service;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.ActivityEvent;
import com.propsightai.Model.Property;
import com.propsightai.Repository.ActivityRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.ActivityEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Read-only query execution increases baseline system scalability
public class RecommendationServiceImpl implements RecommendationService {

    private final PropertyRepository propertyRepository;
    private final ActivityRepository activityRepository;
    private final PropertyScoringService scoringService;
    private final PredictionService predictionService;

    @Override
    public List<PropertyResponseDto> getRecommendations(Integer userId, Integer limit) {
        return getPersonalizedRecommendations(userId, limit);
    }

    @Override
    public List<PropertyResponseDto> getPersonalizedRecommendations(Integer userId, Integer limit) {
        int recommendationLimit = (limit != null && limit > 0) ? limit : 10;
        log.info("Generating indexed personalized recommendation cluster for user: {}", userId);

        try {
            // 1. Efficient paginated lookups for user history logs
            List<ActivityEvent> userViews = activityRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(e -> e.getEventType() == ActivityEventType.PROPERTY_VIEW)
                    .limit(10)
                    .toList();

            if (userViews.isEmpty()) {
                log.info("User history vector empty for ID: {}. Routing to fallback trending results.", userId);
                return getTrendingProperties(recommendationLimit);
            }

            Set<Integer> viewedPropertyIds = userViews.stream()
                    .map(ActivityEvent::getPropertyId)
                    .collect(Collectors.toSet());

            // 2. CRITICAL BATCH FIX: In-clause lookup replaces N+1 inner-loop query operations
            Map<Integer, Property> propertiesMap = propertyRepository.findAllById(viewedPropertyIds).stream()
                    .collect(Collectors.toMap(Property::getId, Function.identity()));

            Set<String> preferredCategoryStrings = propertiesMap.values().stream()
                    .filter(Objects::nonNull)
                    .map(p -> p.getPropertyType().name())
                    .collect(Collectors.toSet());

            if (preferredCategoryStrings.isEmpty()) {
                return getTrendingProperties(recommendationLimit);
            }

            // 3. PERFORMANCE FIX: Query the DB for matches directly instead of using propertyRepository.findAll()
            List<Property> targetedCandidates = propertyRepository.findByPropertyTypeNamesInAndIdNotIn(
                    preferredCategoryStrings, viewedPropertyIds, PageRequest.of(0, 100));

            return targetedCandidates.stream()
                    .sorted(Comparator.comparingDouble(this::getPropertyScore).reversed())
                    .limit(recommendationLimit)
                    .map(this::convertToDto)
                    .toList();

        } catch (Exception e) {
            log.error("Error generating customized engine recommendations for user ID: {}", userId, e);
            return getTrendingProperties(recommendationLimit);
        }
    }

    @Override
    public List<PropertyResponseDto> getTrendingProperties(Integer limit) {
        int trendingLimit = (limit != null && limit > 0) ? limit : 10;
        log.info("Analyzing user interactions matrix for global trending recommendations.");

        try {
            LocalDateTime thirtyDaysAgo = LocalDate.now().atStartOfDay().minusDays(30);

            // Single database trip pulling top aggregated items directly via pagination boundaries
            List<Object[]> mostViewed = activityRepository.getMostViewedProperties(
                    ActivityEventType.PROPERTY_VIEW, thirtyDaysAgo, PageRequest.of(0, trendingLimit));

            if (mostViewed.isEmpty()) return Collections.emptyList();

            List<Integer> propertyIds = mostViewed.stream().map(row -> ((Number) row[0]).intValue()).toList();

            // Map the collected items as a batch structure
            Map<Integer, Property> trendsMap = propertyRepository.findAllById(propertyIds).stream()
                    .collect(Collectors.toMap(Property::getId, Function.identity()));

            return propertyIds.stream()
                    .map(trendsMap::get)
                    .filter(Objects::nonNull)
                    .map(this::convertToDto)
                    .toList();

        } catch (Exception e) {
            log.error("Fatal exception during trending properties query assembly", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<PropertyResponseDto> getSimilarProperties(Integer propertyId, Integer limit) {
        int similarLimit = (limit != null && limit > 0) ? limit : 5;
        log.info("Calculating similarity indexing matrix against reference listing ID: {}", propertyId);

        try {
            Property ref = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new NoSuchElementException("Property context not found for ID: " + propertyId));

            // PERFORMANCE FIX: Database-driven range extraction replaces memory-heavy findAll() operations
            List<Property> candidates = propertyRepository.findSimilarPropertiesQuery(
                    ref.getPropertyType(), ref.getPurpose(), ref.getCity(), propertyId, PageRequest.of(0, 50));

            return candidates.stream()
                    .sorted(Comparator.comparing(p -> {
                        long refPrice = ref.getPrice() != null ? ref.getPrice().longValue() : 0L;
                        long candPrice = p.getPrice() != null ? p.getPrice().longValue() : 0L;
                        return Math.abs(candPrice - refPrice);
                    }))
                    .limit(similarLimit)
                    .map(this::convertToDto)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to extract spatial lookalikes for listing item: {}", propertyId, e);
            return Collections.emptyList();
        }
    }

    private double getPropertyScore(Property property) {
        try {
            var score = scoringService.calculateScore(property);
            return (score != null) ? score.getScore() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private PropertyResponseDto convertToDto(Property property) {
        PropertyResponseDto dto = new PropertyResponseDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setPurpose(property.getPurpose());
        dto.setPropertyType(property.getPropertyType());

        if (property.getImages() != null) {
            dto.setImages(property.getImages().stream()
                    .map(img -> img.getCloudinary_src())
                    .filter(Objects::nonNull)
                    .toList());
        }

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

        // Isolated diagnostic try-catch wrappers for safety
        try {
            dto.setPrediction(predictionService.predictProperty(property));
        } catch (Exception e) {
            log.warn("Asynchronous analytics skipping: Prediction exception trace for listing item: {}", property.getId());
        }

        try {
            dto.setScore(scoringService.calculateScore(property));
        } catch (Exception e) {
            log.warn("Failed to append diagnostic score map profile to DTO row output: {}", property.getId());
        }

        return dto;
    }
}