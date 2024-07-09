package com.project.userauthservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAccount extends BaseModel{

    private String provider;
    private String providerId;
    @ManyToOne
    private User user;

}
