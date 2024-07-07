package com.project.userauthservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class OAuthAccount extends BaseModel{

    private String provider;
    private String providerId;
    @ManyToOne
    private User user;

}
