package com.dsr.practice.testingsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AnswerDto {
    private Integer id;

    @NotBlank(message = "Enter your answer")
    @Size(max = 100, message = "Your answer length must be <= 100 characters")
    private String text;

    @NotNull(message = "Select right answer")
    private Boolean isRight;
}
