package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.controller.dto.SystemUserDto;
import com.dsr.practice.testingsystem.controller.mapper.UserMapper;
import com.dsr.practice.testingsystem.entity.SystemUser;
import com.dsr.practice.testingsystem.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UserController {
    private final SystemUserService systemUserService;

    @PostMapping("user")
    public ResponseEntity<?> createUser(@RequestBody SystemUserDto systemUserDto) {
        SystemUser user = UserMapper.toEntity(systemUserDto);
        SystemUser newUser = systemUserService.createUser(user);
        systemUserDto.setId(newUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(systemUserDto);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserMapper.toDto(systemUserService.getUser(id)));
    }
}
