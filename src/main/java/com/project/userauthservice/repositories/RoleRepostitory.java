package com.project.userauthservice.repositories;

import com.project.userauthservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepostitory extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(String value);

}
