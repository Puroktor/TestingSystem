package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.User;

import java.util.ArrayList;

public class UserMapper {
    public static User toEntity(UserRegistrationDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getRole(), dto.getUniversity(),
                dto.getYear(), dto.getGroupNumber(), dto.getEmail(), new ArrayList<>());
    }

    public static User toEntity(UserLoginDto userLoginDto) {
        User user = new User();
        user.setNickname(userLoginDto.getNickname());
        user.setPassword(userLoginDto.getPassword());
        return user;
    }

}
