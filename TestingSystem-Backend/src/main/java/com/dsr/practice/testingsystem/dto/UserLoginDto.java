package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "Enter your nickname")
    @Size(max = 50, message = "Your nickname length must be <= 50 characters")
    private String nickname;

    @NotBlank(message = "Enter your password")
    @Size(max = 256, message = "Your password length must be <= 256 characters")
    private String password;
}
