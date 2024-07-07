package com.project.userauthservice.security.models;

import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority {

    private String value;

    CustomGrantedAuthority(String value){
        this.value=value;
    }
    @Override
    public String getAuthority() {
        return value;
    }
}
