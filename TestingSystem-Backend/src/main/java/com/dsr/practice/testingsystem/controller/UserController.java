package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.*;
import com.dsr.practice.testingsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Validated
@Api(tags = "User API")
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "Creates new user if absent")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("user")
    public ResponseEntity<RegistrationResponseDto> createUser(@RequestBody @Valid @ApiParam(value = "Your registration info")
                                                                      UserRegistrationDto userRegistrationDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRegistrationDto));
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("login")
    public ResponseEntity<JwtTokensDto> loginUser(@RequestBody @Valid @ApiParam(value = "Your login info")
                                                          UserLoginDto userLoginDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.loginUser(userLoginDto));
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("token/refresh")
    public ResponseEntity<JwtTokensDto> refreshToken(@RequestBody @NotBlank(message = "Enter your token")
                                                     @ApiParam(value = "Your login info") String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.refreshToken(refreshToken));
    }
}
