package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDto {
    private Integer userId;
    private String nickname;
    private Double score;
    private LocalDateTime dateTime;
    private TestDto test;
    private Map<Integer, Boolean> answerToSubmittedValueMap;
}
