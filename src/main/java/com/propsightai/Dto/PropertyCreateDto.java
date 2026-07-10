package com.propsightai.Dto;

import com.propsightai.Role.PurposeType;
import com.propsightai.Role.PropertyType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyCreateDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Positive
    private Double price;


    @NotNull
    private PurposeType purpose;

    @NotNull
    private PropertyType propertyType;

    @NotBlank
    private String location;

    @NotNull
    @Positive
    private Integer bathrooms;

    @NotNull
    @Positive
    private Integer bedrooms;

    @NotBlank
    private String address;

    @NotBlank
    private String area;

    @NotNull
    private Long cityId;

    private Boolean auctionEnabled = false;
}