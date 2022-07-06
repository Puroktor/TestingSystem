package com.dsr.practice.testingsystem.dto;

import com.dsr.practice.testingsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationDto {
    private Integer id;

    private String name;

    private String nickname;

    private String password;

    private Role role;

    private String university;

    private Integer year;

    private Integer groupNumber;

    private String email;
}
