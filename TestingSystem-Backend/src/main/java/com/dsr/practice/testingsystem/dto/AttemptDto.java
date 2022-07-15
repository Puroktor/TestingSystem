package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class AttemptDto {
    private String nickname;
    private Double score;
    private TestDto test;
    private Map<Integer, Boolean> answerToSubmittedValueMap;
}
