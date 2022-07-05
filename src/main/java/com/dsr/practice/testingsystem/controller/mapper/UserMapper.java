package com.dsr.practice.testingsystem.controller.mapper;

import com.dsr.practice.testingsystem.controller.dto.UserLoginDto;
import com.dsr.practice.testingsystem.controller.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.User;

public class UserMapper {
    public static User toEntity(UserRegistrationDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getNickname(), dto.getPasswordHash(), dto.getUniversity(),
                dto.getYear(), dto.getGroupNumber(), dto.getEmail(), null);
    }

    public static User toEntity(UserLoginDto userLoginDto) {
        User user = new User();
        user.setNickname(userLoginDto.getNickname());
        user.setPasswordHash(userLoginDto.getPasswordHash());
        return user;
    }

}
