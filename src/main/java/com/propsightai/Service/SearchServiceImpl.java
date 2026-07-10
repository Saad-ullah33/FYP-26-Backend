package com.propsightai.Service;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.Property;
import com.propsightai.Repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private PropertyScoringService scoringService;

    @Override
    public Page<PropertyResponseDto> searchProperties(String keyword, SearchFilters filters, Pageable pageable) {
        logger.info("Searching properties with keyword: '{}' and filters: {}", keyword, filters);

        // Start with paginated search using existing repository method
        Page<Property> results = propertyRepository.searchProperties(
                filters != null && filters.cityId != null ? filters.cityId : null,
                filters != null ? filters.propertyType : null,
                filters != null ? filters.purpose : null,
                filters != null ? filters.minPrice : null,
                filters != null ? filters.maxPrice : null,
                pageable
        );

        // If keyword provided, filter by title/description in memory
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchTerm = keyword.toLowerCase().trim();
            List<Property> filtered = results.getContent().stream()
                    .filter(p -> {
                        String title = p.getTitle() != null ? p.getTitle().toLowerCase() : "";
                        String desc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                        return title.contains(searchTerm) || desc.contains(searchTerm);
                    })
                    .collect(Collectors.toList());
            
            results = new PageImpl<>(filtered, pageable, filtered.size());
        }

        // Convert to DTOs with predictions and scores
        return results.map(this::convertToDto);
    }

    @Override
    public Page<PropertyResponseDto> searchByTitle(String title, Pageable pageable) {
        logger.info("Searching properties by title: '{}'", title);
        
        // Get all properties and filter by title
        List<Property> allProperties = propertyRepository.findAll();
        
        String searchTerm = title.toLowerCase().trim();
        List<Property> filtered = allProperties.stream()
                .filter(p -> p.getTitle() != null && p.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Property> pageContent = filtered.subList(Math.min(start, filtered.size()), end);
        
        return new PageImpl<>(pageContent.stream().map(this::convertToDto).collect(Collectors.toList()),
                pageable,
                filtered.size());
    }

    @Override
    public Page<PropertyResponseDto> searchByLocation(Long cityId, Pageable pageable) {
        logger.info("Searching properties by city ID: {}", cityId);
        
        Page<Property> results = propertyRepository.searchProperties(
                cityId,
                null,
                null,
                null,
                null,
                pageable
        );
        
        return results.map(this::convertToDto);
    }

    @Override
    public Page<PropertyResponseDto> searchBySimilarPrice(Double basePrice, Integer tolerancePercent, Pageable pageable) {
        logger.info("Searching properties with similar price to: {} (±{}%)", basePrice, tolerancePercent);
        
        if (basePrice == null || basePrice <= 0) {
            return Page.empty(pageable);
        }
        
        double tolerance = (tolerancePercent != null && tolerancePercent > 0) ? tolerancePercent : 20;
        double minPrice = basePrice * (1 - tolerance / 100);
        double maxPrice = basePrice * (1 + tolerance / 100);
        
        Page<Property> results = propertyRepository.searchProperties(
                null,
                null,
                null,
                minPrice,
                maxPrice,
                pageable
        );
        
        return results.map(this::convertToDto);
    }

    /**
     * Convert Property entity to PropertyResponseDto with predictions and scores.
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

        // Add prediction
        try {
            dto.setPrediction(predictionService.predictProperty(property));
        } catch (Exception e) {
            logger.warn("Failed to generate prediction for property {}", property.getId(), e);
        }

        // Add score
        try {
            dto.setScore(scoringService.calculateScore(property));
        } catch (Exception e) {
            logger.warn("Failed to calculate score for property {}", property.getId(), e);
        }

        return dto;
    }
}
