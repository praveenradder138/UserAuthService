package com.project.userauthservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseModel   {

    private String name;
    private String email;
    private String password;
    private boolean isEmailVerified;
    private String emailVerificationToken;
    private Date emailTokenExpirationTime;

    @OneToMany(mappedBy = "user")
    private List<OAuthAccount> oAuthAccounts;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<Role> roles;


}
