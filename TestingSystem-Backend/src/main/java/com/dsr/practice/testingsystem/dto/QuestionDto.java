package com.dsr.practice.testingsystem.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class QuestionDto {

    private Integer id;

    @NotBlank(message = "Enter your question")
    @Size(max = 200, message = "Question length must be <= 200 characters")
    private String text;

    @NotNull(message = "Enter question score")
    @Min(value = 1, message = "Question score must be >= 1")
    private Integer maxScore;

    @Min(value = 0, message = "Question template index must be >= 0")
    private Integer questionTemplateIndex;

    @NotNull(message = "Enter at least 1 answer!")
    @Size(min = 1, message = "Enter at least 1 answer!")
    private List<AnswerDto> answers;
}
