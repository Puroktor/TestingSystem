package com.dsr.practice.testingsystem.dto;

import com.dsr.practice.testingsystem.entity.Role;
import lombok.Data;

@Data
public class RegistrationResponseDto {
    private Integer id;

    private String name;

    private String nickname;

    private Role role;

    private String university;

    private Integer year;

    private Integer groupNumber;

    private String email;
}
