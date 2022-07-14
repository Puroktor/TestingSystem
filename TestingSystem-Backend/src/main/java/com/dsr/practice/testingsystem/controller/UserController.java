package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.*;
import com.dsr.practice.testingsystem.service.ActionsService;
import com.dsr.practice.testingsystem.service.UserAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Validated
@Api(tags = "User API")
public class UserController {
    private final UserAuthService userAuthService;
    private final ActionsService actionsService;

    @ApiOperation(value = "Creates new user if absent")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("user")
    public ResponseEntity<RegistrationResponseDto> createUser(@RequestBody @Valid @ApiParam(value = "Your registration info")
                                                                      UserRegistrationDto userRegistrationDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAuthService.createUser(userRegistrationDto));
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("login")
    public ResponseEntity<JwtTokensDto> loginUser(@RequestBody @Valid @ApiParam(value = "Your login info")
                                                          UserLoginDto userLoginDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthService.loginUser(userLoginDto));
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("token/refresh")
    public ResponseEntity<JwtTokensDto> refreshToken(@RequestBody @NotBlank(message = "Enter your token")
                                                     @ApiParam(value = "Your login info") String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthService.refreshToken(refreshToken));
    }

    @ApiOperation(value = "Checks your test")
    @PostMapping("submit")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<Void> submitAttempt(@RequestBody @NotNull(message = "Provide answers")
                                              @Size(min = 1, message = "Provide at least one answer")
                                              @ApiParam(value = "Your answers") List<AnswerDto> answers,
                                              @RequestParam("userId") @ApiParam(value = "Your user id", example = "1")
                                                      int userId) {
        actionsService.submitAttempt(answers, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @ApiOperation(value = "Returns requested page of leaderboard")
    @GetMapping("leaderboard")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<LeaderboardPageDto> getLeaderboardPage(
            @RequestParam("index") @Min(value = 0, message = "Index must be >=0")
            @ApiParam(value = "Index of desired page", example = "1") int index,
            @RequestParam("size") @Min(value = 1, message = "Page size must be >=1")
            @ApiParam(value = "Size of pages", example = "1") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(actionsService.getLeaderboardPage(index, size));
    }
}
