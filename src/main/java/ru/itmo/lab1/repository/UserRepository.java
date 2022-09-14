package ru.itmo.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}