package ru.itmo.lab1.user_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.user_service.controller.exception.UserNotFoundException;
import ru.itmo.lab1.user_service.model.User;
import ru.itmo.lab1.user_service.model.dto.UserDto;
import ru.itmo.lab1.user_service.model.dto.UserRegDto;
import ru.itmo.lab1.user_service.repository.UserRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    public Page<UserDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(user -> modelMapper.map(user, UserDto.class));
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id).map(market -> modelMapper.map(market, UserDto.class)).orElseThrow(
                () -> new UserNotFoundException(id)
        );
    }

    public void delete(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public boolean register(UserRegDto userRegDto) {
        if (userRepository.existsByName(userRegDto.getName())) {
            return false;
        }
        User user = modelMapper.map(userRegDto, User.class);
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleted(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return new UserDetailsImpl(user);
    }
}
