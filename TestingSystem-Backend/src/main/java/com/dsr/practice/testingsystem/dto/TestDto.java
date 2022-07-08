package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class TestDto {

    @Valid
    private TestInfoDto testInfo;

    @Valid
    @NotNull(message = "Enter at least 1 question")
    @Size(min = 1, message = "Enter at least 1 question")
    private List<QuestionDto> questions;
}
