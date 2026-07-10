package com.propsightai.Service;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.Property;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {

    /**
     * Search properties by keyword, filters, and pagination.
     *
     * @param keyword search term (title/description)
     * @param filters optional filters (city, type, purpose, price)
     * @param pageable pagination details
     * @return paginated search results
     */
    Page<PropertyResponseDto> searchProperties(String keyword, SearchFilters filters, Pageable pageable);

    /**
     * Search by title only.
     *
     * @param title property title or partial match
     * @param pageable pagination details
     * @return matching properties
     */
    Page<PropertyResponseDto> searchByTitle(String title, Pageable pageable);

    /**
     * Search by location (city).
     *
     * @param cityId city ID
     * @param pageable pagination details
     * @return properties in that city
     */
    Page<PropertyResponseDto> searchByLocation(Long cityId, Pageable pageable);

    /**
     * Find properties with similar price (price range).
     *
     * @param basePrice base price to match
     * @param tolerancePercent tolerance percentage (e.g., 20 = ±20%)
     * @param pageable pagination details
     * @return properties within price range
     */
    Page<PropertyResponseDto> searchBySimilarPrice(Double basePrice, Integer tolerancePercent, Pageable pageable);

    /**
     * Advanced filters for search.
     */
    class SearchFilters {
        public Long cityId;
        public PropertyType propertyType;
        public PurposeType purpose;
        public Double minPrice;
        public Double maxPrice;
        public Integer minBedrooms;
        public Integer minBathrooms;

        public SearchFilters() {}

        // Fluent builder pattern
        public SearchFilters withCity(Long cityId) { this.cityId = cityId; return this; }
        public SearchFilters withType(PropertyType type) { this.propertyType = type; return this; }
        public SearchFilters withPurpose(PurposeType purpose) { this.purpose = purpose; return this; }
        public SearchFilters withPriceRange(Double min, Double max) { this.minPrice = min; this.maxPrice = max; return this; }
        public SearchFilters withBedrooms(Integer min) { this.minBedrooms = min; return this; }
        public SearchFilters withBathrooms(Integer min) { this.minBathrooms = min; return this; }
    }
}
