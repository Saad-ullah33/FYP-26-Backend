package com.propsightai.Service;

import com.propsightai.Model.City;
import com.propsightai.Repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public City addCity(City city) {
        if (cityRepository.existsByName(city.getName())) {
            throw new RuntimeException("City already exists");
        }
        return cityRepository.save(city);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }
}