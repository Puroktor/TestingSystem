package com.dsr.practice.testingsystem.dto;

import lombok.Data;

@Data
public class TestInfoDto {
    private Integer id;
    private String programmingLang;
    private String name;
    private Integer passingScore;
    private Integer questionsCount;
    private Double userScore;
}
