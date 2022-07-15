package com.dsr.practice.testingsystem.dto;

import com.dsr.practice.testingsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    @NotBlank(message = "Enter your name")
    @Size(max = 100, message = "Your name length must be <= 100 characters")
    private String name;

    @NotBlank(message = "Enter your nickname")
    @Size(max = 50, message = "Your nickname length must be <= 50 characters")
    private String nickname;

    @NotBlank(message = "Enter your password")
    @Size(max = 256, message = "Your password length must be <= 256 characters")
    private String password;

    @NotNull(message = "Enter your role")
    private Role role;

    @NotBlank(message = "Enter your university")
    @Size(max = 100, message = "University name length must be <= 100 characters")
    private String university;

    @Min(value = 1, message = "Year must be >= 1")
    @Max(value = 6, message = "Year must be <= 6")
    private Integer year;

    @Min(value = 1, message = "Group number must be >= 1")
    private Integer groupNumber;

    @NotBlank(message = "Enter your email")
    @Size(max = 320, message = "Email length must be <= 320 characters")
    @Email(message = "Not valid email", regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;
}
