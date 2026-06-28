package com.propsightai.Controller.PropertyController;

import com.propsightai.Dto.PropertyScoreResponse;
import com.propsightai.Model.Property;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.PropertyType;
import com.propsightai.Service.PropertyScoringService;
import com.propsightai.Service.PropertyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> CreateProperty(
            @Valid @RequestPart("property")Property property,
            @RequestPart("images") List<MultipartFile> images)
    {
        // Restrict number of images
        if (images.size() < 1 || images.size() > 4) {  // change 4 to 8 if needed
            return ResponseEntity.badRequest()
                    .body("You must upload between 1 and 4 images for a property.");
        }
        try {
            Property savedProperty = propertyService.SaveProperty(property, images);
            return ResponseEntity.ok(savedProperty);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Property creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/getAllProperties")
    public List<Property> getAllProperties()
    {
        return this. propertyService.getProperties();
    }

    @GetMapping("/property-types")
    public ResponseEntity<PropertyType[]> getPropertyTypes() {
        return ResponseEntity.ok(PropertyType.values());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Property>> getPropertiesByTypePath(
            @PathVariable String type) {

        PropertyType propertyType = PropertyType.valueOf(type.toUpperCase());

        List<Property> properties = propertyService.getByType(propertyType);

        return ResponseEntity.ok(properties);
    }



    @GetMapping("/id/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Integer id)
    {
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok)
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
}
