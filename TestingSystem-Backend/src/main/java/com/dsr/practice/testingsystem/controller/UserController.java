package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.FullTestDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.mapper.UserMapper;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;

    @PostMapping("user")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        User user = UserMapper.toEntity(userRegistrationDto);
        User newUser = userService.createUser(user);
        userRegistrationDto.setId(newUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userRegistrationDto);
    }

    @GetMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
        User user = UserMapper.toEntity(userLoginDto);
        Integer id = userService.loginUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PostMapping("submit")
    public ResponseEntity<?> submitAttempt(@RequestBody FullTestDto testDto, @RequestParam("student-id") int studentId) {
        Test test = TestMapper.toEntity(testDto);
        userService.submitAttempt(test, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
