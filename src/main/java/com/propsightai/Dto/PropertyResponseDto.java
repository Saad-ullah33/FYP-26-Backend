package com.propsightai.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyResponseDto {
    private Integer id;
    private String title;
    private String description;
    private Double price;
    private PurposeType purpose;
    private PropertyType propertyType;
    private List<String> images; // urls
    private String city;
    private String location;
    private Integer bathrooms;
    private Integer bedrooms;
    private String address;
    private Boolean isAvailable;
    private Boolean featured;
    private Boolean auctionEnabled;
    private Integer viewsCount;
    private Boolean approved;
    private Boolean sold;
    private LocalDateTime createdAt;
    private PredictionResponse prediction; // AI price prediction
    private PropertyScoreResponse score; // Property scoring
}