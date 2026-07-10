package com.propsightai.Controller.PropertyController;

import com.propsightai.Dto.PropertyScoreResponse;
import com.propsightai.Model.Property;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.PropertyType;
import com.propsightai.Service.PropertyScoringService;
import com.propsightai.Service.PropertyService;
import com.propsightai.Service.PredictionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.log;

@Slf4j
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertiesController {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
   private  PropertyService propertyService;
    @Autowired
    private PropertyScoringService scoringService;
    @Autowired
    private PredictionService predictionService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> CreateProperty(
            @Valid @RequestPart("property") com.propsightai.Dto.PropertyCreateDto propertyDto,
            @RequestPart("images") List<MultipartFile> images)

    {
        PropertiesController.log.info("Creating property: {}", propertyDto);
        // Restrict number of images
        if (images.size() < 1 || images.size() > 4) {  // change 4 to 8 if needed
            return ResponseEntity.badRequest()
                    .body("You must upload between 1 and 4 images for a property.");
        }
        try {
            Property savedProperty = propertyService.SavePropertyFromDto(propertyDto, images);
            // map to response DTO
            com.propsightai.Dto.PropertyResponseDto resp = mapToDto(savedProperty);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Property creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/getAllProperties")
    public List<com.propsightai.Dto.PropertyResponseDto> getAllProperties()
    {
        return this.propertyService.getProperties().stream().map(this::mapToDto).collect(java.util.stream.Collectors.toList());
    }

    // New paginated endpoint
    @GetMapping("")
    public org.springframework.data.domain.Page<com.propsightai.Dto.PropertyResponseDto> listProperties(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) com.propsightai.Role.PropertyType propertyType,
            @RequestParam(required = false) com.propsightai.Role.PurposeType purpose,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            org.springframework.data.domain.Pageable pageable
    ) {
        org.springframework.data.domain.Page<com.propsightai.Model.Property> page = propertyService.searchProperties(cityId, propertyType, purpose, minPrice, maxPrice, pageable);
        return page.map(this::mapToDto);
    }

    @GetMapping("/property-types")
    public ResponseEntity<PropertyType[]> getPropertyTypes() {
        return ResponseEntity.ok(PropertyType.values());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<com.propsightai.Dto.PropertyResponseDto>> getPropertiesByTypePath(
            @PathVariable String type) {

        PropertyType propertyType = PropertyType.valueOf(type.toUpperCase());

        List<com.propsightai.Model.Property> properties = propertyService.getByType(propertyType);

        return ResponseEntity.ok(properties.stream().map(this::mapToDto).collect(java.util.stream.Collectors.toList()));
    }



    @GetMapping("/id/{id}")
    public ResponseEntity<com.propsightai.Dto.PropertyResponseDto> getPropertyById(@PathVariable Integer id)
    {
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(p -> ResponseEntity.ok(mapToDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }




//ai score
@GetMapping("/{id}/score")
public PropertyScoreResponse getScore(@PathVariable Integer id) {
    Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));

    return scoringService.calculateScore(property);
}

    private com.propsightai.Dto.PropertyResponseDto mapToDto(Property p) {
        com.propsightai.Dto.PropertyResponseDto dto = new com.propsightai.Dto.PropertyResponseDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setPurpose(p.getPurpose());
        dto.setPropertyType(p.getPropertyType());
        dto.setImages(p.getImages().stream().map(com.propsightai.Model.Image::getCloudinary_src).collect(java.util.stream.Collectors.toList()));
        dto.setCity(p.getCity() != null ? p.getCity().getName() : null);
        dto.setLocation(p.getLocation());
        dto.setBathrooms(p.getBathrooms());
        dto.setBedrooms(p.getBedrooms());
        dto.setAddress(p.getAddress());
        dto.setIsAvailable(p.getAvailable());
        dto.setFeatured(p.getFeatured());
        dto.setAuctionEnabled(p.getAuctionEnabled());
        dto.setViewsCount(p.getViewsCount());
        dto.setApproved(p.getApproved());
        dto.setSold(p.getSold());
        dto.setCreatedAt(p.getCreatedAt());
        
        // Add AI prediction
        try {
            dto.setPrediction(predictionService.predictProperty(p));
        } catch (Exception e) {
            PropertiesController.log.warn("Failed to generate price prediction for property {}: {}", p.getId(), e.getMessage());
            // Prediction is optional, don't fail if it errors
        }
        
        // Add property score
        try {
            dto.setScore(scoringService.calculateScore(p));
        } catch (Exception e) {
            PropertiesController.log.warn("Failed to calculate score for property {}: {}", p.getId(), e.getMessage());
            // Score is optional, don't fail if it errors
        }
        
        return dto;
    }
}
