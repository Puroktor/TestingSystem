package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FullTestDto {

    private TestInfoDto testInfoDto;
    private List<QuestionDto> questionList;
}
