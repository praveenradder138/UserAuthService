package com.project.userauthservice.dtos;

import com.project.userauthservice.models.Role;
import com.project.userauthservice.models.User;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String name;
    private String email;
    private List<Role> roles;

    public static UserDto from(User user) {
        if(user==null){
            return null;
        }
        return UserDto.builder().name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles()).build();
    }
}
