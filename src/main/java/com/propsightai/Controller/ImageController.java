package com.propsightai.Controller;

import com.propsightai.Service.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageServiceImpl imageService;


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Integer id){
        imageService.deleteImage(id);
        return ResponseEntity.ok("Image deleted successfully");
    }
}
