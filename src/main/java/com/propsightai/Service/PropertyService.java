package com.propsightai.Service;

import com.propsightai.Dto.PropertyCreateDto;
import com.propsightai.Dto.PropertyDto;
import com.propsightai.Model.Property;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getProperties();
    Optional <Property> getPropertyById(Integer id);


    void deleteProperty(Integer id);
    Property SaveProperty(Property property,List<MultipartFile> images);
    void deletePropertyById(Integer id);

    List<Property> getByType(PropertyType type);

    // New DTO-friendly method
    Property SavePropertyFromDto(PropertyCreateDto dto, List<MultipartFile> images);

    // Search with filtering + pagination
    Page<Property> searchProperties(Long cityId, PropertyType propertyType, PurposeType purpose, Double minPrice, Double maxPrice, Pageable pageable);

    int countByUser(Integer userId);

    List<PropertyDto> getPropertiesByUser(Integer userId);


    void deletePropertyByIdAndUser(Long id, Integer id1);

    void enableAuctionForUserProperty(Long propertyId, Integer userId);
    Property updatePropertyFromDto(Integer id, com.propsightai.Dto.PropertyCreateDto dto, List<MultipartFile> images, Integer userId);
}
