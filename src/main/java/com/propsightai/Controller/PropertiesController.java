package com.propsightai.Controller;

import com.propsightai.Model.Property;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertiesController {
    @Autowired
    PropertyService propertyService;

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

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Integer id)
    {
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }

}
