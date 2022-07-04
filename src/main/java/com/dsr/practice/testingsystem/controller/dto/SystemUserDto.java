package com.dsr.practice.testingsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemUserDto {
    private Integer id;

    private String university;

    private Integer year;

    private Integer groupNumber;

    private String email;
}
