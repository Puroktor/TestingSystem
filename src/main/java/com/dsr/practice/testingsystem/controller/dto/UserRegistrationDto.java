package com.dsr.practice.testingsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationDto {
    private Integer id;

    private String name;

    private String nickname;

    private String passwordHash;

    private String university;

    private Integer year;

    private Integer groupNumber;

    private String email;
}
