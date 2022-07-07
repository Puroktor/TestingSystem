package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.entity.Answer;

public class AnswerMapper {
    public static Answer toEntity(AnswerDto dto) {
        return new Answer(dto.getId(), dto.getText(), dto.getIsSelected(), null);
    }

    public static AnswerDto toDtoWithoutRight(Answer answer) {
        return new AnswerDto(answer.getId(), answer.getText(), false);
    }

    public static AnswerDto toDtoWithRight(Answer answer) {
        return new AnswerDto(answer.getId(), answer.getText(), answer.getIsRight());
    }

}
