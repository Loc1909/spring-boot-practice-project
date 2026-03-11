package com.ou.springcode.service;

import java.util.List;

import com.ou.springcode.dto.UserPatchRequest;
import org.springframework.http.ResponseEntity;

import com.ou.springcode.dto.UserReponse;
import com.ou.springcode.dto.UserRequest;

public interface IUserService {
    List<UserReponse> findAll();
    ResponseEntity<UserReponse> findById(Long id);
    ResponseEntity<UserReponse> create(UserRequest request);
    ResponseEntity<UserReponse> update(Long id, UserRequest request);
    ResponseEntity<UserReponse> patchUpdate(Long id, UserPatchRequest request);
    boolean deleteById(Long id);
}
