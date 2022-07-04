package com.dsr.practice.testingsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionDto {

    private Integer id;

    private String text;

    private Integer maxScore;

    private List<AnswerDto> answers;
}
