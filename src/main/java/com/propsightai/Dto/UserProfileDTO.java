package com.propsightai.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String address;
    private Boolean notificationsEnabled;
}