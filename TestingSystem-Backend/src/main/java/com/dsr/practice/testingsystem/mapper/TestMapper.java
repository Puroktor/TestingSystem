package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.FullTestDto;
import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMapper {

    public static TestInfoDto tooInfoDto(Test test) {
        return new TestInfoDto(test.getId(), test.getProgrammingLang(), test.getName(), test.getQuestionsCount());
    }

    public static FullTestDto toFullDto(Test test) {
        TestInfoDto infoDto = tooInfoDto(test);
        List<QuestionDto> shuffledDtoList = new ArrayList<>();
        Collections.shuffle(test.getQuestionsBank());
        for (Question question : test.getQuestionsBank().subList(0, test.getQuestionsCount())) {
            List<AnswerDto> answerDtos = new ArrayList<>();
            for (Answer answer : question.getAnswers()) {
                answerDtos.add(new AnswerDto(answer.getId(), answer.getText(), false));
            }
            QuestionDto questionDto = new QuestionDto(question.getId(), question.getText(), question.getMaxScore(), answerDtos);
            shuffledDtoList.add(questionDto);
        }
        return new FullTestDto(infoDto, shuffledDtoList);
    }

    public static Test toEntity(FullTestDto testDto) {
        Test test = new Test(testDto.getTestInfoDto().getId(), testDto.getTestInfoDto().getProgrammingLang(),
                testDto.getTestInfoDto().getName(), testDto.getTestInfoDto().getQuestionsCount(),
                null, new ArrayList<>());
        for (QuestionDto questionDto : testDto.getQuestionList()) {
            Question question = new Question(questionDto.getId(), test, questionDto.getText(),
                    questionDto.getMaxScore(), new ArrayList<>());
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer(answerDto.getId(), answerDto.getText(), answerDto.getIsSelected(), question);
                question.getAnswers().add(answer);
            }
            test.getQuestionsBank().add(question);
        }
        return test;
    }
}
