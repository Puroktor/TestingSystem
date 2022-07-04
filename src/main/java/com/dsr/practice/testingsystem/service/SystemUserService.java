package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.entity.SystemUser;
import com.dsr.practice.testingsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
public class SystemUserService {
    private final SystemUserRepository systemUserRepository;

    public SystemUser createUser(@Valid SystemUser user) {
        return systemUserRepository.save(user);
    }

    public SystemUser getUser(Integer id) {
        return systemUserRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }
}
