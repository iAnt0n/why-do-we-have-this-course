package ru.itmo.lab1.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.user_service.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByName(String name);

    boolean existsByName(String name);
}
