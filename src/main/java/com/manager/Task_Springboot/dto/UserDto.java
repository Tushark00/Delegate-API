package com.manager.Task_Springboot.dto;

import com.manager.Task_Springboot.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}
