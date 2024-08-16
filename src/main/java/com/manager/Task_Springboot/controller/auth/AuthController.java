package com.manager.Task_Springboot.controller.auth;

import com.manager.Task_Springboot.dto.AuthRequest;
import com.manager.Task_Springboot.dto.AuthenticationResponse;
import com.manager.Task_Springboot.dto.SignupRequest;
import com.manager.Task_Springboot.dto.UserDto;
import com.manager.Task_Springboot.entity.User;
import com.manager.Task_Springboot.repositories.UserRepository;
import com.manager.Task_Springboot.services.auth.AuthService;
import com.manager.Task_Springboot.services.jwt.UserService;
import com.manager.Task_Springboot.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {


    private final AuthService authService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUseWithEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User Already exist with this email!!!");
        }

            UserDto createdUserDto = authService.signupUser(signupRequest);

        if (createdUserDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
        }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthRequest authRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()));
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect username or password");
        }
        final UserDetails userDetails= userService.userDetailService().loadUserByUsername(authRequest.getEmail());
        final String jwtToken= jwtUtil.generateToken(userDetails);
        Optional<User> optionalUser =userRepository.findFirstByEmail(authRequest.getEmail());
        AuthenticationResponse authenticationResponse= new AuthenticationResponse();
        if(optionalUser.isPresent()){
            authenticationResponse.setJwt(jwtToken);
            authenticationResponse.setUserId(optionalUser.get().getId());
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
        }
        return authenticationResponse;
    }
}
