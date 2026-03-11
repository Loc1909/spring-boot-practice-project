package com.ou.springcode.service;

import java.util.List;

import com.ou.springcode.dto.UserPatchRequest;
import org.springframework.http.ResponseEntity;

import com.ou.springcode.dto.UserResponse;
import com.ou.springcode.dto.UserRequest;

public interface IUserService {
    List<UserResponse> findAll();
    ResponseEntity<UserResponse> findById(Long id);
    ResponseEntity<UserResponse> create(UserRequest request);
    ResponseEntity<UserResponse> update(Long id, UserRequest request);
    ResponseEntity<UserResponse> patchUpdate(Long id, UserPatchRequest request);
    boolean deleteById(Long id);
}
