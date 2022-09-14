package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.controller.exception.UserNotFoundException;
import ru.itmo.lab1.model.User;
import ru.itmo.lab1.model.dto.UserDto;
import ru.itmo.lab1.repository.UserRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    public Page<UserDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(user -> modelMapper.map(user, UserDto.class));
    }

    public void delete(UUID id) {
        User market = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(market);
    }

    public boolean register(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            return false;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}
