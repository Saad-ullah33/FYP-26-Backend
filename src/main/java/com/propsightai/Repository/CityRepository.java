package com.propsightai.Repository;

import com.propsightai.Model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByName(String name);
    Optional<City> findByNameIgnoreCase(String name);
}