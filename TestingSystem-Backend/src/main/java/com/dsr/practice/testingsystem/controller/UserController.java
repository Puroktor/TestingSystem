package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.mapper.AnswerMapper;
import com.dsr.practice.testingsystem.mapper.UserMapper;
import com.dsr.practice.testingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
        User user = UserMapper.toEntity(userLoginDto);
        Integer id = userService.loginUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PostMapping("submit")
    public ResponseEntity<?> submitAttempt(@RequestBody AnswerDto[] answerDtos, @RequestParam("userId") int userId) {
        List<Answer> answers = Arrays.stream(answerDtos).map(AnswerMapper::toEntity).collect(Collectors.toList());
        userService.submitAttempt(answers, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getLeaderboard());
    }
}
