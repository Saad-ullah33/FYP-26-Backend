package com.propsightai.Service;

import com.propsightai.Model.Property;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getProperties();
    Optional <Property> getPropertyById(Integer id);


    void deleteProperty(Integer id);
    Property SaveProperty(Property property,List<MultipartFile> images);
    void deletePropertyById(Integer id);

}
