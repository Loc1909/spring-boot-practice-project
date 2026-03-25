package com.ou.springcode.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ou.springcode.dto.UserResponse;
import com.ou.springcode.entity.Role;
import com.ou.springcode.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<Page<UserResponse>> list(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Role role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "username") String sort,
        @RequestParam(defaultValue = "asc") String order
    ) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC; 
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(direction, sort));
        Page<UserResponse> users = userService.findAll(search, role, pageable);

        return ResponseEntity.ok(users);
    }
    
}
