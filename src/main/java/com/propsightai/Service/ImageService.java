package com.propsightai.Service;

import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<Image> getAllImages();

    Optional<Image> getImageById(Integer id);

    public Image uploadImage(MultipartFile file, Property property);
    void updateImage(Image image,MultipartFile file, Property property);

    void deleteImage(Integer id);
}
