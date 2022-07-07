package com.dsr.practice.testingsystem.controller;

import com.auth0.jwt.JWT;
import com.dsr.practice.testingsystem.dto.*;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.mapper.AnswerMapper;
import com.dsr.practice.testingsystem.mapper.UserMapper;
import com.dsr.practice.testingsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Api(tags = "User API")
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "Creates new user if absent")
    @PostMapping("user")
    public ResponseEntity<UserRegistrationDto> createUser(@RequestBody @ApiParam(value = "Your registration info")
                                                                  UserRegistrationDto userRegistrationDto) {
        User user = UserMapper.toEntity(userRegistrationDto);
        User newUser = userService.createUser(user);
        userRegistrationDto.setId(newUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userRegistrationDto);
    }

    @ApiOperation(value = "Returns JWT tokens if OK")
    @PostMapping("login")
    public ResponseEntity<JwtTokenDto> loginUser(@RequestBody @ApiParam(value = "Your login info") UserLoginDto userLoginDto) {
        User user = UserMapper.toEntity(userLoginDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.loginUser(user));
    }

    @ApiOperation(value = "Checks your test")
    @PostMapping("submit")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<Void> submitAttempt(@RequestBody @ApiParam(value = "Your answers") AnswerDto[] answerDtos,
                                              @RequestParam("userId") @ApiParam(value = "Your user id", example = "1")
                                                      int userId) {
        List<Answer> answers = Arrays.stream(answerDtos).map(AnswerMapper::toEntity).collect(Collectors.toList());
        userService.submitAttempt(answers, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @ApiOperation(value = "Returns current leaderboard")
    @GetMapping("leaderboard")
    public ResponseEntity<LeaderboardDto> getLeaderboard() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getLeaderboard());
    }
}
