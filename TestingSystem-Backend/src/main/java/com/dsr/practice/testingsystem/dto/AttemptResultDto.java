package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private Integer id;
    private Integer testId;
    private String testName;
    private LocalDateTime dateTime;
    private Double score;
    private Boolean hasPassed;
}
