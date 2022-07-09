package com.dsr.practice.testingsystem.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class TestInfoDto {
    private Integer id;

    @NotBlank(message = "Enter programming language")
    @Size(max = 50, message = "Programming language length must be <= 50 characters")
    private String programmingLang;

    @NotBlank(message = "Enter test name")
    @Size(max = 50, message = "Test name length must be <= 50 characters")
    private String name;

    @NotNull(message = "Enter questions count")
    @Min(value = 1, message = "Questions count must be >= 1")
    @Max(value = 50, message = "Questions count must be <= 50")
    private Integer questionsCount;
}
