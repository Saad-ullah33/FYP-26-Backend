package com.propsightai.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import com.propsightai.Repository.ImageRepository;
import com.propsightai.Repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Property SaveProperty(Property property,List<MultipartFile> images) {
        Property savedProperty = propertyRepository.save(property);
        for (MultipartFile image : images) {
            imageServiceImpl.uploadImage(image,savedProperty);
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
}
