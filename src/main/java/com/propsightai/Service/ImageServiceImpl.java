package com.propsightai.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import com.propsightai.Repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;
    @Override
    public List<Image> getAllImages() {
        return  imageRepository.findAll();
    }

    @Override
    public Optional<Image> getImageById(Integer id) {
        return imageRepository.findById(id);
    }

    @Override
    public Image uploadImage(MultipartFile file, Property property) {
        try {

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());


            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            Image image = new Image();
            image.setCloudinary_src(imageUrl);
            image.setCloud_id(publicId);
            image.setProperty(property);

            return imageRepository.save(image);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed");
        }
    }

    @Override
    public void updateImage(Image image, MultipartFile file, Property property) {
        if (file==null|| file.isEmpty()){
            throw new RuntimeException("File Cannot Found!");
        }

        try{
            //first upload new image
            Map uploadResult=cloudinary.uploader()
                    .upload(file.getBytes(),ObjectUtils.emptyMap());
            String newUrl=uploadResult.get("secure_url").toString();
            String newPublicId=uploadResult.get("public_id").toString();


            //delete the old one image
            if (image.getCloud_id()!=null){
                cloudinary.uploader()
                        .destroy(image.getCloud_id(),ObjectUtils.emptyMap());
            }
            //update image in db
            image.setCloudinary_src(newUrl);
            image.setCloud_id(newPublicId);
            image.setProperty(property);

            imageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException("Failed To Upload Image",e);
        } catch (Exception e) {
            throw new RuntimeException("Failed To Update Image ",e);
        }
    }

    @Override
    public void deleteImage(Integer id) {
        Image image=imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        try{
            //delete image from cloudinary
            cloudinary.uploader().destroy(image.getCloud_id(),ObjectUtils.emptyMap());
            //delete from db
            imageRepository.delete(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed To Delete Image From Cloudinary",e);
        }
        this.imageRepository.deleteById(id);
    }

}

