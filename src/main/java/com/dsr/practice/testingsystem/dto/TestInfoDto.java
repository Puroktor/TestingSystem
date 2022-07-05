package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestInfoDto {
    private Integer id;

    private String programmingLang;

    private String name;

    private Integer questionsCount;
}
