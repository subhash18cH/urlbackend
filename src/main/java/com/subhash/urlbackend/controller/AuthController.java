package com.subhash.urlbackend.controller;

import com.subhash.urlbackend.model.Userr;
import com.subhash.urlbackend.repository.UserRepository;
import com.subhash.urlbackend.request.LoginRequest;
import com.subhash.urlbackend.request.SignUpRequest;
import com.subhash.urlbackend.response.LoginResponse;
import com.subhash.urlbackend.response.MessageResponse;
import com.subhash.urlbackend.response.UserInfoResponse;
import com.subhash.urlbackend.security.jwt.JwtUtils;
import com.subhash.urlbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest){

        if(userRepository.existsByUserName(signUpRequest.getUserName())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
        }

        Userr user = new Userr(signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User Registered successfully!!"));
    }

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication=authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();

        String jwtToken=jwtUtils.generateToken(userDetails);

        LoginResponse response=new LoginResponse(jwtToken,userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?>getUserDetails(@AuthenticationPrincipal UserDetails userDetails){
        Userr user=userService.findByUserName(userDetails.getUsername());
        UserInfoResponse response=new UserInfoResponse(user.getId(),user.getUserName(),user.getEmail());
        return ResponseEntity.ok().body(response);
    }

}

