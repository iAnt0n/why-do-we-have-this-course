package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    private UUID id;
    private String name;
    private String password;
    private String role;
}
