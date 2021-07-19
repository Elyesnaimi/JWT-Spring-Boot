package com.project.auth.ws;

import com.project.auth.dtos.JwtResponse;
import com.project.auth.dtos.UserCredentialsDto;
import com.project.auth.dtos.UserSignUpDto;
import com.project.auth.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationWebService {

    private final AuthenticationService authenticationService;

    public AuthenticationWebService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody UserCredentialsDto userCredentialsDto) {
        JwtResponse jwtResponse = this.authenticationService.authenticateUser(userCredentialsDto);
        return ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            JwtResponse jwtResponse = this.authenticationService.registerUser(userSignUpDto);
            return new ResponseEntity<>(jwtResponse, CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }
}