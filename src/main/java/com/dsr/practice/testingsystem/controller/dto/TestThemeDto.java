package com.dsr.practice.testingsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestThemeDto {
    private Integer id;

    private String programmingLang;

    private String name;

    private Integer questionsCount;
}
