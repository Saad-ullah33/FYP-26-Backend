package com.propsightai.Controller;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get recommendations for authenticated user.
     * GET /api/recommendations?limit=10
     */
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PropertyResponseDto>> getRecommendations(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            Authentication authentication
    ) {
        log.info("Getting recommendations for user: {}", authentication.getName());
        
        try {
            // Extract actual user ID from authentication principal
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            Integer userId = user.getId();
            
            log.debug("Fetching recommendations for user ID: {}", userId);
            List<PropertyResponseDto> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error getting recommendations", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get personalized recommendations for authenticated user.
     * GET /api/recommendations/personalized?limit=10
     */
    @GetMapping("/personalized")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PropertyResponseDto>> getPersonalizedRecommendations(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            Authentication authentication
    ) {
        log.info("Getting personalized recommendations for user: {}", authentication.getName());
        
        try {
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            Integer userId = user.getId();

            log.debug("Fetching personalized recommendations for user ID: {}", userId);
            List<PropertyResponseDto> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error getting personalized recommendations", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get trending properties across the platform.
     * GET /api/recommendations/trending?limit=10
     */
    @GetMapping("/trending")
    public ResponseEntity<List<PropertyResponseDto>> getTrendingProperties(
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        log.info("Getting trending properties with limit: {}", limit);
        
        try {
            List<PropertyResponseDto> trendingProperties = recommendationService.getTrendingProperties(limit);
            return ResponseEntity.ok(trendingProperties);
        } catch (Exception e) {
            log.error("Error getting trending properties", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get properties similar to a given property.
     * GET /api/recommendations/similar/{propertyId}?limit=5
     */
    @GetMapping("/similar/{propertyId}")
    public ResponseEntity<List<PropertyResponseDto>> getSimilarProperties(
            @PathVariable Integer propertyId,
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        log.info("Getting properties similar to property: {}", propertyId);
        
        try {
            List<PropertyResponseDto> similarProperties = recommendationService.getSimilarProperties(propertyId, limit);
            return ResponseEntity.ok(similarProperties);
        } catch (Exception e) {
            log.error("Error getting similar properties for property {}", propertyId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
