package com.project.auth.dtos;


public class UserCredentialsDto {

    private String username;
    private String password;

    protected UserCredentialsDto() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
