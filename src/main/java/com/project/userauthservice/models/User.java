package com.project.userauthservice.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
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
