package com.manager.Task_Springboot.services.auth;

import com.manager.Task_Springboot.dto.SignupRequest;
import com.manager.Task_Springboot.dto.UserDto;

public interface AuthService {

    UserDto signupUser(SignupRequest signupRequest);

    boolean hasUseWithEmail(String email);
}
