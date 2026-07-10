package com.propsightai.Dto;

import lombok.Data;

@Data
public class PropertyDto {
    private Integer id;
    private String title;
    private Double price;
    private String location;
    private String status;  // Checked by item.status inside React
    private String type;    // Checked by item.type inside React
    private String city;    // Checked by item.city inside React
}