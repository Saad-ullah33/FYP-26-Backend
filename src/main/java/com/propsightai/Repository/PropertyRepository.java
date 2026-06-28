package com.propsightai.Repository;

import com.propsightai.Model.Property;
import com.propsightai.Role.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property,Integer> {


    List<Property> findByPropertyTypeAndTrendingTrue(PropertyType type);

    List<Property> findByPropertyTypeAndFeaturedTrue(PropertyType type);

    List<Property> findByPropertyTypeOrderByCreatedAtDesc(PropertyType type);


    List<Property> findByPropertyType(PropertyType propertyType);
}
