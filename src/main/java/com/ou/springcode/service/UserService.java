package com.ou.springcode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.springcode.dto.UserRequest;
import com.ou.springcode.dto.UserResponse;
import com.ou.springcode.entity.Role;
import com.ou.springcode.entity.User;
import com.ou.springcode.exception.DuplicateResourceException;
import com.ou.springcode.exception.ResourceNotFoundException;
import com.ou.springcode.repository.UserRepository;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(String search, Role role, Pageable pageable) {
        Page<User> users = userRepository.findAllBySearchAndRole(search, role, pageable);

        return users.map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", id));

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        validateCreate(request);

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build();

        User savedUser = userRepository.save(user);
        log.info("Created user id={}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        validateUpdate(id, request);

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        log.info("Updated user id={}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        userRepository.delete(user);
        log.info("Deleted user id={}", id);
    }

    private void validateCreate(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username đã tồn tại: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email đã tồn tại: " + request.email());
        }
    }

    private void validateUpdate(Long id, UserRequest request) {
        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username đã tồn tại: " + request.username());
        }

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email đã tồn tại: " + request.email());
        }
    }
}
