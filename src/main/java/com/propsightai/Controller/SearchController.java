package com.propsightai.Controller;

import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import com.propsightai.Service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * Advanced search with keyword and filters.
     * GET /api/search?q=bedroom&cityId=1&minPrice=100000&maxPrice=500000&page=0&size=10
     */
    @GetMapping("")
    public ResponseEntity<Page<PropertyResponseDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) PurposeType purpose,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer minBathrooms,
            Pageable pageable
    ) {
        log.info("Advanced search: q='{}', cityId={}, type={}, purpose={}, priceRange=[{},{}]",
                q, cityId, propertyType, purpose, minPrice, maxPrice);

        SearchService.SearchFilters filters = new SearchService.SearchFilters()
                .withCity(cityId)
                .withType(propertyType)
                .withPurpose(purpose)
                .withPriceRange(minPrice, maxPrice)
                .withBedrooms(minBedrooms)
                .withBathrooms(minBathrooms);

        Page<PropertyResponseDto> results = searchService.searchProperties(q, filters, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Search by title only.
     * GET /api/search/title?q=luxury&page=0&size=10
     */
    @GetMapping("/title")
    public ResponseEntity<Page<PropertyResponseDto>> searchByTitle(
            @RequestParam String q,
            Pageable pageable
    ) {
        log.info("Title search: '{}'", q);
        Page<PropertyResponseDto> results = searchService.searchByTitle(q, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Search by location/city.
     * GET /api/search/location?cityId=1&page=0&size=10
     */
    @GetMapping("/location")
    public ResponseEntity<Page<PropertyResponseDto>> searchByLocation(
            @RequestParam Long cityId,
            Pageable pageable
    ) {
        log.info("Location search: cityId={}", cityId);
        Page<PropertyResponseDto> results = searchService.searchByLocation(cityId, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Search by similar price range.
     * GET /api/search/similar-price?basePrice=500000&tolerance=20&page=0&size=10
     */
    @GetMapping("/similar-price")
    public ResponseEntity<Page<PropertyResponseDto>> searchBySimilarPrice(
            @RequestParam Double basePrice,
            @RequestParam(required = false, defaultValue = "20") Integer tolerance,
            Pageable pageable
    ) {
        log.info("Price similarity search: basePrice={}, tolerance={}%", basePrice, tolerance);
        
        if (basePrice == null || basePrice <= 0) {
            return ResponseEntity.badRequest()
                    .body(null); // Or you could return a proper error response
        }

        Page<PropertyResponseDto> results = searchService.searchBySimilarPrice(basePrice, tolerance, pageable);
        return ResponseEntity.ok(results);
    }
}
