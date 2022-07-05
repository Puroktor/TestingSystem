package com.dsr.practice.testingsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginDto {
    private String nickname;

    private String passwordHash;
}
