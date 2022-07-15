package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Integer id;

    @NotBlank(message = "Enter your question")
    @Size(max = 500, message = "Question length must be <= 500 characters")
    private String text;

    @NotNull(message = "Enter question score")
    @Min(value = 1, message = "Question score must be >= 1")
    private Integer maxScore;

    @Min(value = 0, message = "Question template index must be >= 0")
    private Integer questionTemplateIndex;

    @NotNull(message = "Enter at least 1 answer")
    @Size(min = 1, max = 10, message = "Answer count must be 1-10")
    @Valid
    private List<AnswerDto> answers;
}
