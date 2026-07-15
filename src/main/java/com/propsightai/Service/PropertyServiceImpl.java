package com.propsightai.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.propsightai.Dto.PropertyCreateDto;
import com.propsightai.Dto.PropertyDto;
import com.propsightai.Dto.PropertyResponseDto;
import com.propsightai.Model.City;
import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import com.propsightai.Model.User;
import com.propsightai.Repository.CityRepository;
import com.propsightai.Repository.ImageRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageServiceImpl  imageServiceImpl;
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<Property> getProperties() {
        List<Property> properties = propertyRepository.findAll();
        properties.forEach(property -> {
            List<Image> images = imageRepository.findByProperty(property);
            property.setImages(images); // set the images list
        });
        return properties;
    }

    @Override
    public Optional<Property> getPropertyById(Integer id) {
        return propertyRepository.findById(id);
    }

    @Override
    public void deleteProperty(Integer id) {
        propertyRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Property SaveProperty(Property property, List<MultipartFile> images) {
        // ================= GET LOGGED IN USER =================
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ================= ASSIGN OWNER =================
        property.setOwner(owner);
        Set<String> allowedLocations = Stream.of(
                "Lyallpur Town", "Madina Town", "Jinnah Town", "Iqbal Town", "Chak Jhumra Town",
                "Jaranwala Town", "Samundri Town", "Tandlianwala Town", "Faisalabad City",
                "Faisalabad Sadar", "Chak Jhumra", "Jaranwala", "Samundri", "Tandlianwala",
                "D Ground", "People Colony No 1", "People Colony No 2", "Canal Road",
                "Susan Road", "Wapda City", "FDA City", "Citi Housing", "Gulberg",
                "Samanabad", "Millat Town", "Satiana Road", "Jaranwala Road", "Samundari Road",
                "Jhang Road", "Sargodha Road", "Sheikhupura Road", "Eden Gardens",
                "Eden Valley", "Kohinoor City", "Batala Colony", "Civil Lines", "Officers Colony",
                "Ghulam Muhammad Abad", "D-Type Colony", "Nishatabad", "Gulistan Colony",
                "Manawala", "Khurrianwala", "Dijkot", "Mamu Kanjan", "Satiana", "Makuana",
                "Sadhar", "Garh Fateh Shah", "Rodala Road", "Lundianwala", "Bachiana",
                "Kanjwani", "Salatwala", "Pansra", "Thikriwala", "Dhandra", "Katchery Bazaar",
                "Chiniot Bazaar", "Aminpur Bazaar", "Bhawana Bazaar", "Jhang Bazaar",
                "Montgomery Bazaar", "Karkhana Bazaar", "Rail Bazaar"
        ).map(String::trim).collect(Collectors.toSet());

        // 1. Validate null first
        if (property.getLocation() == null || property.getLocation().trim().isEmpty()) {
            throw new RuntimeException("Location is required");
        }

        // 2. Normalize
        String location = property.getLocation().trim();

        // 3. Allowed list
        if (!allowedLocations.contains(location)) {
            throw new RuntimeException("Invalid Faisalabad Location: " + location);
        }

        // 4. City check
        City city = cityRepository.findById(property.getCity().getId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        property.setCity(city);

        if (!"Faisalabad".equalsIgnoreCase(city.getName())) {
            throw new RuntimeException("Only Faisalabad allowed");
        }

        // SAVE PROPERTY
        Property savedProperty = propertyRepository.save(property);

        // IMAGE UPLOAD
        if (images != null) {
            for (MultipartFile image : images) {
                imageServiceImpl.uploadImage(image, savedProperty);
            }
        }

        return savedProperty;
    }

    @Override
    public void deletePropertyById(Integer id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        try {
            // Delete images from Cloudinary
            for (Image image : property.getImages()) {
                cloudinary.uploader().destroy(image.getCloud_id(), ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Cloudinary assets", e);
        }

        // Delete from database
        propertyRepository.delete(property);
    }

    @Override
    public List<Property> getByType(PropertyType type) {
        return propertyRepository.findByPropertyType(type);
    }

    // ================= new methods =================
    @Transactional
    @Override
    public Property SavePropertyFromDto(PropertyCreateDto dto, List<MultipartFile> images) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setPurpose(dto.getPurpose());
        property.setPropertyType(dto.getPropertyType());
        property.setLocation(dto.getLocation());
        property.setBathrooms(dto.getBathrooms());
        property.setArea(dto.getArea());
        property.setBedrooms(dto.getBedrooms());
        property.setAddress(dto.getAddress());
        property.setAuctionEnabled(dto.getAuctionEnabled() != null ? dto.getAuctionEnabled() : false);

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        property.setCity(city);

        return SaveProperty(property, images);
    }

    @Override
    public Page<Property> searchProperties(Long cityId, PropertyType propertyType, PurposeType purpose, Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Property> page = propertyRepository.searchProperties(cityId, propertyType, purpose, minPrice, maxPrice, pageable);
        // Ensure images loaded
        page.getContent().forEach(p -> p.setImages(imageRepository.findByProperty(p)));
        return page;
    }

    @Override
    public int countByUser(Integer userId) {
        return propertyRepository.countByOwner_Id(userId);
    }

    @Override
    public List<PropertyDto> getPropertiesByUser(Integer userId) {
        List<Property> properties = propertyRepository.findByOwner_Id(userId);
        return properties.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public void deletePropertyByIdAndUser(Long id, Integer userId) {
        Integer propertyId = id.intValue();

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized transaction entry.");
        }
        // 1. Safe Media Cache Purge on Cloudinary Nodes
        try {
            for (Image img : property.getImages()) {
                if (img.getCloud_id() != null && !img.getCloud_id().isEmpty()) {
                    cloudinary.uploader().destroy(img.getCloud_id(), ObjectUtils.emptyMap());
                }
            }
        } catch (Exception e) {
            System.err.println("Non-blocking Cloudinary cleanup warning: " + e.getMessage());
        }

        // 2. NEW FIX: Delete dependent foreign references out of your database tables manually
        // You can inject a PredictionRepository or execute a direct native query execution:
        try {
            // Example if using a native query executor via your existing repository layer:
            propertyRepository.deletePredictionRecordsByPropertyId(propertyId);

            // Also check if your Auctions table has an un-cascaded binding:
            propertyRepository.deleteAuctionRecordsByPropertyId(propertyId);
        } catch (Exception e) {
            System.err.println("Reference cleanup trace note: " + e.getMessage());
        }

        // 3. Clear Parent Record cleanly now that all child foreign constraints are gone
        propertyRepository.delete(property);
    }

    @Transactional
    @Override
    public void enableAuctionForUserProperty(Long propertyId, Integer userId) {
        Property property = propertyRepository.findById(propertyId.intValue())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Ownership validation mismatch.");
        }

        // Shift feature toggle bit
        property.setAuctionEnabled(true);
        propertyRepository.save(property);
    }

    @Transactional
    @Override
    public Property updatePropertyFromDto(Integer id, com.propsightai.Dto.PropertyCreateDto dto, List<MultipartFile> images, Integer userId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property entity target not found"));

        if (!property.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Modification context rejected.");
        }

        // Overwrite standard mutable payload definitions
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setPurpose(dto.getPurpose());
        property.setPropertyType(dto.getPropertyType());
        property.setLocation(dto.getLocation());
        property.setBathrooms(dto.getBathrooms());
        property.setBedrooms(dto.getBedrooms());
        property.setArea(dto.getArea());
        property.setAddress(dto.getAddress());

        // Process images safely if new overwrite files are attached
        if (images != null && !images.isEmpty()) {
            try {
                // Wipe previous image mappings from Cloudinary nodes
                for (Image img : property.getImages()) {
                    if (img.getCloud_id() != null) {
                        cloudinary.uploader().destroy(img.getCloud_id(), ObjectUtils.emptyMap());
                    }
                }
                property.getImages().clear();

                // Load updated asset paths
                for (MultipartFile file : images) {
                    imageServiceImpl.uploadImage(file, property);
                }
            } catch (Exception e) {
                throw new RuntimeException("Media replacement pipeline error: " + e.getMessage());
            }
        }

        return propertyRepository.save(property);
    }

    @Override
    public void verifyPropertyOwnership(Integer id, Integer id1) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getId().equals(id1)) {
            throw new RuntimeException("Unauthorized: Ownership validation mismatch.");
        }
    }

    private PropertyDto mapToDto(Property property) {
        PropertyDto dto = new PropertyDto();

        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setPrice(property.getPrice());
        dto.setLocation(property.getLocation());

        // 1. Dynamic Status Resolution matching your frontend layout logic
        if (Boolean.TRUE.equals(property.getSold())) {
            dto.setStatus("SOLD");
        } else if (Boolean.TRUE.equals(property.getApproved())) {
            dto.setStatus("APPROVED");
        } else {
            dto.setStatus("PENDING"); // Default fallback state
        }

        // 2. Map PropertyType enum safely to string
        dto.setType(property.getPropertyType() != null ? property.getPropertyType().name() : "N/A");

        // 3. Map City model name safely
        if (property.getCity() != null) {
            dto.setCity(property.getCity().getName());
        } else {
            dto.setCity("Faisalabad");
        }

        return dto;
    }
}