package com.dsr.practice.testingsystem.controller.mapper;

import com.dsr.practice.testingsystem.controller.dto.AnswerDto;
import com.dsr.practice.testingsystem.controller.dto.QuestionDto;
import com.dsr.practice.testingsystem.controller.dto.FullTestDto;
import com.dsr.practice.testingsystem.controller.dto.TestThemeDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMapper {
    public static TestThemeDto tooThemeDto(Test test) {
        return TestThemeDto.builder()
                .id(test.getId())
                .name(test.getName())
                .programmingLang(test.getProgrammingLang())
                .questionsCount(test.getQuestionsCount())
                .build();
    }

    public static FullTestDto toFullDto(Test test) {
        TestThemeDto themeDto = tooThemeDto(test);
        List<QuestionDto> shuffledDtoList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>(test.getQuestionsBank());
        Collections.shuffle(questionList);
        for (Question question : questionList.subList(0, test.getQuestionsCount())) {
            List<AnswerDto> answerDtos = new ArrayList<>();
            for (Answer answer : question.getAnswers()) {
                answerDtos.add(new AnswerDto(answer.getId(), answer.getText(), false));
            }
            QuestionDto questionDto = new QuestionDto(question.getId(), question.getText(), question.getMaxScore(), answerDtos);
            shuffledDtoList.add(questionDto);
        }
        return new FullTestDto(themeDto, shuffledDtoList);
    }

    public static Test toEntity(FullTestDto testDto) {
        Test test = Test.builder()
                .id(testDto.getTestThemeDto().getId())
                .programmingLang(testDto.getTestThemeDto().getProgrammingLang())
                .name(testDto.getTestThemeDto().getName())
                .questionsCount(testDto.getTestThemeDto().getQuestionsCount())
                .build();
        for (QuestionDto questionDto : testDto.getQuestionList()) {
            Question question = Question.builder()
                    .id(questionDto.getId())
                    .test(test)
                    .maxScore(questionDto.getMaxScore())
                    .text(questionDto.getText())
                    .build();
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = Answer.builder()
                        .id(answerDto.getId())
                        .text(answerDto.getText())
                        .isRight(answerDto.getIsSelected())
                        .question(question).build();
                question.getAnswers().add(answer);
            }
            test.getQuestionsBank().add(question);
        }
        return test;
    }
}
