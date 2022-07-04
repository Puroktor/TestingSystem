package com.dsr.practice.testingsystem.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestThemeDto {
    private Integer id;

    private String programmingLang;

    private String name;

    private Integer questionsCount;
}
