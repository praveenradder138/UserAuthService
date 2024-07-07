package com.project.userauthservice.security.models;

import com.project.userauthservice.models.Role;
import com.project.userauthservice.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerUserDetails implements UserDetails {

    private String email;
    private List<GrantedAuthority> grantedAuthorities;
    private String password;


    public CustomerUserDetails(User user){
        this.email=user.getEmail();
        this.password=user.getPassword();
        this.grantedAuthorities=new ArrayList<>();
        for(Role role : user.getRoles()){
            grantedAuthorities.add(new CustomGrantedAuthority(role.getValue()));
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
