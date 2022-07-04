package com.dsr.practice.testingsystem.controller.mapper;

import com.dsr.practice.testingsystem.controller.dto.SystemUserDto;
import com.dsr.practice.testingsystem.entity.SystemUser;

public class UserMapper {
    public static SystemUser toEntity(SystemUserDto systemUserDto) {
        return new SystemUser(systemUserDto.getId(), systemUserDto.getUniversity(), systemUserDto.getYear(), systemUserDto.getGroupNumber(),
                systemUserDto.getEmail(), null);
    }

    public static SystemUserDto toDto(SystemUser user) {
        return new SystemUserDto(user.getId(), user.getUniversity(), user.getYear(), user.getGroupNumber(), user.getEmail());
    }

}
