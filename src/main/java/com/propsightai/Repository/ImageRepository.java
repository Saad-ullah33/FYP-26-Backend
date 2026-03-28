package com.propsightai.Repository;

import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByProperty(Property property);

}
