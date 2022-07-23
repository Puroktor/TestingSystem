package com.dsr.practice.testingsystem.dto;

import com.dsr.practice.testingsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;

    private String name;

    private String nickname;

    private Role role;

    private String university;

    private Integer year;

    private Integer groupNumber;

    private String email;
}
