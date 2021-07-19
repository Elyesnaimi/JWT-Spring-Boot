package com.project.auth.dtos;


import com.project.auth.domain.RoleName;

import java.util.Set;

public class UserSignUpDto extends UserCredentialsDto {

    private String firstname;
    private String lastname;
    private String email;
    private Set<RoleName> roles;
    private String address;

    private UserSignUpDto() {
        super();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public Set<RoleName> getRoles() {
        return roles;
    }

    public String getAddress() {
        return address;
    }
}