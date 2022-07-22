package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.AttemptResultDto;
import com.dsr.practice.testingsystem.entity.Attempt;
import com.dsr.practice.testingsystem.entity.Test;
import org.springframework.stereotype.Component;

@Component
public class AttemptMapper {

    public AttemptResultDto toResultDto(Attempt attempt) {
        Test test = attempt.getTest();
        return new AttemptResultDto(attempt.getId(), test.getId(), test.getName(), attempt.getDateTime(),
                attempt.getScore(), attempt.getScore() >= test.getPassingScore());
    }
}
