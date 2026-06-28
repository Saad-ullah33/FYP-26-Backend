package com.propsightai.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.propsightai.Model.City;
import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import com.propsightai.Repository.CityRepository;
import com.propsightai.Repository.ImageRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.PropertyType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        for (MultipartFile image : images) {
            imageServiceImpl.uploadImage(image, savedProperty);
        }

        return savedProperty;
    }

    @Override
    public void deletePropertyById(Integer id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

       try{
           // Delete images from Cloudinary
           for (Image image : property.getImages()) {
               cloudinary.uploader().destroy(image.getCloud_id(), ObjectUtils.emptyMap());
           }
       } catch (Exception e) {
           throw new RuntimeException("Failed to delete",e);
       }

        // Delete from database
        propertyRepository.delete(property);
    }


    @Override
    public List<Property> getByType(PropertyType type) {
        return propertyRepository.findByPropertyType(type);
    }
}
