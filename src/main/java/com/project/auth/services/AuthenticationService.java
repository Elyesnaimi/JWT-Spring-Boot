package com.project.auth.services;


import com.project.auth.domain.Role;
import com.project.auth.domain.RoleName;
import com.project.auth.domain.User;
import com.project.auth.dtos.JwtResponse;
import com.project.auth.dtos.UserCredentialsDto;
import com.project.auth.dtos.UserSignUpDto;
import com.project.auth.repository.RoleRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.jwt.JwtProvider;
import com.project.auth.security.services.UserPrinciple;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.project.auth.domain.RoleName.ROLE_ADMIN;
import static com.project.auth.domain.RoleName.ROLE_USER;
import static java.lang.String.format;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder encoder,
                                 JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }

    public JwtResponse authenticateUser(UserCredentialsDto userCredentialsDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCredentialsDto.getUsername(), userCredentialsDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return new JwtResponse(jwt,
                userPrinciple.getUsername(),
                userPrinciple.getAuthorities(),
                userPrinciple.getId());
    }

    @Transactional
    public JwtResponse registerUser(UserSignUpDto userSignUpDto) {
        if (userRepository.findByUsername(userSignUpDto.getUsername()).isPresent()) {
            throw new RuntimeException(format("user with username %s already exists", userSignUpDto.getUsername()));
        }
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new RuntimeException(format("user with email %s already exists", userSignUpDto.getEmail()));
        }

        User user = new User(
                userSignUpDto.getFirstname(),
                userSignUpDto.getLastname(),
                userSignUpDto.getUsername(),
                userSignUpDto.getEmail(),
                encoder.encode(userSignUpDto.getPassword()),
                userSignUpDto.getAddress()
        );
        if (!userSignUpDto.getRoles().isEmpty()) {
            user.setRoles(associateRolesToUser(userSignUpDto.getRoles()));
        }
        userRepository.save(user);
        return this.authenticateUser(userSignUpDto);
    }

    private Set<Role> associateRolesToUser(Set<RoleName> roles) {
        Set<Role> setOfRoles = new HashSet<>();

        roles.forEach(role -> {
            switch (role) {
                case ROLE_ADMIN:
                    Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Admin Role not found."));
                    setOfRoles.add(adminRole);

                    break;
                case ROLE_USER:
                    Role userRole = roleRepository.findByName(ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not found."));
                    setOfRoles.add(userRole);
                    break;
                default:
                    throw new RuntimeException("Fail ! -> Cause : the type" + role + "is not valid");
            }
        });
        return setOfRoles;
    }
}
