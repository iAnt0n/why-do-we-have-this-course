package ru.itmo.lab2.gateway.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab2.gateway.model.enums.Role;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private UUID id;
    private String name;
    private String password;
    private Role role;
    private Boolean deleted = false;
}
