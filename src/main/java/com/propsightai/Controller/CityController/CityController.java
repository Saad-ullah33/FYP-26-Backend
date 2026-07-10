package com.propsightai.Controller.CityController;

import com.propsightai.Model.City;
import com.propsightai.Service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin
public class CityController {

    @Autowired
    private CityService cityService;

    @PostMapping
    public com.propsightai.Model.City addCity(@Valid @RequestBody com.propsightai.Dto.CityDto cityDto) {
        com.propsightai.Model.City city = new com.propsightai.Model.City();
        city.setName(cityDto.getName());
        return cityService.addCity(city);
    }

    @GetMapping
    public List<com.propsightai.Model.City> getAllCities() {
        return cityService.getAllCities();
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }
}