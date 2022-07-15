package com.dsr.practice.testingsystem.dto;

import com.dsr.practice.testingsystem.entity.TestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

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

    @NotNull(message = "Enter test type")
    private TestType testType;

    @Valid
    @NotNull(message = "Enter at least 1 question")
    @Size(min = 1, message = "Enter at least 1 question")
    private List<QuestionDto> questions;
}
