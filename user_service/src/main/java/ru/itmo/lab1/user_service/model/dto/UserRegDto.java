package ru.itmo.lab1.user_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab1.user_service.model.enums.Role;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserRegDto implements Serializable {
    private String name;
    private String password;
    private Role role;
}
