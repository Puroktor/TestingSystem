package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.mapper.UserMapper;
import com.dsr.practice.testingsystem.service.UserAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Api(tags = "User authorisation API")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @ApiOperation(value = "Creates new user if absent")
    @PostMapping("user")
    public ResponseEntity<UserRegistrationDto> createUser(@RequestBody @ApiParam(value = "Your registration info")
                                                                  UserRegistrationDto userRegistrationDto) {
        User user = UserMapper.toEntity(userRegistrationDto);
        User newUser = userAuthService.createUser(user);
        userRegistrationDto.setId(newUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userRegistrationDto);
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("login")
    public ResponseEntity<JwtTokensDto> loginUser(@RequestBody @ApiParam(value = "Your login info") UserLoginDto userLoginDto) {
        User user = UserMapper.toEntity(userLoginDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthService.loginUser(user));
    }

    @ApiOperation(value = "Returns JWT access and refresh tokens if OK")
    @PostMapping("token/refresh")
    public ResponseEntity<JwtTokensDto> refreshToken(@RequestBody @ApiParam(value = "Your login info") String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAuthService.refreshToken(refreshToken));
    }
}
