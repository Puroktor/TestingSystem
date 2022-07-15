package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private Integer id;

    @NotBlank(message = "Enter your answer")
    @Size(max = 200, message = "Your answer length must be <= 200 characters")
    private String text;

    @NotNull(message = "Select right answer")
    private Boolean isRight;
}
